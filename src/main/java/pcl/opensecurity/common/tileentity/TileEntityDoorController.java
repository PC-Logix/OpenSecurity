package pcl.opensecurity.common.tileentity;

import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Visibility;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import pcl.opensecurity.ContentRegistry;
import pcl.opensecurity.common.blocks.BlockSecureDoor;

public class TileEntityDoorController extends TileEntityMachineBase {
	BlockSecureDoor doorBlock;
	BlockSecureDoor neighborDoorBlock;
	BlockDoor doorBlockVanilla;
	BlockDoor neighborDoorBlockVanilla;
	TileEntity te;
	BlockPos doorPos;
	BlockPos neighborDoorPos;

	private String password = "";
	String ownerUUID = "";

	public TileEntityDoorController(){
		node = Network.newNode(this, Visibility.Network).withComponent(getComponentName()).withConnector(32).create();
	}

	private static String getComponentName() {
		return "os_doorcontroller";
	}

	@Callback
	public Object[] toggle(Context context, Arguments args) {
		if(BlockDoor.isOpen(world, doorPos)) {
			return close(context, args);
		} else {
			return open(context, args);
		}
	}

	@Callback
	public Object[] open(Context context, Arguments args) {
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
}