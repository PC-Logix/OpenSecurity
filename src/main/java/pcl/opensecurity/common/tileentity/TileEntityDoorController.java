package pcl.opensecurity.common.tileentity;

import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Visibility;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import pcl.opensecurity.common.blocks.BlockSecureDoor;

public class TileEntityDoorController extends TileEntityMachineBase {
	BlockSecureDoor block;
	BlockSecureDoor neighborBlock;
	TileEntity te;
	BlockPos doorPos;
	BlockPos neighborDoorPos;
	
	public TileEntityDoorController(){
		node = Network.newNode(this, Visibility.Network).withComponent(getComponentName()).withConnector(32).create();
	}

	private static String getComponentName() {
		return "os_dc";
	}

	@Callback
	public Object[] toggle(Context context, Arguments args) {
        if(BlockDoor.isOpen(worldObj, doorPos)) {
        	block.toggleDoor(worldObj, doorPos, false);
        	neighborBlock.toggleDoor(worldObj, neighborDoorPos, false);
        } else {
        	block.toggleDoor(worldObj, doorPos, true);
        	neighborBlock.toggleDoor(worldObj, neighborDoorPos, true);
        }
		return new Object[] { true };
	}
	
	@Callback
	public Object[] open(Context context, Arguments args) {
		block.toggleDoor(worldObj, doorPos, true);
		neighborBlock.toggleDoor(worldObj, neighborDoorPos, true);
		return new Object[] { true };
	}
	
	@Callback
	public Object[] close(Context context, Arguments args) {
		block.toggleDoor(worldObj, doorPos, false);
		neighborBlock.toggleDoor(worldObj, neighborDoorPos, false);
		return new Object[] { true };
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
			if (neighbourBlock instanceof BlockSecureDoor){ // If the neighbouring block is a Coal Block,
				block = (BlockSecureDoor) worldObj.getBlockState(neighbourPos).getBlock();
				te = worldObj.getTileEntity(neighbourPos);
				doorPos = neighbourPos;
				
				for (EnumFacing neighborDoorDirection : EnumFacing.VALUES) {
					BlockPos neighbourDoorPos = doorPos.offset(neighborDoorDirection); // Offset the block's position by 1 block in the current direction
					IBlockState neighbourDoorState = worldObj.getBlockState(neighbourDoorPos); // Get the IBlockState at the neighboring position
					Block neighbourDoorBlock = neighbourDoorState.getBlock(); // Get the IBlockState's Block
					if (neighbourDoorBlock instanceof BlockSecureDoor){ // If the neighbouring block is a Coal Block,
						neighborBlock = (BlockSecureDoor) worldObj.getBlockState(neighbourDoorPos).getBlock();
						neighborDoorPos = neighbourDoorPos;
						//return;
					}
				}
				
				return;
			}
		}

	}
}
