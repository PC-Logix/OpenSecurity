package pcl.opensecurity.tileentity;

import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ComponentConnector;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import pcl.opensecurity.ContentRegistry;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.blocks.BlockSecurityDoor;
import pcl.opensecurity.util.BlockLocation;

/**
 * @author Caitlyn
 *
 */
public class TileEntityDoorController extends TileEntityMachineBase implements Environment {

	public Block block = null;

	public BlockSecurityDoor door;

	private String password = "";

	int doorCoordX;
	int doorCoordY;
	int doorCoordZ;

	String ownerUUID = "";

	public TileEntityDoorController() {}

	public ItemStack[] DoorControllerCamo = new ItemStack[1];

	protected ComponentConnector node = Network.newNode(this, Visibility.Network).withComponent(getComponentName()).withConnector(32).create();

	public IIcon[] blockTextures = new IIcon[6];

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

	private String getComponentName() {
		return "os_door";
	}

	@Override
	public void onConnect(Node arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDisconnect(final Node node) {

	}

	@Override
	public void onMessage(Message arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		if (node != null && node.host() == this) {
			node.load(nbt.getCompoundTag("oc:node"));
		}
		this.ownerUUID = nbt.getString("owner");
		this.password = nbt.getString("password");
		NBTTagList var2 = nbt.getTagList("Items", nbt.getId());
		this.DoorControllerCamo = new ItemStack[1];
		for (int var3 = 0; var3 < var2.tagCount(); ++var3) {
			NBTTagCompound var4 = var2.getCompoundTagAt(var3);
			byte var5 = var4.getByte("Slot");
			if (var5 >= 0 && var5 < this.DoorControllerCamo.length) {
				this.DoorControllerCamo[var5] = ItemStack.loadItemStackFromNBT(var4);
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		if (node != null && node.host() == this) {
			final NBTTagCompound nodeNbt = new NBTTagCompound();
			node.save(nodeNbt);
			nbt.setTag("oc:node", nodeNbt);
		}
		nbt.setString("owner", this.ownerUUID);
		NBTTagList var2 = new NBTTagList();
		for (int var3 = 0; var3 < this.DoorControllerCamo.length; ++var3) {
			if (this.DoorControllerCamo[var3] != null) {
				NBTTagCompound var4 = new NBTTagCompound();
				var4.setByte("Slot", (byte) var3);
				this.DoorControllerCamo[var3].writeToNBT(var4);
				var2.appendTag(var4);
			}
		}
		nbt.setTag("Items", var2);
		//nbt.setString("password", password);
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (node != null && node.network() == null) {
			Network.joinOrCreateNetwork(this);
		}
		for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
			block = worldObj.getBlock(xCoord + direction.offsetX, yCoord + direction.offsetY, zCoord + direction.offsetZ);
			TileEntity te = worldObj.getTileEntity(xCoord + direction.offsetX, yCoord + direction.offsetY, zCoord + direction.offsetZ);
			if (block instanceof BlockSecurityDoor) {
				this.door = (BlockSecurityDoor) block;
				doorCoordX = xCoord + direction.offsetX;
				doorCoordY = yCoord + direction.offsetY;
				doorCoordZ = zCoord + direction.offsetZ;
				if(te instanceof TileEntitySecureDoor) {
					if (((TileEntitySecureDoor) te).getPass().isEmpty()) {
						((TileEntitySecureDoor) te).setPassword(this.password);
					}
				}
			}
		}
	}

	@Callback
	public Object[] removePassword(Context context, Arguments args) {
		TileEntitySecureDoor te = (TileEntitySecureDoor) worldObj.getTileEntity(doorCoordX, doorCoordY, doorCoordZ);
		if (ownerUUID.equals(te.getOwner())) {
			if (args.checkString(0).equals(te.getPass())) {
				if (te instanceof TileEntitySecureDoor) {
					((TileEntitySecureDoor) te).setPassword("");
				}
				return new Object[] { true, "Password Removed" };
			} else {
				return new Object[] { false, "Password was not removed" };
			}	
		} else {
			return new Object[] { false, "Owner of Controller and Door do not match." };
		}
	}

	@Callback
	public Object[] setPassword(Context context, Arguments args) {
		TileEntitySecureDoor te = (TileEntitySecureDoor) worldObj.getTileEntity(doorCoordX, doorCoordY, doorCoordZ);
		if (ownerUUID.equals(te.getOwner())) {
			if (te.getPass().isEmpty()) {
				//password = args.checkString(0);
				if (te instanceof TileEntitySecureDoor) {
					((TileEntitySecureDoor) te).setPassword(args.checkString(0));
				}

				return new Object[] { true, "Password set" };			
			} else {
				if (args.checkString(0).equals(te.getPass())) {
					if (te instanceof TileEntitySecureDoor) {
						((TileEntitySecureDoor) te).setPassword(args.checkString(1));
					}
					return new Object[] { true, "Password Changed" };
				} else {
					return new Object[] { false, "Password was not changed" };
				}
			}	
		} else {
			return new Object[] { false, "Owner of Controller and Door do not match." };
		}
	}

	@Callback
	public Object[] greet(Context context, Arguments args) {
		return new Object[] { "Lasciate ogne speranza, voi ch'intrate" };
	}

	private int getDoorOrientation(BlockDoor door, BlockLocation loc) {
		return door.func_150013_e(loc.blockAccess, loc.x, loc.y, loc.z);
	}

	private boolean isDoorOpen(BlockDoor door, BlockLocation loc) {
		return door.func_150015_f(loc.blockAccess, loc.x, loc.y, loc.z);
	}

	private boolean isDoorMirrored(BlockDoor door, BlockLocation loc) {
		return (door.func_150012_g(loc.blockAccess, loc.x, loc.y, loc.z) & 16) != 0;
	}

	@Callback
	public Object[] isOpen(Context context, Arguments args) {
		BlockSecurityDoor door = (BlockSecurityDoor) ContentRegistry.SecurityDoor;
		BlockLocation loc = BlockLocation.get(worldObj, doorCoordX, doorCoordY, doorCoordZ);
		return new Object[] { isDoorOpen(door, loc) };
	}

/*	@Callback
	public Object[] open(Context context, Arguments args) {
		if (node.changeBuffer(-5) == 0) {
			
		}
	}
	
	@Callback
	public Object[] close(Context context, Arguments args) {
		if (node.changeBuffer(-5) == 0) {
			
		}
	}*/
	
	@Callback
	public Object[] toggle(Context context, Arguments args) {
		if (node.changeBuffer(-5) == 0) {
			BlockSecurityDoor door = (BlockSecurityDoor) ContentRegistry.SecurityDoor;
			BlockLocation loc = BlockLocation.get(worldObj, doorCoordX, doorCoordY, doorCoordZ);
			TileEntitySecureDoor te = (TileEntitySecureDoor) worldObj.getTileEntity(doorCoordX, doorCoordY, doorCoordZ);

			if (ownerUUID.equals(te.getOwner())) {
				if (!te.getPass().isEmpty() && !te.getPass().equals(args.checkString(0))) {
					return new Object[] { false, "Password Incorrect" };
				}
				int direction = getDoorOrientation(door, loc);
				// boolean isOpen = isDoorOpen(door, loc);
				boolean isMirrored = isDoorMirrored(door, loc);

				int i = isMirrored ? -1 : 1;
				switch (direction) {
				case 0:
					loc = loc.relative(0, 0, i);
					break;
				case 1:
					loc = loc.relative(-i, 0, 0);
					break;
				case 2:
					loc = loc.relative(0, 0, -i);
					break;
				case 3:
					loc = loc.relative(i, 0, 0);
					break;
				default:
					break;
				}

				if ((loc.getBlock() == door) && (getDoorOrientation(door, loc) == direction) && (isDoorMirrored(door, loc) != isMirrored) || worldObj.getBlock(doorCoordX, doorCoordY, doorCoordZ) instanceof BlockSecurityDoor) {

					int i1 = worldObj.getBlockMetadata(doorCoordX, doorCoordY, doorCoordZ);

					int j2;

					if ((i1 & 8) == 0) {
						int doorBottomMeta = worldObj.getBlockMetadata(doorCoordX, doorCoordY, doorCoordZ);
						j2 = doorBottomMeta & 7;
						j2 ^= 4;
						worldObj.setBlockMetadataWithNotify(doorCoordX, doorCoordY, doorCoordZ, j2, 2);
						worldObj.markBlockRangeForRenderUpdate(doorCoordX, doorCoordY, doorCoordZ, doorCoordX, doorCoordY, doorCoordZ);
						if (loc.getBlock() instanceof BlockSecurityDoor) {
							worldObj.setBlockMetadataWithNotify(loc.x, loc.y, loc.z, j2, 2);
							worldObj.markBlockRangeForRenderUpdate(loc.x, loc.y, loc.z, loc.x, loc.y, loc.z);
						}
					} else {
						int doorBottomMeta = worldObj.getBlockMetadata(doorCoordX, doorCoordY - 1, doorCoordZ);
						j2 = doorBottomMeta & 7;
						j2 ^= 4;
						worldObj.setBlockMetadataWithNotify(doorCoordX, doorCoordY - 1, doorCoordZ, j2, 2);
						worldObj.markBlockRangeForRenderUpdate(doorCoordX, doorCoordY - 1, doorCoordZ, doorCoordX, doorCoordY - 1, doorCoordZ);
						if (loc.getBlock() instanceof BlockSecurityDoor) {
							worldObj.setBlockMetadataWithNotify(loc.x, loc.y - 1, loc.z, j2, 2);
							worldObj.markBlockRangeForRenderUpdate(loc.x, loc.y, loc.z - 1, loc.x, loc.y, loc.z);
						}
					}
				}
				return new Object[] { !isDoorOpen(door, loc) };
			} else {
				return new Object[] { false, "Owner of Controller and Door do not match." };
			}
		} else {
			return new Object[] { false, "Not enough power in OC Network." };
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

	public void setOwner(String UUID) {
		this.ownerUUID = UUID;
	}

	public String getOwner() {
		return this.ownerUUID;
	}

	public void overrideTexture(Block theBlock, ItemStack theItem, ForgeDirection forgeDirection) {
		DoorControllerCamo[0] = theItem;
		for (int getSide = 0; getSide < blockTextures.length; getSide++)
		{
			if (worldObj.isRemote) {
				this.blockTextures[getSide] = theBlock.getIcon(getSide, theItem.getItem().getDamage(theItem));
			}
		}
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		getDescriptionPacket();
	}

	public void overrideTexture(ItemStack theItem) {
		DoorControllerCamo[0] = theItem;
		Block theBlock = Block.getBlockFromItem(theItem.getItem());
		for (int getSide = 0; getSide < blockTextures.length; getSide++)
		{
			if (worldObj.isRemote) {
				this.blockTextures[getSide] = theBlock.getIcon(getSide, theItem.getItem().getDamage(theItem));
			}
		}
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		getDescriptionPacket();
	}
}
