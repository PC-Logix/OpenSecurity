package pcl.opensecurity.blockentity;

import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.Config;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public final class AlarmBlockEntity extends SecurityBlockEntity {
    private static final String DEFAULT_SOUND = "klaxon1";

    private boolean active;
    // Preserve the old alarm semantics: this is really the sound volume value used
    // by Minecraft to extend audible range, with the OC-facing range being volume - 0.5.
    private float volume = 15.0F;
    private String sound = DEFAULT_SOUND;

    // Client-only runtime state, deliberately typed to a common nested interface so
    // this block entity never needs to link against net.minecraft.client classes.
    private transient PlaybackHandle playback;

    public AlarmBlockEntity(BlockPos pos, BlockState state) {
        super(OpenSecurity.ALARM_BE.get(), pos, state, "os_alarm", 32);
    }

    @Callback(direct = true, doc = "function(range:integer):boolean,number -- Sets the range in blocks of the alarm.")
    public Object[] setRange(Context context, Arguments args) {
        if (args.count() == 0) {
            return new Object[]{false, "missing arguments"};
        }

        volume = Math.max(0.0F, Math.min(args.checkInteger(0) + 0.5F, Config.alarmMaxRange()));
        syncState();
        return new Object[]{true, volume - 0.5F};
    }

    @Callback(direct = true, doc = "function(soundName:string):boolean -- Sets the alarm sound. Unqualified names use the opensecurity namespace.")
    public Object[] setAlarm(Context context, Arguments args) {
        if (args.count() == 0) {
            return new Object[]{false, "missing arguments"};
        }

        String requested = args.checkString(0).trim();
        if (requested.isEmpty() || resolveSound(requested) == null || !Config.isAllowedAlarmSound(requested)) {
            return new Object[]{false, "invalid sound name"};
        }

        if (!sound.equals(requested)) {
            sound = requested;
            syncState();
        }
        return new Object[]{true};
    }

    @Callback(direct = true, doc = "function():boolean -- Activates the alarm.")
    public Object[] activate(Context context, Arguments args) {
        if (!active) {
            active = true;
            syncState();
        }
        return new Object[]{true};
    }

    @Callback(direct = true, doc = "function():boolean -- Deactivates the alarm.")
    public Object[] deactivate(Context context, Arguments args) {
        if (active) {
            active = false;
            syncState();
        }
        return new Object[]{true};
    }

    @Callback(direct = true, doc = "function():table -- Lists bundled and configured custom alarm sounds available on this server.")
    public Object[] listSounds(Context context, Arguments args) {
        return new Object[]{Config.availableAlarmSounds().toArray(String[]::new)};
    }

    @Callback(direct = true, doc = "function(x:number,y:number,z:number[,sound:string,range:number]):boolean,string -- Plays a configured sound relative to the alarm when enabled in the common config.")
    public Object[] playSoundAt(Context context, Arguments args) {
        if (!Config.enablePlaySoundAt()) return new Object[]{false, "Feature disabled in configuration"};
        if (args.count() < 3) return new Object[]{false, "missing arguments"};
        if (level == null) return new Object[]{false, "world is unavailable"};

        BlockPos position = worldPosition.offset(
                (int) Math.floor(args.checkDouble(0)),
                (int) Math.floor(args.checkDouble(1)),
                (int) Math.floor(args.checkDouble(2)));
        String requested = args.optString(3, sound).trim();
        ResourceLocation soundId = resolveSound(requested);
        if (soundId == null || !Config.isAllowedAlarmSound(requested)) return new Object[]{false, "invalid or unconfigured sound name"};

        float requestedRange = (float) args.optDouble(4, Math.max(0.0F, volume - 0.5F));
        float playVolume = Math.max(0.0F, Math.min(requestedRange + 0.5F, Config.alarmMaxRange()));
        level.playSound(null, position, SoundEvent.createVariableRangeEvent(soundId), SoundSource.BLOCKS, playVolume, 1.0F);
        return new Object[]{true};
    }

    public boolean isActive() {
        return active;
    }

    public float getVolume() {
        return volume;
    }

    public String getSoundName() {
        return sound;
    }

    public ResourceLocation getSoundId() {
        ResourceLocation resolved = resolveSound(sound);
        return resolved != null ? resolved : OpenSecurity.id(DEFAULT_SOUND);
    }

    public PlaybackHandle getPlayback() {
        return playback;
    }

    public void setPlayback(PlaybackHandle playback) {
        this.playback = playback;
    }

    private static ResourceLocation resolveSound(String name) {
        return name.indexOf(':') >= 0
                ? ResourceLocation.tryParse(name)
                : ResourceLocation.tryBuild(OpenSecurity.MOD_ID, name);
    }

    private void syncState() {
        setChanged();
        if (level != null && !level.isClientSide) {
            BlockState state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_CLIENTS);
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, provider);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void setRemoved() {
        if (playback != null) {
            playback.stopPlayback();
            playback = null;
        }
        super.setRemoved();
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putBoolean("active", active);
        tag.putFloat("volume", volume);
        tag.putString("sound", sound);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        active = tag.getBoolean("active");
        volume = tag.contains("volume")
                ? Math.max(0.0F, Math.min(Config.alarmMaxRange(), tag.getFloat("volume")))
                : Config.alarmMaxRange();

        String loadedSound = tag.getString("sound");
        sound = loadedSound.isBlank() || resolveSound(loadedSound) == null || !Config.isAllowedAlarmSound(loadedSound)
                ? DEFAULT_SOUND : loadedSound;
    }

    /**
     * Common-side handle for the client sound instance. Keeping this interface free of
     * client classes lets the block entity stop playback on removal without making the
     * dedicated server load client-only Minecraft classes.
     */
    public interface PlaybackHandle {
        ResourceLocation soundId();
        boolean isStopped();
        void stopPlayback();
    }
}
