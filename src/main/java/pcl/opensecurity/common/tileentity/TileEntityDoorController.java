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
        if(BlockDoor.isOpen(worldObj, doorPos)) {
        	doorBlock.toggleDoor(worldObj, doorPos, false);
        	neighborDoorBlock.toggleDoor(worldObj, neighborDoorPos, false);
        } else {
        	doorBlock.toggleDoor(worldObj, doorPos, true);
        	neighborDoorBlock.toggleDoor(worldObj, neighborDoorPos, true);
        }
		return new Object[] { true };
	}
	
	@Callback
	public Object[] open(Context context, Arguments args) {
		doorBlock.toggleDoor(worldObj, doorPos, true);
		neighborDoorBlock.toggleDoor(worldObj, neighborDoorPos, true);
		return new Object[] { true };
	}
	
	@Callback
	public Object[] close(Context context, Arguments args) {
		doorBlock.toggleDoor(worldObj, doorPos, false);
		neighborDoorBlock.toggleDoor(worldObj, neighborDoorPos, false);
		return new Object[] { true };
	}
	
	@Callback
	public Object[] removePassword(Context context, Arguments args) {
		TileEntitySecureDoor te = (TileEntitySecureDoor) worldObj.getTileEntity(doorPos);
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
		TileEntitySecureDoor te = (TileEntitySecureDoor) worldObj.getTileEntity(doorPos);
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
		for (EnumFacing direction : EnumFacing.VALUES) {
			BlockPos neighbourPos = this.pos.offset(direction); // Offset the block's position by 1 block in the current direction
			IBlockState neighbourState = worldObj.getBlockState(neighbourPos); // Get the IBlockState at the neighboring position
			Block neighbourBlock = neighbourState.getBlock(); // Get the IBlockState's Block
			if (neighbourBlock instanceof BlockSecureDoor){ // If the neighbouring block is a Door Block,
				doorBlock = (BlockSecureDoor) worldObj.getBlockState(neighbourPos).getBlock();
				te = worldObj.getTileEntity(neighbourPos);
				doorPos = neighbourPos;
				if(te instanceof TileEntitySecureDoor && ((TileEntitySecureDoor) te).getPass().isEmpty()) {
					((TileEntitySecureDoor) te).setPassword(this.password);
				}
				for (EnumFacing neighborDoorDirection : EnumFacing.VALUES) {
					BlockPos neighbourDoorPos = doorPos.offset(neighborDoorDirection); // Offset the block's position by 1 block in the current direction
					IBlockState neighbourDoorState = worldObj.getBlockState(neighbourDoorPos); // Get the IBlockState at the neighboring position
					Block neighbourDoorBlock = neighbourDoorState.getBlock(); // Get the IBlockState's Block
					if (neighbourDoorBlock instanceof BlockSecureDoor){ // If the neighbouring block is a Coal Block,
						neighborDoorBlock = (BlockSecureDoor) worldObj.getBlockState(neighbourDoorPos).getBlock();
						neighborDoorPos = neighbourDoorPos;
						te = worldObj.getTileEntity(neighbourDoorPos);
						if(te instanceof TileEntitySecureDoor && ((TileEntitySecureDoor) te).getPass().isEmpty()) {
							((TileEntitySecureDoor) te).setPassword(this.password);
						}
						//return;
					}
				}
				
				return;
			}
		}

	}
}
