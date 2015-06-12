package pcl.opensecurity.tileentity;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import pcl.opensecurity.OpenSecurity;

public class TileEntityAlarm extends TileEntityMachineBase {
	public static String cName = "OSAlarm";
	public Boolean shouldPlay = false;
	public String alarmName = "klaxon1";

	public TileEntityAlarm() {
		super(cName);
		setSound(alarmName);
	}

	@Override
	public String getComponentName() {
		return "OSAlarm";
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
		return alarmName;
		
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
	
}
