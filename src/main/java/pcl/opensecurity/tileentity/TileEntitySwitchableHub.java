package pcl.opensecurity.tileentity;

import java.util.HashMap;
import java.util.Map;

import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.SidedEnvironment;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.TileEntitySidedEnvironment;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntitySwitchableHub extends TileEntitySidedEnvironment implements SidedEnvironment, Environment {

	protected Node node = Network.newNode(this, Visibility.Network).withComponent(getComponentName()).withConnector(32).create();

	public boolean north = false;
	public boolean south = false;
	public boolean east = false;
	public boolean west = false;
	public boolean up = false;
	public boolean down = false;

	public int Texture;
	
	@SideOnly(Side.CLIENT)
	@Override
	public boolean canConnect(ForgeDirection side) {
		int meta = this.worldObj.getBlockMetadata(xCoord, yCoord, zCoord);

		if (meta == side.ordinal()) {
			return true;
		} else if (side.ordinal() == ForgeDirection.NORTH.ordinal() && north) {
			return true;
		} else if (side.ordinal() == ForgeDirection.SOUTH.ordinal() && south) {
			return true;
		} else if (side.ordinal() == ForgeDirection.EAST.ordinal() && east) {
			return true;
		} else if (side.ordinal() == ForgeDirection.WEST.ordinal() && west) {
			return true;
		} else if (side.ordinal() == ForgeDirection.UP.ordinal() && up) {
			return true;
		} else if (side.ordinal() == ForgeDirection.DOWN.ordinal() && down) {
			return true;
		} else {
			return false;
		}

	}

	public String getComponentName() {
		return "os_switchinghub";
	}

	@Override
	public Node sidedNode(ForgeDirection side) {
		int meta = this.worldObj.getBlockMetadata(xCoord, yCoord, zCoord);

		if (meta == side.ordinal()) {
			return node;
		} else if (side.ordinal() == ForgeDirection.NORTH.ordinal() && north) {
			return node;
		} else if (side.ordinal() == ForgeDirection.SOUTH.ordinal() && south) {
			return node;
		} else if (side.ordinal() == ForgeDirection.EAST.ordinal() && east) {
			return node;
		} else if (side.ordinal() == ForgeDirection.WEST.ordinal() && west) {
			return node;
		} else if (side.ordinal() == ForgeDirection.UP.ordinal() && up) {
			return node;
		} else if (side.ordinal() == ForgeDirection.DOWN.ordinal() && down) {
			return node;
		} else {
			return null;
		}
	}

	@Override
	public Node node() {
		return node;
	}

	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		if (node != null)
			node.remove();
	}

	@Override
	public void invalidate() {
		super.invalidate();
		if (node != null)
			node.remove();
	}

	@Override
	public void onConnect(final Node node) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onMessage(Message message) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onDisconnect(Node node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		if (node != null && node.host() == this) {
			node.load(nbt.getCompoundTag("oc:node"));
		}
		north = nbt.getBoolean("north");
		south = nbt.getBoolean("south");
		west = nbt.getBoolean("west");
		east = nbt.getBoolean("east");
		up = nbt.getBoolean("up");
		down = nbt.getBoolean("down");

	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		if (node != null && node.host() == this) {
			final NBTTagCompound nodeNbt = new NBTTagCompound();
			node.save(nodeNbt);
			nbt.setTag("oc:node", nodeNbt);
		}
		nbt.setBoolean("north", north);
		nbt.setBoolean("south", south);
		nbt.setBoolean("east", east);
		nbt.setBoolean("west", west);
		nbt.setBoolean("up", up);
		nbt.setBoolean("down", down);
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (node != null && node.network() == null) {
			Network.joinOrCreateNetwork(this);
		}
	}

	@Callback
	public Object[] greet(Context context, Arguments args) {
		return new Object[] { "Lasciate ogne speranza, voi ch'intrate" };
	}

	@Callback
	public Object[] setSide(Context context, Arguments args) {
		String side = args.checkString(0);
		Boolean enabled = args.checkBoolean(1);

		if (side.equalsIgnoreCase("south"))
			south = enabled;
		else if (side.equalsIgnoreCase("north"))
			north = enabled;
		else if (side.equalsIgnoreCase("east"))
			east = enabled;
		else if (side.equalsIgnoreCase("west"))
			west = enabled;
		else if (side.equalsIgnoreCase("up"))
			up = enabled;
		else if (side.equalsIgnoreCase("down"))
			down = enabled;

		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);

		getDescriptionPacket();

		if (node != null)
			node.remove();

		if (node != null && node.network() == null) {
			Network.joinOrCreateNetwork(this);
		}
		return new Object[] { "ok" };
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound tagCom = new NBTTagCompound();
		this.writeToNBT(tagCom);
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, this.blockMetadata, tagCom);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		NBTTagCompound tagCom = packet.func_148857_g();
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		this.readFromNBT(tagCom);
	}

}
