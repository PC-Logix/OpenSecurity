package pcl.opensecurity.client;

import pcl.opensecurity.blockentity.AlarmBlockEntity;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

/** One continuously-looping client sound bound to one alarm block entity. */
public final class AlarmSoundInstance extends AbstractTickableSoundInstance implements AlarmBlockEntity.PlaybackHandle {
    private final AlarmBlockEntity alarm;
    private final ResourceLocation soundId;

    public AlarmSoundInstance(AlarmBlockEntity alarm) {
        super(SoundEvent.createVariableRangeEvent(alarm.getSoundId()), SoundSource.BLOCKS, RandomSource.create());
        this.alarm = alarm;
        this.soundId = alarm.getSoundId();

        this.looping = true;
        this.delay = 0;
        this.relative = false;
        this.pitch = 1.0F;
        updateFromAlarm();
    }

    @Override
    public void tick() {
        if (alarm.isRemoved() || alarm.getLevel() == null || !alarm.isActive()
                || !soundId.equals(alarm.getSoundId())) {
            stop();
            return;
        }

        updateFromAlarm();
    }

    private void updateFromAlarm() {
        this.x = alarm.getBlockPos().getX() + 0.5D;
        this.y = alarm.getBlockPos().getY() + 0.5D;
        this.z = alarm.getBlockPos().getZ() + 0.5D;
        this.volume = alarm.getVolume();
    }

    @Override
    public ResourceLocation soundId() {
        return soundId;
    }


    @Override
    public void stopPlayback() {
        stop();
    }
}
