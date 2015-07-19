package pcl.opensecurity.tileentity;

import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Connector;
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
	
	public static ForgeDirection sideBack;
	public static ForgeDirection sideLeft;
	public static ForgeDirection sideRight;
	public static ForgeDirection sideFront;
	public static ForgeDirection sideUp;
	public static ForgeDirection sideDown;
	
	private String password = "";
	
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
		password = nbt.getString("password");

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
		nbt.setString("password", password);
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
	public Object[] setPassword(Context context, Arguments args) {
		if (password.isEmpty()) {
			password = args.checkString(0);
			return new Object[] { "Password set" };
		} else {
			if (args.checkString(0).equals(password)) {
				password = args.checkString(1);
				return new Object[] { "Password Changed" };
			} else {
				return new Object[] { "Password was not changed" };
			}
		}
	}
	
	@Callback
	public Object[] setSide(Context context, Arguments args) throws Exception {
		int side = args.checkInteger(0);
		Boolean isEnabled = args.checkBoolean(1);
		int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
		ForgeDirection facing = getWorldSide(ForgeDirection.getOrientation(side).name(), ForgeDirection.getOrientation(meta));

		if (!password.isEmpty() && !password.equals(args.checkString(2))) {
			return new Object[] { "Password Incorrect" };
		}
		
		switch (facing) {
			case NORTH:
				north = isEnabled;
				break;
			case SOUTH:
				south = isEnabled;
				break;
			case EAST:
				east = isEnabled;
				break;
			case WEST:
				west = isEnabled;
				break;
			case UP:
				up = isEnabled;
				break;
			case DOWN:
				down = isEnabled;
				break;
		default:
			break;
		}
				
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);

		getDescriptionPacket();

		if (node != null)
			node.remove();

		if (node != null && node.network() == null) {
			Network.joinOrCreateNetwork(this);
		}
		if (((Connector) node).changeBuffer(-5) == 0) {
			return new Object[]{ "ok" };
		} else {
			throw new Exception("Not enough power in OC Network.");
		}
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

	public static ForgeDirection getWorldSide(String localSide, ForgeDirection facing) {
		if ("up".equalsIgnoreCase(localSide))
			return ForgeDirection.UP;
		if ("down".equalsIgnoreCase(localSide))
			return ForgeDirection.DOWN;

		ForgeDirection front = facing;

		switch (front) {
			case NORTH:
				sideBack = ForgeDirection.SOUTH;
				sideLeft = ForgeDirection.WEST;
				sideRight = ForgeDirection.EAST;
				break;
			case SOUTH:
				sideBack = ForgeDirection.NORTH;
				sideLeft = ForgeDirection.EAST;
				sideRight = ForgeDirection.WEST;
				break;
			case WEST:
				sideBack = ForgeDirection.EAST;
				sideLeft = ForgeDirection.SOUTH;
				sideRight = ForgeDirection.NORTH;
				break;
			case EAST:
				sideBack = ForgeDirection.WEST;
				sideLeft = ForgeDirection.NORTH;
				sideRight = ForgeDirection.SOUTH;
				break;
			case UP:
				sideBack = ForgeDirection.NORTH;
				sideLeft = ForgeDirection.EAST;
				sideRight = ForgeDirection.WEST;
				sideFront = ForgeDirection.SOUTH;
				break;
			case DOWN:
				sideBack = ForgeDirection.NORTH;
				sideLeft = ForgeDirection.EAST;
				sideRight = ForgeDirection.WEST;
				sideFront = ForgeDirection.SOUTH;
				break;
			default:
				throw new IllegalArgumentException("Invalid side");
		}

		if ("south".equalsIgnoreCase(localSide))
			return sideFront;
		if ("north".equalsIgnoreCase(localSide))
			return sideBack;
		if ("east".equalsIgnoreCase(localSide))
			return sideLeft;
		if ("west".equalsIgnoreCase(localSide))
			return sideRight;
		throw new IllegalArgumentException("Invalid side");
	}
	
	
}
