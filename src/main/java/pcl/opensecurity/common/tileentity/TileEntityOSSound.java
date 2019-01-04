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

    public TileEntityOSSound(String name){
        super(name);
    }

    public TileEntityOSSound(String name, EnvironmentHost host){
        super(name, host);
    }
    
    @Override
    public void update() {
        super.update();
        if (!hasSound())
            return;

        if(!isUpgrade && getWorld().isRemote)
            updateSound();

    }    

    @SideOnly(Side.CLIENT)
    private void updateSound() {
        if (!hasSound())
            return;

        if (getShouldPlay()) {
            playSoundNow();
        } else if (sound != null) {
            sound.endPlaying();
            sound = null;
        }
    }

    @SideOnly(Side.CLIENT)
    public void playSoundNow() {
        if(sound == null)
            sound = new MachineSound(soundRes, getPos(), getVolume(), getPitch(), shouldRepeat());

        FMLClientHandler.instance().getClient().getSoundHandler().playSound(sound);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        isUpgrade = tag.getBoolean("isUpgrade");
        shouldPlay = tag.getBoolean("shouldPlay");
        setSound(tag.getString("soundName"));
        setVolume(tag.getFloat("volume"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setBoolean("isUpgrade", isUpgrade);
        tag.setBoolean("shouldPlay", getShouldPlay());
        tag.setString("soundName", soundName);
        tag.setFloat("volume", volume);
        return tag;
    }

    public ResourceLocation setSound(String sound) {
        soundName = sound;
        soundRes = new ResourceLocation(OpenSecurity.MODID + ":" + sound);
        return getSoundRes();
    }

    public String getSoundName() {
        return soundName;
    }

    private ResourceLocation getSoundRes() {
        return soundRes;
    }

    public boolean getShouldPlay() {
        return shouldPlay;
    }

    public void setShouldPlay(boolean b) {
        shouldPlay = b;
        if(!isUpgrade) {
            getWorld().notifyBlockUpdate(getPos(), getWorld().getBlockState(getPos()), getWorld().getBlockState(getPos()), 3);
            getUpdateTag();
            markDirty();
        }
    }

    public boolean hasSound() {
        return getSoundName().length() > 0;
    }

    public float getPitch() {
        return 1.0f;
    }

    public boolean shouldRepeat() {
        return getShouldPlay();
    }

    public void setVolume(float vol){
        volume = vol;
    }

    public float getVolume() {
        return volume;
    }


}
