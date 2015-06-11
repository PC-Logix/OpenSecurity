package pcl.opensecurity.tileentity;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import pcl.opensecurity.OpenSecurity;

public class TileEntityAlarm extends TileEntityMachineBase {
	public String cName;
	public Boolean shouldPlay = false;
	public String alarmName = "klaxon1";
	public TileEntityAlarm(String componentName) {
		super(componentName);
		cName = componentName;
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
		System.out.println("Fak " + this.alarmName);
		return this.alarmName;
		
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
			this.alarmName = alarm;
			return new Object[] { "Success" };
		} else {
			return new Object[] { "Fail" };
		}
	}
	
}
