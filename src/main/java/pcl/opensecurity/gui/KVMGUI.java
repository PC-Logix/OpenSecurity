package pcl.opensecurity.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import pcl.opensecurity.OSPacketHandler;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.containers.KVMContainer;
import pcl.opensecurity.tileentity.TileEntityKVM;

public class KVMGUI extends GuiContainer {

	TileEntityKVM te;

	private GuiButton frontBttn;
	private GuiButton backBttn;
	private GuiButton leftBttn;
	private GuiButton rightBttn;
	private GuiButton topBttn;
	private GuiButton bottomBttn;

	@SuppressWarnings("unchecked")
	@Override
	public void initGui() {
		super.initGui();
		this.guiLeft = (this.width - this.xSize) / 2;
		this.guiTop = (this.height - this.ySize) / 2;
		this.backBttn = new GuiButton(2, (width / 2) - 85, (height / 2) - 70, ((xSize - 8) / 2) - 2, 20, "Back");
		this.leftBttn = new GuiButton(5, (width / 2) + 2, (height / 2) - 70, ((xSize - 8) / 2) - 2, 20, "Left");
		this.frontBttn = new GuiButton(3, (width / 2) - 85, (height / 2) - 40, ((xSize - 8) / 2) - 2, 20, "Front");
		this.rightBttn = new GuiButton(4, (width / 2) + 2, (height / 2) - 40, ((xSize - 8) / 2) - 2, 20, "Right");
		this.topBttn = new GuiButton(1, (width / 2) - 85, (height / 2) - 10, ((xSize - 8) / 2) - 2, 20, "Top");
		this.bottomBttn = new GuiButton(0, (width / 2) + 2, (height / 2) - 10, ((xSize - 8) / 2) - 2, 20, "Bottom");
		this.buttonList.add(new GuiButton(6, (width / 2) + 30, (height / 2) + 50, 50, 20, "Close"));
		this.buttonList.add(backBttn);
		this.buttonList.add(leftBttn);
		this.buttonList.add(frontBttn);
		this.buttonList.add(rightBttn);
		this.buttonList.add(topBttn);
		this.buttonList.add(bottomBttn);
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}


	@Override
	public void actionPerformed(GuiButton button)
	{
		switch(button.id) {
		case 0:
			//bottom
			OpenSecurity.network.sendToServer(new OSPacketHandler(0, te.xCoord, te.yCoord, te.zCoord, 1));
			break;
		case 1:
			//top
			OpenSecurity.network.sendToServer(new OSPacketHandler(1, te.xCoord, te.yCoord, te.zCoord, 1));
			break;
		case 2:
			//back
			OpenSecurity.network.sendToServer(new OSPacketHandler(2, te.xCoord, te.yCoord, te.zCoord, 1));			
			break;
		case 3:
			//front		
			OpenSecurity.network.sendToServer(new OSPacketHandler(3, te.xCoord, te.yCoord, te.zCoord, 1));
			break;
		case 4:
			//right
			OpenSecurity.network.sendToServer(new OSPacketHandler(4, te.xCoord, te.yCoord, te.zCoord, 1));
			break;
		case 5:
			//left
			OpenSecurity.network.sendToServer(new OSPacketHandler(5, te.xCoord, te.yCoord, te.zCoord, 1));
			break;
		case 6:
			this.mc.thePlayer.closeScreen(); 
			break;
		}
	}

	public KVMGUI(InventoryPlayer inventoryPlayer, TileEntityKVM tileEntity) {
		super(new KVMContainer(inventoryPlayer, tileEntity));
		te = tileEntity;
		this.xSize = 175;
		this.ySize = 195;
	}


	public boolean getSideState(int side, int meta) {		
		ForgeDirection facing = ForgeDirection.getOrientation(meta);

		switch (facing) {
		case NORTH:
			this.rightBttn.enabled = !te.east;
			this.leftBttn.enabled = !te.west;
			this.backBttn.enabled = !te.south;
			this.frontBttn.enabled = !te.north;
			this.topBttn.enabled = !te.up;
			this.bottomBttn.enabled = !te.down;
			break;
		case SOUTH:
			this.rightBttn.enabled = !te.west;
			this.leftBttn.enabled = !te.east;
			this.backBttn.enabled = !te.north;
			this.frontBttn.enabled = !te.south;
			this.topBttn.enabled = !te.up;
			this.bottomBttn.enabled = !te.down;
			break;
		case EAST:
			this.rightBttn.enabled = !te.south;
			this.leftBttn.enabled = !te.north;
			this.backBttn.enabled = !te.west;
			this.frontBttn.enabled = !te.east;
			this.topBttn.enabled = !te.up;
			this.bottomBttn.enabled = !te.down;
			break;
		case WEST:
			this.rightBttn.enabled = !te.north;
			this.leftBttn.enabled = !te.south;
			this.backBttn.enabled = !te.east;
			this.frontBttn.enabled = !te.west;
			this.topBttn.enabled = !te.up;
			this.bottomBttn.enabled = !te.down;
			break;
		case UP:
			this.rightBttn.enabled = !te.west;
			this.leftBttn.enabled = !te.east;
			this.backBttn.enabled = !te.north;
			this.frontBttn.enabled = !te.south;
			this.topBttn.enabled = !te.up;
			this.bottomBttn.enabled = !te.down;
			break;
		case DOWN:
			this.rightBttn.enabled = !te.west;
			this.leftBttn.enabled = !te.east;
			this.backBttn.enabled = !te.north;
			this.frontBttn.enabled = !te.south;
			this.topBttn.enabled = !te.up;
			this.bottomBttn.enabled = !te.down;
			break;
		default:
			break;
		}

		return true;
	}


	@Override
	protected void drawGuiContainerForegroundLayer(int param1, int param2) {
		// the parameters for drawString are: string, x, y, color
		mc.fontRenderer.drawString(StatCollector.translateToLocal("gui.string.kvm"), 45, 5, 4210752);
		int meta = te.getBlockMetadata();
		for (int i = 0; i <= 5; i++) {
			getSideState(i, meta);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		// draw your Gui here, only thing you need to change is the path
		ResourceLocation texture = new ResourceLocation(OpenSecurity.MODID, "textures/gui/kvm.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(texture);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}

}