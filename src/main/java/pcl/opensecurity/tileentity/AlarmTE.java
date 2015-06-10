package pcl.opensecurity.tileentity;

import li.cil.oc.api.network.SimpleComponent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class AlarmTE extends TileEntity implements SimpleComponent {

	@Override
	public String getComponentName() {
		// TODO Auto-generated method stub
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
	public void updateEntity() {
		super.updateEntity();
		if (!isPlaying && shouldStart) {
			System.out.println("k");
			//check to see if we are not already playing (to stop infinite amounts playing) and if we should start
			shouldStart = false; //set should start to false to stop us trying to play more
			shouldStop = false; //this is so when we have played and then stopped we can play again...yeah that was a bugger to solve!
			isPlaying = true; //we tell it we are now playing
			if (!this.worldObj.isRemote) {
				System.out.println("Trying to play sound");
				this.worldObj.playSoundEffect((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D, "opensecurity:klaxon1", 5.0F, 1.0F);
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
