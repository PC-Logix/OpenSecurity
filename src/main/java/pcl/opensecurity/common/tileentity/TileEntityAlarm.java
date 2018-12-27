package pcl.opensecurity.common.tileentity;

import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Visibility;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.client.sounds.ISoundTile;

public class TileEntityAlarm extends TileEntityOSSound implements ISoundTile {

	public TileEntityAlarm() {
		super("os_alarm");
		setSound("klaxon1");
		node = Network.newNode(this, Visibility.Network).withComponent(getComponentName()).withConnector(32).create();
	}

	// OC Methods.
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
		setSound(alarm);
		getUpdateTag();
		markDirty();
		return new Object[] { "Success" };
	}

	@Callback(doc = "function():string; Activates the alarm", direct = true)
	public Object[] activate(Context context, Arguments args) {
		this.setShouldStart(true);
		computerPlaying = true;
		setShouldPlay(true);
		return new Object[] { "Ok" };
	}

	@Callback(doc = "function():string; Deactivates the alarm", direct = true)
	public Object[] deactivate(Context context, Arguments args) {
		this.setShouldStop(true);
		computerPlaying = false;
		setShouldPlay(false);
		return new Object[] { "Ok" };
	}

	@Callback(doc = "function(int:x, int:y, int:z, string:sound, float:range(1-10 recommended)):string; Plays sound at x y z", direct = true)
	public Object[] playSoundAt(Context context, Arguments args) {
		if (OpenSecurity.enableplaySoundAt) {
			double x = args.checkDouble(0);
			double y = args.checkDouble(1);
			double z = args.checkDouble(2);
			String sound = args.checkString(3);
			float range = args.checkInteger(4);
			world.playSound(x, y, z, new SoundEvent(new ResourceLocation(sound)), SoundCategory.BLOCKS, range / 15 + 0.5F, 1.0F, false);
			getUpdateTag();
			markDirty();
			return new Object[] { "Ok" };
		} else {
			return new Object[] { "Disabled" };
		}
	}

}
