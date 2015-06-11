package pcl.opensecurity.tileentity;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.client.sounds.AlarmSoundHandler;
import pcl.opensecurity.client.sounds.IShouldLoop;
import li.cil.oc.api.network.SimpleComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class TileEntityAlarm extends TileEntity implements IShouldLoop, SimpleComponent {

	@Override
	public String getComponentName() {
		return "OSAlarm";
	}
	
	private boolean isPlaying = false;
	private boolean shouldStart = false;
	private boolean shouldStop = false;
	private boolean computerOverride = false;
	private AlarmSoundHandler alarm = null;
	public boolean isShouldStop() {
		return shouldStop;
	}

	public void setShouldStart(boolean shouldStart) {
		this.shouldStart = shouldStart;
		System.out.println("Starting: " + this.shouldStart);
	}

	public void setShouldStop(boolean shouldStop) {
		if (isPlaying && !computerOverride) {
			isPlaying = false;
			this.shouldStop = shouldStop;
			isDonePlaying();
			System.out.println("Stopping: " + shouldStop);
		}
	}

	public boolean isPlaying() {
		return isPlaying;
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		if (worldObj.isRemote) {
			if (!isPlaying && shouldStart) {
				shouldStart = false;
				shouldStop = false;
				isPlaying = true;
				alarm = new AlarmSoundHandler(new ResourceLocation(OpenSecurity.MODID + ":" + "klaxon1"), this);
				playSound();
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void playSound() {
		FMLClientHandler.instance().getClient().getSoundHandler().playSound(alarm);
	}

	@SideOnly(Side.CLIENT)
	public void isDonePlaying() {
		alarm.shouldBePlaying = false;
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

	@Override
	public boolean continueLoopingAudio() {
		return true;
	}  
}
