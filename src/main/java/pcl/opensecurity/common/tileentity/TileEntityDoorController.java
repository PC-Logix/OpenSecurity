package pcl.opensecurity.common.tileentity;

import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.EnvironmentHost;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.network.Visibility;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import pcl.opensecurity.common.ContentRegistry;
import pcl.opensecurity.common.component.DoorController;
import pcl.opensecurity.common.protection.IProtection;
import pcl.opensecurity.common.protection.Protection;


public class TileEntityDoorController extends TileEntityOSBase implements IProtection, ManagedEnvironment {
	private ItemStack[] DoorControllerCamo = new ItemStack[1];
	private String ownerUUID = "";


	public TileEntityDoorController(){
		super("os_doorcontroller");
		node = Network.newNode(this, Visibility.Network).withComponent(getComponentName()).withConnector(32).create();
	}

	public TileEntityDoorController(EnvironmentHost host){
		super("os_doorcontroller", host);
		node = Network.newNode(this, Visibility.Network).withComponent(getComponentName()).withConnector(32).create();
	}


	@Override
	public void validate(){
		super.validate();
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
		return DoorController.isOpen(getWorld(), getPos());
	}

	@Callback
	public Object[] toggle(Context context, Arguments args) {
		return DoorController.toggle(getWorld(), getPos(), args.optString(0, ""));
	}

	@Callback
	public Object[] open(Context context, Arguments args) {
		return DoorController.setDoorStates(getWorld(), getPos(), true, args.optString(0, ""));
	}

	@Callback
	public Object[] close(Context context, Arguments args) {
		return DoorController.setDoorStates(getWorld(), getPos(), false, args.optString(0, ""));
	}

	@Callback
	public Object[] removePassword(Context context, Arguments args) {
		return DoorController.setDoorPasswords(getWorld(), getPos(), args.checkString(0), "");
	}

	@Callback
	public Object[] setPassword(Context context, Arguments args) {
		return DoorController.setDoorPasswords(getWorld(), getPos(), args.checkString(0), args.checkString(1));
	}



	// DoorControllerTile Methods
	public void setOwner(String UUID) {
		this.ownerUUID = UUID;
	}

	public String getOwner() {
		return this.ownerUUID;
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


	@Override
	public World getWorld(){
		if(isUpgrade)
			return container.world();

		return super.getWorld();
	}


	@Override
	public BlockPos getPos(){
		if(isUpgrade)
			return new BlockPos(container.xPosition(), container.yPosition(), container.zPosition());

		return super.getPos();
	}

	//only upgrade related methods

	@Override
	public boolean canUpdate(){ return true; }

	@Override
	public void load(NBTTagCompound var1){}

	@Override
	public void save(NBTTagCompound var1){}


}
