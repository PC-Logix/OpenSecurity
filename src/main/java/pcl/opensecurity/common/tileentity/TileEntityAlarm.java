package pcl.opensecurity.common.tileentity;

import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.EnvironmentHost;
import li.cil.oc.api.network.Visibility;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import pcl.opensecurity.Config;
import pcl.opensecurity.OpenSecurity;

public class TileEntityAlarm extends TileEntityOSSound {
	public static float volumeMax = 15; //range in BLOCKS!!!111

	public TileEntityAlarm() {
		super("os_alarm");
		setSound("klaxon1");
		node = Network.newNode(this, Visibility.Network).withComponent(getComponentName()).withConnector(32).create();
	}

	public TileEntityAlarm(EnvironmentHost host){
		super("os_alarm", host);
	}

	// OC Methods.
	@Callback(doc = "function(range:integer):string; Sets the range in blocks of the alarm", direct = true)
	public Object[] setRange(Context context, Arguments args) {
		if(args.count() == 0)
			return new Object[] { false, "missing arguments" };

		setVolume(Math.max(0, Math.min(args.checkInteger(0) + 0.5F, volumeMax)));

		return new Object[] { true, getVolume() - 0.5f };
	}

	@Callback(doc = "function(soundName:string):string; Sets the alarm sound", direct = true)
	public Object[] setAlarm(Context context, Arguments args) {
		if(args.count() == 0)
			return new Object[] { false, "missing arguments" };

		String sound = args.checkString(0);

		//if (!sound.equals("alarms/klaxon1") && !sound.equals("alarms/klaxon2"))
		//	return new Object[] { false, "sound file doesnt exist" };

		setSound(sound);

		if(!isUpgrade) {
			getUpdateTag();
			markDirty();
		}
		return new Object[] { true };
	}

	@Callback(doc = "function():string; Activates the alarm", direct = true)
	public Object[] activate(Context context, Arguments args) {
		setShouldPlay(true);
		return new Object[] { true };
	}

	@Callback(doc = "function():string; Deactivates the alarm", direct = true)
	public Object[] deactivate(Context context, Arguments args) {
		setShouldPlay(false);
		return new Object[] { true };
	}

	@Callback(doc = "function():array; returns a list of the available sounds", direct = true)
	public Object[] listSounds(Context context, Arguments args) {
		return new Object[] { OpenSecurity.alarmList.toArray() };
	}

	@Callback(doc = "function(int:x, int:y, int:z [, string:sound, float:range(1-15 recommended)]):string; Plays sound at x y z", direct = true)
	public Object[] playSoundAt(Context context, Arguments args) {
		if (!Config.getConfig().getCategory("general").get("enableplaySoundAt").getBoolean())
			return new Object[] { false, "Feature disabled in configuration" };

		if(args.count() < 3)
			return new Object[] { false, "missing arguments" };

		BlockPos alarmPosition = new BlockPos(args.checkDouble(0), args.checkDouble(1), args.checkDouble(2)).add(getPos());

		String sound = args.optString(3, getSoundName());

		//if (!sound.equals("klaxon1") && !sound.equals("klaxon2"))
		//	return new Object[] { false, "sound file doesnt exist" };

		float range = (float) Math.max(0, Math.min(args.optDouble(4, getVolume()-0.5F) + 0.5F, volumeMax));

		getWorld().playSound(null, alarmPosition, new SoundEvent(new ResourceLocation(sound)), SoundCategory.BLOCKS, range, 1.0F);

		if(!isUpgrade) {
			getUpdateTag();
			markDirty();
		}

		return new Object[] { true };
	}

}
