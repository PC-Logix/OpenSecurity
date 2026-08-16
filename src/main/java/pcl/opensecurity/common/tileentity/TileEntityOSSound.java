package pcl.opensecurity.common.tileentity;

import li.cil.oc.api.network.EnvironmentHost;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.client.sounds.MachineSound;

public class TileEntityOSSound extends TileEntityOSBase {
    @SideOnly(Side.CLIENT)
    private MachineSound sound;

    private Boolean shouldPlay = false;
    private ResourceLocation soundRes;
    private float volume = 1.0F;
    private String soundName = "";

    TileEntityOSSound(String name){
        super(name);
    }

    TileEntityOSSound(String name, EnvironmentHost host){
        super(name, host);
    }

    @Override
    public void invalidate(){
        if(getWorld() != null && getWorld().isRemote)
            stopSound();

        setShouldPlay(false);

        super.invalidate();
    }

    @Override
    public void onChunkUnload() {
        if(getWorld() != null && getWorld().isRemote)
            stopSound();

        super.onChunkUnload();
    }

    @Override
    public void update() {
        super.update();

        if(getWorld().isRemote)
            updateSound();
    }

    @SideOnly(Side.CLIENT)
    private void updateSound() {
        if (!hasSound() || isUpgrade)
            return;

        if (getShouldPlay()) {
            playSoundNow();
        } else
            stopSound();
    }

    @SideOnly(Side.CLIENT)
    private void stopSound() {
        if(sound != null) {
            sound.endPlaying();
            sound = null;
        }
    }

    @SideOnly(Side.CLIENT)
    void playSoundNow() {
        if(sound == null) {
            sound = new MachineSound(getSoundRes(), getPos(), getVolume(), getPitch(), shouldRepeat());
            FMLClientHandler.instance().getClient().getSoundHandler().playSound(sound);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        float oldVolume = volume;
        super.readFromNBT(tag);
        isUpgrade = tag.getBoolean("isUpgrade");
        setSound(tag.getString("soundName"));
        setVolume(tag.getFloat("volume"));
        setShouldPlay(tag.getBoolean("shouldPlay"));

        if(getWorld() != null && getWorld().isRemote && sound != null &&
                (!getShouldPlay() || oldVolume != volume))
            stopSound();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag.setBoolean("isUpgrade", isUpgrade);
        tag.setBoolean("shouldPlay", getShouldPlay());
        tag.setString("soundName", getSoundName());
        tag.setFloat("volume", getVolume());
        return super.writeToNBT(tag);
    }

    void setSound(String sound) {
        boolean changed = !soundName.equals(sound);
        soundName = sound;
        soundRes = new ResourceLocation(OpenSecurity.MODID, sound);

        if(changed && getWorld() != null && getWorld().isRemote)
            stopSound();
    }

    String getSoundName() {
        return soundName;
    }

    private ResourceLocation getSoundRes() {
        return soundRes;
    }

    private boolean getShouldPlay() {
        return shouldPlay;
    }

    public void setShouldPlay(boolean b) {
        if(shouldPlay == b)
            return;

        shouldPlay = b;
        if(!b && getWorld() != null && getWorld().isRemote)
            stopSound();

        if(!isUpgrade && getWorld() != null && !getWorld().isRemote) {
            markSoundStateDirty();
        }
    }

    void markSoundStateDirty() {
        if(!isUpgrade && getWorld() != null && !getWorld().isRemote) {
            getWorld().notifyBlockUpdate(getPos(), getWorld().getBlockState(getPos()), getWorld().getBlockState(getPos()), 3);
            markDirty();
        }
    }

    private boolean hasSound() {
        return getSoundName().length() > 0;
    }

    private float getPitch() {
        return 1.0f;
    }

    private boolean shouldRepeat() {
        return getShouldPlay();
    }

    void setVolume(float vol){
        volume = vol;
    }

    float getVolume() {
        return volume;
    }

}
