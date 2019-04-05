package pcl.opensecurity.common.tileentity;

import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Visibility;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import pcl.opensecurity.common.interfaces.IOwner;
import pcl.opensecurity.common.tileentity.logic.EnergyTurret;
import pcl.opensecurity.util.ItemUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class TileEntityEnergyTurret extends TileEntityOSSound implements IOwner {

	private UUID owner;

	private EnergyTurret energyTurret = new EnergyTurret(this);

	public TileEntityEnergyTurret() {
		super("os_energyturret");
		setSound("turretMove");
		node = Network.newNode(this, Visibility.Network).withComponent(getComponentName()).withConnector(32).create();
	}

	@Override
	public void onLoad(){
		energyTurret.onLoad();
	}

	@Override
	public void update() {
		energyTurret.update();
		super.update();
	}

	public boolean consumeEnergy(double amount){
		return getWorld().isRemote || (node != null && node.tryChangeBuffer(-amount));
	}

	public EnergyTurret getEnergyTurret() {
		return energyTurret;
	}

	/* OC Callbacks*/

	@Callback(doc="function():number -- Current real yaw", direct=true)
	public Object[] getYaw(Context context, Arguments args) {
		return new Object[] { energyTurret.yaw() };
	}

	@Callback(doc="function():number -- Current real pitch", direct=true)
	public Object[] getPitch(Context context, Arguments args) {
		return new Object[] { energyTurret.pitch() };
	}

	@Callback(doc="function():boolean -- Returns whether the gun has reached the set position", direct=true)
	public Object[] isOnTarget(Context context, Arguments args) {
		return energyTurret.isOnTarget();
	}

	@Callback(doc = "function():boolean -- Returns whether the gun is ready to fire again (cooled down and armed)", direct=true)
	public Object[] isReady(Context context, Arguments args)  {
		return new Object[] { energyTurret.isReady() };
	}

	@Callback(doc = "function():boolean -- Returns whether the gun is powered", direct=true)
	public Object[] isPowered(Context context, Arguments args)  {
		return new Object[] { energyTurret.isPowered() };
	}

	@Callback(doc="function(length:boolean):number -- Extends gun shaft (0-2)")
	public Object[] extendShaft(Context context, Arguments args) {
		float setTo = energyTurret.setShaft((float) args.checkDouble(0));
		return new Object[] { setTo };
	}
	@Callback(doc="function():boolean -- Get gun shaft extension", direct=true)
	public Object[] getShaftLength(Context context, Arguments args) {
		return new Object[] { energyTurret.getShaftLength() };
	}

	@Callback(doc="function(yaw:number, pitch:number) -- Changes the gun's setpoint (Yaw ranges (0.0..360) Pitch ranges (-45..90))")
	public Object[] moveTo(Context context, Arguments args) {
		if (!energyTurret.isPowered())
			return new Object[] { false, "powered off" };

		energyTurret.setYawPitch((float)args.checkDouble(0), (float)args.checkDouble(1));
		return new Object[] { true };
	}

	@Callback(doc="function(yaw:number, pitch:number) -- Changes the gun's setpoint (Yaw ranges (0.0..360) Pitch ranges (-45..90))")
	public Object[] moveBy(Context context, Arguments args) {
		if(!energyTurret.isPowered())
			return new Object[] { false, "powered off" };

		float yaw = (float)args.checkDouble(0) + energyTurret.yaw();
		float pitch = (float)args.checkDouble(1) + energyTurret.pitch();

		energyTurret.setYawPitch(yaw, pitch);
		return new Object[] { true };
	}

	@Callback(doc="function(yaw:number, pitch:number) -- Changes the gun's setpoint in radians")
	public Object[] moveToRadians(Context context, Arguments args) {
		if(!energyTurret.isPowered())
			return new Object[] { false, "powered off" };

		double rad = args.checkDouble(0);
		double deg = rad*180/Math.PI;

		double rad2 = args.checkDouble(1);
		double deg2 = rad2*180/Math.PI;

		energyTurret.setYawPitch((float) deg, (float) deg2);

		return new Object[] { true };
	}

	@Callback
	public Object[] setArmed(Context context, Arguments args) {
		energyTurret.setArmed(args.checkBoolean(0));
		return new Object[] { true };
	}

	@Callback
	public Object[] powerOn(Context context, Arguments args) {
		energyTurret.setPowered(true);
		return new Object[] { true };
	}

	@Callback
	public Object[] powerOff(Context context, Arguments args) {
		energyTurret.setPowered(false);
		return new Object[] { true };
	}

	@Callback(doc="function():table -- Fires the gun.  More damage means longer cooldown and more energy draw. Returns true for success and throws error with a message for failure")
	public Object[] fire(Context context, Arguments args) {
		return energyTurret.fire();
	}

	/* Inventory => Capability Wrapper */

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Nullable
	@Override
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return (T) energyTurret.getInventory();
		return super.getCapability(capability, facing);
	}

	public void remove(){
		if(getWorld().isRemote)
			return;

		// drop upgrades
		for(int slot=0; slot < energyTurret.getInventory().getSlots(); slot++)
			ItemUtils.dropItem(energyTurret.getInventory().getStackInSlot(slot), getWorld(), getPos(), false, 10);
	}

	/* IOwner */
	@Override
	public void setOwner(UUID newOwner){
		owner = newOwner;
	}

	@Override
	public UUID getOwner(){
		return owner;
	}

	/* NBT */

	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		energyTurret.readFromNBT(tag.getCompoundTag("energyTurret"));
		if(tag.hasUniqueId("owner"))
			owner = tag.getUniqueId("owner");
	}

	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setTag("energyTurret", energyTurret.writeToNBT(new NBTTagCompound()));
		if(owner != null)
			tag.setUniqueId("owner", owner);

		return super.writeToNBT(tag);
	}

	public void markDirtyClient(){
		markDirty();
		getWorld().notifyBlockUpdate(getPos(), getWorld().getBlockState(getPos()), getWorld().getBlockState(getPos()), 3);
		getUpdateTag();
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(super.getUpdateTag());
	}

	@Nullable
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(getPos(), 1, getUpdateTag());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		readFromNBT(packet.getNbtCompound());
	}


}

