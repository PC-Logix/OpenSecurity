package pcl.opensecurity.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * @author Caitlyn
 *
 */
public class TileEntityDisplayPanel extends TileEntityMachineBase {
	@SideOnly(Side.CLIENT)
	private float lineScroll = 0.0f;

	public String getScreenText() {
		// TODO Auto-generated method stub
		return "DEBUGGING BLOCK PLEASE IGNORE";
	}

	public int getScreenColor() {
		// TODO Auto-generated method stub
		return 0xFFFFFF;
	}

	@SideOnly(Side.CLIENT)
	public float getLineScroll() {
		return lineScroll;
	}

	@SideOnly(Side.CLIENT)
	public void scrollLines(float amount) {
		lineScroll += amount;
	}

	@SideOnly(Side.CLIENT)
	public void setLineScroll(float lineScroll) {
		this.lineScroll = lineScroll;
	}
}