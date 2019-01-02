package pcl.opensecurity.common.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.client.sounds.ISoundTile;
import pcl.opensecurity.client.sounds.MachineSound;

public class TileEntityOSSound extends TileEntityOSBase {
    @SideOnly(Side.CLIENT)
    protected MachineSound sound;
    protected Boolean shouldPlay = false;
    protected ResourceLocation soundRes;
    protected float volume = 1.0F;
    protected Boolean computerPlaying = false;
    protected String soundName = "";

    public TileEntityOSSound(String name){
        super(name);
    }

    @Override
    public void update(){
        super.update();
        if (world.isRemote && hasSound()) {
            updateSound();
        }
    }

    @SideOnly(Side.CLIENT)
    public void updateSound() {
        if (hasSound()) {
            if ((getShouldPlay()) && !isInvalid()) {
                if (sound == null && this instanceof ISoundTile) {
                    ISoundTile tile = (ISoundTile) this;
                    soundRes = new ResourceLocation("opensecurity:" + tile.getSoundName());
                    sound = new MachineSound(soundRes, this.getPos(), getVolume(), getPitch(), shouldRepeat());
                    FMLClientHandler.instance().getClient().getSoundHandler().playSound(sound);
                }
            } else if (sound != null) {
                sound.endPlaying();
                sound = null;
            }
        }
    }


    public boolean playSoundNow() {
        return false;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        setShouldPlay(tag.getBoolean("isPlayingSound"));
        soundName = tag.getString("alarmName");
        volume = tag.getFloat("volume");
        computerPlaying = tag.getBoolean("computerPlaying");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setBoolean("isPlayingSound", getShouldPlay());
        tag.setString("alarmName", soundName);
        tag.setFloat("volume", volume);
        tag.setBoolean("computerPlaying", computerPlaying);
        return tag;
    }


    public void setShouldStart(boolean b) {
        setShouldPlay(true);
        world.notifyBlockUpdate(this.pos, world.getBlockState(this.pos), world.getBlockState(this.pos), 3);
        getUpdateTag();
        markDirty();
    }

    public void setShouldStop(boolean b) {
        setShouldPlay(false);
        world.notifyBlockUpdate(this.pos, world.getBlockState(this.pos), world.getBlockState(this.pos), 3);
        getUpdateTag();
        markDirty();
    }

    public void setSoundRes(ResourceLocation soundRes) {
        this.soundRes = soundRes;
    }

    public ResourceLocation setSound(String sound) {
        setSoundRes(new ResourceLocation(OpenSecurity.MODID + ":" + sound));
        return getSoundRes();
    }

    public float getVolume() {
        return volume;
    }

    public String getSoundName() {
        return soundName;
    }

    public ResourceLocation getSoundRes() {
        return soundRes;
    }

    public boolean getShouldPlay() {
        return shouldPlay;
    }

    public void setShouldPlay(boolean b) {
        shouldPlay = b;
    }

    public boolean hasSound() {
        return getSoundName() != null;
    }

    public float getPitch() {
        return 1.0f;
    }

    public boolean shouldRepeat() {
        return getShouldPlay();
    }

    public boolean shouldPlaySound() {
        return false;
    }
}
