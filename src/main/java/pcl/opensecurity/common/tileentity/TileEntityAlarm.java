package pcl.opensecurity.common.tileentity;

import java.io.File;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.SimpleComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.client.sounds.ISoundTile;

public class TileEntityAlarm extends TileEntityMachineBase implements SimpleComponent, ISoundTile {
	public static String cName = "OSAlarm";
	public Boolean shouldPlay = false;
	public String soundName = "klaxon1";
	public float volume = 1.0F;
	public Boolean computerPlaying = false;
	
	public TileEntityAlarm() {
		super();
		setSound(soundName);
	}

	@Override
	public String getComponentName() {
		return "os_alarm";
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
	}

	@Override
	public boolean shouldPlaySound() {
		return shouldPlay;
	}

	@Override
	public String getSoundName() {
		return soundName;
	}

	@Override
	public float getVolume() {
		return volume;
	}

	@Override
	public ResourceLocation setSound(String sound) {
		setSoundRes(new ResourceLocation(OpenSecurity.MODID + ":" + sound));
		return getSoundRes();
	}

	public void setShouldStart(boolean b) {
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		getDescriptionPacket();
		shouldPlay = true;

	}

	public void setShouldStop(boolean b) {
		shouldPlay = false;
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		getDescriptionPacket();
	}
	
	// OC Methods.

	@Callback
	public Object[] greet(Context context, Arguments args) {
		return new Object[] { "Lasciate ogne speranza, voi ch'entrate" };
	}

	@Callback(doc = "function(range:integer):string; Sets the range in blocks of the alarm", direct = true)
	public Object[] setRange(Context context, Arguments args) {
		Float newVolume = (float) args.checkInteger(0);
		if (newVolume >= 15 && newVolume <= 150) {
			volume = newVolume / 15 + 0.5F;
			return new Object[] { "Success" };
		} else {
			return new Object[] { "Error, range should be between 15-150" };
		}
	}

	@Callback(doc = "function(soundName:string):string; Sets the alarm sound", direct = true)
	public Object[] setAlarm(Context context, Arguments args) {
		String alarm = args.checkString(0);
		File f = new File("mods"+File.separator+"OpenSecurity"+File.separator+"sounds"+File.separator+"alarms"+File.separator+ alarm + ".ogg");
		if (f.exists() && !f.isDirectory()) {
			soundName = alarm;
			setSound(alarm);
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			getDescriptionPacket();
			return new Object[] { "Success" };
		} else {
			return new Object[] { "Fail" };
		}
	}

	@Callback(doc = "function():string; Activates the alarm", direct = true)
	public Object[] activate(Context context, Arguments args) {
		this.setShouldStart(true);
		computerPlaying = true;
		return new Object[] { "Ok" };
	}

	@Callback(doc = "function():table; Returns a table of Alarm Sounds", direct = true)
	public Object[] listSounds(Context context, Arguments args) {
		return new Object[] { OpenSecurity.alarmList };
	}

	@Callback(doc = "function(int:x, int:y, int:z, string:sound, float:range(1-10 recommended)):string; Plays sound at x y z", direct = true)
	public Object[] playSoundAt(Context context, Arguments args) {
		if (OpenSecurity.enableplaySoundAt) {
			double x = args.checkDouble(0);
			double y = args.checkDouble(1);
			double z = args.checkDouble(2);
			String sound = args.checkString(3);
			float range = args.checkInteger(4);
			worldObj.playSound(x, y, z, new SoundEvent(new ResourceLocation(sound)), SoundCategory.BLOCKS, range / 15 + 0.5F, 1.0F, false);
			return new Object[] { "Ok" };
		} else {
			return new Object[] { "Disabled" };
		}
	}

	@Callback(doc = "function():string; Deactivates the alarm", direct = true)
	public Object[] deactivate(Context context, Arguments args) {
		this.setShouldStop(true);
		computerPlaying = false;
		return new Object[] { "Ok" };
	}

	@Override
	public boolean playSoundNow() {
		// TODO Auto-generated method stub
		return false;
	}
}
