package pcl.opensecurity.common.tileentity;

import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Visibility;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import pcl.opensecurity.common.ContentRegistry;
import pcl.opensecurity.common.blocks.BlockSecureDoor;
import pcl.opensecurity.common.component.DoorController;
import pcl.opensecurity.common.protection.IProtection;
import pcl.opensecurity.common.protection.Protection;
import pcl.opensecurity.util.DoorHelper;

import java.util.ArrayList;
import java.util.Map;


public class TileEntityDoorController extends TileEntityOSBase implements IProtection {
	private ItemStack[] DoorControllerCamo = new ItemStack[1];
	private String ownerUUID = "";
	//private DoorController controller;


	public TileEntityDoorController(){
		super("os_doorcontroller");
		node = Network.newNode(this, Visibility.Network).withComponent(getComponentName()).withConnector(32).create();
	}

	@Override
	public void validate(){
		super.validate();
		//controller = new DoorController();
		Protection.addArea(getWorld(), new AxisAlignedBB(getPos()), getPos());
	}

	@Override
	public void invalidate() {
		Protection.removeArea(getWorld(), getPos());
		super.invalidate();
	}

	@Override
	public boolean isProtected(Entity entityIn, Protection.UserAction action){
		if(!action.equals(Protection.UserAction.explode) && ownerUUID.length() > 0 && entityIn.getUniqueID().toString().equals(ownerUUID))
			return false;

		if(entityIn != null && entityIn instanceof EntityPlayer)
			((EntityPlayer) entityIn).sendStatusMessage(new TextComponentString("this block is protected"), false);

		return true;
	}

	// OC Callbacks

	@Callback
	public Object[] isOpen(Context context, Arguments args) {
		ArrayList<Boolean> states = new ArrayList<>();

		for(Map.Entry<BlockPos, BlockDoor> doorSet : DoorHelper.getDoors(getWorld(), getPos()).entrySet())
			states.add(BlockDoor.isOpen(world, doorSet.getKey()));

		return new Object[] { states.toArray() };
	}

	@Callback
	public Object[] toggle(Context context, Arguments args) {
		for(Map.Entry<BlockPos, BlockDoor> doorSet : DoorHelper.getDoors(getWorld(), getPos()).entrySet()){
			if(BlockDoor.isOpen(world, doorSet.getKey()))
				return close(context, args);
			else
				return open(context, args);
		}

		return new Object[]{ false, "couldnt find any door" };
	}

	@Callback
	public Object[] open(Context context, Arguments args) {
		return setDoorStates(true, args.optString(0, ""));
	}

	@Callback
	public Object[] close(Context context, Arguments args) {
		return setDoorStates(false, args.optString(0, ""));
	}


	private Object[] setDoorStates(boolean open, String password){
		ArrayList<Object[]> doorResponses = new ArrayList<>();
		for(Map.Entry<BlockPos, BlockDoor> doorSet : DoorHelper.getDoors(getWorld(), getPos()).entrySet()){
			if(doorSet.getValue() instanceof BlockSecureDoor) {
				TileEntitySecureDoor te = (TileEntitySecureDoor) world.getTileEntity(doorSet.getKey());
				if (password.equals(te.getPass())) {
					doorSet.getValue().toggleDoor(world, doorSet.getKey(), open);
					doorResponses.add(new Object[] { true });
				} else {
					doorResponses.add(new Object[] { false, "Password incorrect" });
				}
			}
			else {
				doorSet.getValue().toggleDoor(world, doorSet.getKey(), open);
				doorResponses.add(new Object[] { true });
			}
		}

		if(doorResponses.size() == 0)
			return new Object[] { false, "No Security door found" };
		else
			return doorResponses.toArray();
	}

	@Callback
	public Object[] removePassword(Context context, Arguments args) {
		return setDoorPasswords(args.checkString(0), "");
	}

	@Callback
	public Object[] setPassword(Context context, Arguments args) {
		return setDoorPasswords(args.checkString(0), args.checkString(1));
	}

	Object[] setDoorPasswords(String oldPass, String newPass){
		ArrayList<Object[]> doorResponses = new ArrayList<>();
		for(Map.Entry<BlockPos, BlockDoor> doorSet : DoorHelper.getDoors(getWorld(), getPos()).entrySet()){
			if(!(doorSet.getValue() instanceof BlockSecureDoor))
				continue;

			TileEntitySecureDoor te = (TileEntitySecureDoor) world.getTileEntity(doorSet.getKey());
			TileEntitySecureDoor otherTE = (TileEntitySecureDoor) world.getTileEntity(getOtherDoorPart(doorSet.getKey()));
			if (te.getPass().isEmpty()) {
				te.setPassword(newPass);
				otherTE.setPassword(newPass);
				doorResponses.add(new Object[] { true, "Password set" });
			} else {
				if (oldPass.equals(te.getPass())) {
					te.setPassword(newPass);
					otherTE.setPassword(newPass);
					doorResponses.add(new Object[] { true, "Password Changed" });
				} else {
					doorResponses.add(new Object[] { false, "Password was not changed" });
				}
			}
		}

		if(doorResponses.size() == 0)
			return new Object[] { false, "No Security door found" };
		else
			return new Object[] { doorResponses.toArray() };
	}



	// DoorController Methods


	BlockPos getOtherDoorPart(BlockPos thisPos) {
		if (world.getTileEntity(new BlockPos(thisPos.getX(), thisPos.getY() + 1, thisPos.getZ()))  instanceof TileEntitySecureDoor){
			return new BlockPos(thisPos.getX(), thisPos.getY() + 1, thisPos.getZ());
		} else {
			return new BlockPos(thisPos.getX(), thisPos.getY() - 1, thisPos.getZ());
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


	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.ownerUUID = nbt.getString("owner");
		NBTTagList var2 = nbt.getTagList("Items", nbt.getId());
		this.DoorControllerCamo = new ItemStack[1];
		for (int var3 = 0; var3 < var2.tagCount(); ++var3) {
			NBTTagCompound var4 = var2.getCompoundTagAt(var3);
			byte var5 = var4.getByte("Slot");
			if (var5 >= 0 && var5 < this.DoorControllerCamo.length) {
				this.DoorControllerCamo[var5] = new ItemStack(var4);
				this.overrideTexture(new ItemStack(var4));
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
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
		return nbt;
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
