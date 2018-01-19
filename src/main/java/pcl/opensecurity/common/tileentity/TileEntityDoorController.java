package pcl.opensecurity.common.tileentity;

import javax.annotation.Nullable;

import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Visibility;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import pcl.opensecurity.common.ContentRegistry;
import pcl.opensecurity.common.blocks.BlockSecureDoor;

public class TileEntityDoorController extends TileEntityOSBase {
	BlockSecureDoor doorBlock;
	BlockSecureDoor neighborDoorBlock;
	BlockDoor doorBlockVanilla;
	BlockDoor neighborDoorBlockVanilla;
	TileEntity te;
	BlockPos doorPos;
	BlockPos neighborDoorPos;
	public ItemStack[] DoorControllerCamo = new ItemStack[1];
	
	private String password = "";
	String ownerUUID = "";
	public Block block;

	public TileEntityDoorController(){
		node = Network.newNode(this, Visibility.Network).withComponent(getComponentName()).withConnector(32).create();
	}

	private static String getComponentName() {
		return "os_doorcontroller";
	}

	@Callback
	public Object[] isOpen(Context context, Arguments args) {
		rescan(this.pos);
		if(BlockDoor.isOpen(world, doorPos)) {
			return new Object[] { true }
		} else {
			return new Object[] { false }
		}
	}
	
	@Callback
	public Object[] toggle(Context context, Arguments args) {
		rescan(this.pos);
		if(BlockDoor.isOpen(world, doorPos)) {
			return close(context, args);
		} else {
			return open(context, args);
		}
	}


	public void toggle() {
		rescan(this.pos);
		if(BlockDoor.isOpen(world, doorPos)) {
			System.out.println("Door is open closing");
			rescan(this.pos);
			if (doorBlock != null && doorBlock instanceof BlockSecureDoor) {
				TileEntitySecureDoor te = (TileEntitySecureDoor) world.getTileEntity(doorPos);
				doorBlock.toggleDoor(world, doorPos, false);
			} else if (doorBlockVanilla != null && doorBlockVanilla instanceof BlockDoor) {
				doorBlockVanilla.toggleDoor(world, doorPos, false);
				neighborDoorBlockVanilla.toggleDoor(world, neighborDoorPos, false);
			}
		} else {
			System.out.println("Door is closed opening");
			rescan(this.pos);
			if (doorBlock != null && doorBlock instanceof BlockSecureDoor) {
				System.out.println("Door was valid!");
				TileEntitySecureDoor te = (TileEntitySecureDoor) world.getTileEntity(doorPos);
				doorBlock.toggleDoor(world, doorPos, true);
			} else if (doorBlockVanilla != null && doorBlockVanilla instanceof BlockDoor) {
				doorBlockVanilla.toggleDoor(world, doorPos, true);
				neighborDoorBlockVanilla.toggleDoor(world, neighborDoorPos, true);
			}
		}
	}
	
	@Callback
	public Object[] open(Context context, Arguments args) {
		rescan(this.pos);
		if (doorBlock != null && doorBlock instanceof BlockSecureDoor) {
			TileEntitySecureDoor te = (TileEntitySecureDoor) world.getTileEntity(doorPos);
			if (args.optString(0, "").equals(te.getPass())) {
				doorBlock.toggleDoor(world, doorPos, true);
				neighborDoorBlock.toggleDoor(world, neighborDoorPos, true);
				return new Object[] { true };
			} else {
				return new Object[] { false, "Password incorrect" };
			}
		} else if (doorBlockVanilla != null && doorBlockVanilla instanceof BlockDoor) {
			doorBlockVanilla.toggleDoor(world, doorPos, true);
			neighborDoorBlockVanilla.toggleDoor(world, neighborDoorPos, true);
			return new Object[] { true };
		}
		return new Object[] { false };
	}

	@Callback
	public Object[] close(Context context, Arguments args) {
		rescan(this.pos);
		if (doorBlock != null && doorBlock instanceof BlockSecureDoor) {
			TileEntitySecureDoor te = (TileEntitySecureDoor) world.getTileEntity(doorPos);
			if (args.optString(0, "").equals(te.getPass())) {
				doorBlock.toggleDoor(world, doorPos, false);
				neighborDoorBlock.toggleDoor(world, neighborDoorPos, false);
				return new Object[] { true };
			} else {
				return new Object[] { false, "Password incorrect" };
			}
		} else if (doorBlockVanilla != null && doorBlockVanilla instanceof BlockDoor) {
			doorBlockVanilla.toggleDoor(world, doorPos, false);
			neighborDoorBlockVanilla.toggleDoor(world, neighborDoorPos, false);
			return new Object[] { true };
		}
		return new Object[] { false };
	}

	public BlockPos getOtherDoorPart(BlockPos thisPos) {
		if (world.getTileEntity(new BlockPos(thisPos.getX(), thisPos.getY() + 1, thisPos.getZ()))  instanceof TileEntitySecureDoor){
			return new BlockPos(thisPos.getX(), thisPos.getY() + 1, thisPos.getZ());
		} else {
			return new BlockPos(thisPos.getX(), thisPos.getY() - 1, thisPos.getZ());
		}
	}

	@Callback
	public Object[] removePassword(Context context, Arguments args) {
		if (world.getTileEntity(doorPos) instanceof TileEntitySecureDoor) {
			TileEntitySecureDoor te = (TileEntitySecureDoor) world.getTileEntity(doorPos);
			TileEntitySecureDoor otherTE = (TileEntitySecureDoor) world.getTileEntity(getOtherDoorPart(doorPos));
			//if (ownerUUID.equals(te.getOwner())) {
			if (args.checkString(0).equals(te.getPass())) {
				if (te instanceof TileEntitySecureDoor) {
					te.setPassword("");
					otherTE.setPassword("");
				}
				return new Object[] { true, "Password Removed" };
			} else {
				return new Object[] { false, "Password was not removed" };
			}	
			//} else {
			//	return new Object[] { false, "Owner of Controller and Door do not match." };
			//}
		} else {
			return new Object[] { false, "Can only set passwords on Secure Doors" };
		}
	}

	@Callback
	public Object[] setPassword(Context context, Arguments args) {
		if (world.getTileEntity(doorPos) instanceof TileEntitySecureDoor) {
			TileEntitySecureDoor te = (TileEntitySecureDoor) world.getTileEntity(doorPos);
			TileEntitySecureDoor otherTE = (TileEntitySecureDoor) world.getTileEntity(getOtherDoorPart(doorPos));
			//if (ownerUUID.equals(te.getOwner())) {
			if (te.getPass().isEmpty()) {
				//password = args.checkString(0);
				if (te instanceof TileEntitySecureDoor) {
					((TileEntitySecureDoor) te).setPassword(args.checkString(0));
					otherTE.setPassword(args.checkString(0));
				}

				return new Object[] { true, "Password set" };			
			} else {
				if (args.checkString(0).equals(te.getPass())) {
					if (te instanceof TileEntitySecureDoor) {
						((TileEntitySecureDoor) te).setPassword(args.checkString(1));
						otherTE.setPassword(args.checkString(1));
					}
					return new Object[] { true, "Password Changed" };
				} else {
					return new Object[] { false, "Password was not changed" };
				}
			}	
			//} else {
			//	return new Object[] { false, "Owner of Controller and Door do not match." };
			//}
		} else {
			return new Object[] { false, "Can only set passwords on Secure Doors" };
		}
	}

	public void setOwner(String UUID) {
		this.ownerUUID = UUID;
	}

	public String getOwner() {
		return this.ownerUUID;
	}

	@Override
	public void update() {
		super.update();
		if (node != null && node.network() == null) {
			Network.joinOrCreateNetwork(this);
		}
	}

	public void rescan(BlockPos pos) {
		doorBlock = null;
		neighborDoorBlock = null;
		doorBlockVanilla = null;
		neighborDoorBlockVanilla = null;
		for (EnumFacing direction : EnumFacing.VALUES) {
			BlockPos neighbourPos = this.pos.offset(direction); // Offset the block's position by 1 block in the current direction
			IBlockState neighbourState = world.getBlockState(neighbourPos); // Get the IBlockState at the neighboring position
			Block neighbourBlock = neighbourState.getBlock(); // Get the IBlockState's Block
			if (neighbourBlock instanceof BlockSecureDoor){ // If the neighbouring block is a Door Block,
				doorBlock = (BlockSecureDoor) world.getBlockState(neighbourPos).getBlock();
				te = world.getTileEntity(neighbourPos);
				doorPos = neighbourPos;
				if(te instanceof TileEntitySecureDoor && ((TileEntitySecureDoor) te).getPass().isEmpty()) {
					((TileEntitySecureDoor) te).setPassword(this.password);
				}
				for (EnumFacing neighborDoorDirection : EnumFacing.VALUES) {
					BlockPos neighbourDoorPos = doorPos.offset(neighborDoorDirection); // Offset the block's position by 1 block in the current direction
					IBlockState neighbourDoorState = world.getBlockState(neighbourDoorPos); // Get the IBlockState at the neighboring position
					Block neighbourDoorBlock = neighbourDoorState.getBlock(); // Get the IBlockState's Block
					if (neighbourDoorBlock instanceof BlockSecureDoor){ // If the neighbouring block is a Door Block,
						neighborDoorBlock = (BlockSecureDoor) world.getBlockState(neighbourDoorPos).getBlock();
						neighborDoorPos = neighbourDoorPos;
						te = world.getTileEntity(neighbourDoorPos);
						if(te instanceof TileEntitySecureDoor && ((TileEntitySecureDoor) te).getPass().isEmpty()) {
							((TileEntitySecureDoor) te).setPassword(this.password);
						}
						//return;
					}
				}
				return;
			} else if (neighbourBlock instanceof BlockDoor) {
				doorPos = neighbourPos;
				doorBlockVanilla = (BlockDoor) world.getBlockState(neighbourPos).getBlock();
				for (EnumFacing neighborDoorDirection : EnumFacing.VALUES) {
					BlockPos neighbourDoorPos = doorPos.offset(neighborDoorDirection); // Offset the block's position by 1 block in the current direction
					IBlockState neighbourDoorState = world.getBlockState(neighbourDoorPos); // Get the IBlockState at the neighboring position
					Block neighbourDoorBlock = neighbourDoorState.getBlock(); // Get the IBlockState's Block
					if (neighbourDoorBlock instanceof BlockDoor){ // If the neighbouring block is a Door Block,
						neighborDoorPos = neighbourDoorPos;
						neighborDoorBlockVanilla = (BlockDoor) world.getBlockState(neighbourDoorPos).getBlock();
					}
				}
			}
		}
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
				this.overrideTexture(ItemStack.loadItemStackFromNBT(var4));
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		if (node != null && node.host() == this) {
			final NBTTagCompound nodeNbt = new NBTTagCompound();
			node.save(nodeNbt);
			nbt.setTag("oc:node", nodeNbt);
		}
		nbt.setString("owner", this.ownerUUID);
		nbt.setString("password", this.password);
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
		return nbt;
	}
	
	@Override
	@Nullable
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		readFromNBT(packet.getNbtCompound());
	}

	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		this.readFromNBT(tag);
	}
	
	public void overrideTexture(ItemStack equipped) {
		DoorControllerCamo[0] = equipped;
	}

	public IBlockState getBlockFromNBT() {
		if (DoorControllerCamo[0] != null) {
			return Block.getBlockFromItem(DoorControllerCamo[0].getItem()).getStateFromMeta(DoorControllerCamo[0].getMetadata());
		} else {
			return ContentRegistry.doorController.getDefaultState();
		}
	}

}
