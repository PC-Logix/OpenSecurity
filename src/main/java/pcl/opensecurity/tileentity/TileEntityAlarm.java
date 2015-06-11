package pcl.opensecurity.tileentity;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.client.sounds.MachineSound;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class TileEntityAlarm extends TileEntityMachineBase {
	public String cName;
	public Boolean shouldPlay = false;
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
		return "klaxon1";
	}

	public void setShouldStart(boolean b) {
		shouldPlay = true;
		
	}

	public void setShouldStop(boolean b) {
		shouldPlay = false;
	}
	
}
