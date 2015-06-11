package pcl.opensecurity.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import pcl.opensecurity.client.sounds.AlarmSoundHandler;
import li.cil.oc.api.network.SimpleComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityAlarm extends TileEntity implements SimpleComponent {

	@Override
	public String getComponentName() {
		return "OSAlarm";
	}

	private boolean isPlaying = false; //when the tile entity is created we want to have it not playing
	private boolean shouldStart = false; //the trigger we will use to start the sound
	private boolean shouldStop = false;
	private boolean computerOverride = false;

	public boolean isShouldStop() { //so we can see if we should stop this method allows checking for it
		return shouldStop;
	}

    public void setShouldStart(boolean shouldStart) { //this is what we call from our block and set to true to play sound
        this.shouldStart = shouldStart;
        System.out.println("Starting: " + shouldStart);
    }

    public void setShouldStop(boolean shouldStop) { //we call this to stop the sound..well to set it up to stop
        if (isPlaying && !computerOverride) { //we make sure sound is playing, otherwise a bug in the way minecraft deals with block updates causes it to start and stop immediatly
            isPlaying = false; //since were stopping it we set this to false to say the sound is no longer playing
            this.shouldStop = shouldStop;
            System.out.println("Stopping: " + shouldStop);
        }
    }

	public boolean isPlaying() { //we use this to allow other classes to see if were playing or not
		return isPlaying;
	}


	@Override
	@SideOnly(Side.CLIENT)
	public void updateEntity() {
		super.updateEntity();
		if (!this.worldObj.isRemote) {
			if (!isPlaying && shouldStart) {
				//check to see if we are not already playing (to stop infinite amounts playing) and if we should start
				shouldStart = false; //set should start to false to stop us trying to play more
				shouldStop = false; //this is so when we have played and then stopped we can play again...yeah that was a bugger to solve!
				isPlaying = true; //we tell it we are now playing
	            AlarmSoundHandler alarm = new AlarmSoundHandler(worldObj.getTileEntity(xCoord, yCoord, zCoord), "klaxon1", 2.0F);
	            Minecraft.getMinecraft().getSoundHandler().playSound(alarm);
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound par1nbtTagCompound)
	{
		super.writeToNBT(par1nbtTagCompound);
	}

	@Override
	public void readFromNBT(NBTTagCompound par1nbtTagCompound)
	{
		super.readFromNBT(par1nbtTagCompound);
	}    
}
