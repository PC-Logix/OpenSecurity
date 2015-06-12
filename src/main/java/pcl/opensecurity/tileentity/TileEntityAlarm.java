package pcl.opensecurity.tileentity;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.SimpleComponent;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.client.sounds.MachineSound;

public class TileEntityAlarm extends TileEntity implements SimpleComponent  {
	public static String cName = "OSAlarm";
	public Boolean shouldPlay = false;
	public String alarmName = "klaxon1";
	private ResourceLocation soundRes;
	
	public TileEntityAlarm() {
		super();
		setSound(alarmName);
	}

	@Override
	public String getComponentName() {
		return "OSAlarm";
	}
	
	
	public boolean shouldPlaySound() {
		return shouldPlay;
	}
	
	public String getSoundName() {
		return alarmName;
	}

	public ResourceLocation setSound(String sound) {
		
		setSoundRes(new ResourceLocation(OpenSecurity.MODID + ":" + sound));
		return getSoundRes();
	}
	
	public void setShouldStart(boolean b) {
		shouldPlay = true;
		
	}

	public void setShouldStop(boolean b) {
		shouldPlay = false;
	}
	
	
	//OC Methods.
	
	@Callback
	public Object[] greet(Context context, Arguments args) {
		return new Object[] { "Lasciate ogne speranza, voi ch'intrate" };
	}
	
	@Callback
	public Object[] setAlarm(Context context, Arguments args) {
		String alarm = args.checkString(0);
		if (OpenSecurity.alarmList.contains(alarm)) {
			alarmName = alarm;
			setSound(alarm);
			return new Object[] { "Success" };
		} else {
			return new Object[] { "Fail" };
		}
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		if(worldObj.isRemote && hasSound()) {
			updateSound();
		}
	}
	
	// Sound related, thanks to EnderIO code for this!

	@SideOnly(Side.CLIENT)
	private MachineSound sound;

	public ResourceLocation getSoundRes() {
		return soundRes;
	}

	public boolean hasSound() {
		return getSoundName() != null;
	}

	public float getVolume() {
		return 1.0f;
	}

	public float getPitch() {
		return 1.0f;
	}

	public boolean shouldRepeat() {
		return true;
	}

	@SideOnly(Side.CLIENT)
	public void updateSound() {
		if(hasSound()) {
			if(shouldPlaySound() && !isInvalid()) {
				if(sound == null) {
					sound = new MachineSound(getSoundRes(), xCoord + 0.5f, yCoord + 0.5f, zCoord + 0.5f, getVolume(), getPitch(), shouldRepeat());
					FMLClientHandler.instance().getClient().getSoundHandler().playSound(sound);
				}
			} else if(sound != null) {
				sound.endPlaying();
				sound = null;
			}
		}
	}
	
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);

	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
	}

	public void setSoundRes(ResourceLocation soundRes) {
		this.soundRes = soundRes;
	}
	
}
