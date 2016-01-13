package pcl.opensecurity.tileentity;

import pcl.opensecurity.entity.EntityEnergyBolt;
import li.cil.oc.api.Network;
import li.cil.oc.api.detail.Builder.ComponentBuilder;
import li.cil.oc.api.detail.Builder.ComponentConnectorBuilder;
import li.cil.oc.api.detail.Builder.NodeBuilder;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Connector;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.TileEntityEnvironment;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class TileEntityEnergyTurret
extends TileEntityEnvironment
{
	public float yaw = 0.0F;
	public float pitch = 0.0F;
	public float setpointYaw = 0.0F;
	public float setpointPitch = 0.0F;
	public int tickCool = 0;
	public boolean onPoint = true;
	private final float movePerTick = 0.005F;

	public TileEntityEnergyTurret()
	{
		this.node = Network.newNode(this, Visibility.Network).withComponent("energyturret", Visibility.Network).withConnector().create();
	}

	public Packet getDescriptionPacket()
	{
		NBTTagCompound syncData = new NBTTagCompound();
		write(syncData);
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, syncData);
	}

	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
	{
		NBTTagCompound tag = pkt.func_148857_g();
		read(tag);
	}

	public void updateEntity()
	{
		super.updateEntity();
		this.tickCool -= 1;
		float dy = this.setpointYaw - this.yaw;
		float dp = this.setpointPitch - this.pitch;
		float my = Math.min(0.005F, Math.abs(dy));
		float mp = Math.min(0.005F, Math.abs(dp));
		this.yaw += my * Math.signum(dy);
		this.pitch += mp * Math.signum(dp);
		this.onPoint = ((Math.abs(dy) < 0.005F) && (Math.abs(dp) < 0.005F));
	}

	@Callback(doc="function():number -- Current real yaw")
	public Object[] getYaw(Context context, Arguments args)
	{
		return new Object[] { Float.valueOf(this.yaw) };
	}

	@Callback(doc="function():number -- Current real pitch")
	public Object[] getPitch(Context context, Arguments args)
	{
		return new Object[] { Float.valueOf(this.pitch) };
	}

	@Callback(doc="function():boolean -- Returns whether the gun has reached the set position")
	public Object[] isOnTarget(Context context, Arguments args)
	{
		return new Object[] { Boolean.valueOf(this.onPoint) };
	}

	@Callback(doc="function():boolean -- Returns whether the gun is cool enough to fire again")
	public Object[] isReady(Context context, Arguments args)
	{
		return new Object[] { this.tickCool <= 0 ? 1 : false };
	}

	@Callback(doc="function(yaw:number, pitch:number) -- Changes the gun's setpoint (ranges (0.0..1.0))")
	public Object[] moveTo(Context context, Arguments args)
	{
		this.setpointYaw = MathHelper.clamp_float((float)args.checkDouble(0), 0.0F, 1.0F);
		this.setpointPitch = MathHelper.clamp_float((float)args.checkDouble(1), 0.0F, 1.0F);
		this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
		markDirty();
		return new Object[0];
	}

	@Callback(doc="function(damage:number):table -- Fires the gun.  More damage means longer cooldown and more energy draw.  Returns 0 for success and -1 with a message for failure")
	public Object[] fire(Context context, Arguments args)
	{
		float p = getRealPitch();
		float a = getRealYaw() + 3.1415927F;
		float damage = MathHelper.clamp_float((float)args.checkDouble(0), 0.0F, 50.0F);
		EntityEnergyBolt bolt = new EntityEnergyBolt(this.worldObj);
		bolt.setHeading(a, p);
		bolt.setDamage(damage);
		bolt.setPosition(this.xCoord + 0.5F, this.yCoord + 0.85F, this.zCoord + 0.5F);
		if (!((Connector)this.node).tryChangeBuffer(-1.0D)) {
			return new Object[] { Integer.valueOf(-1), "not enough energy" };
		}
		if (this.tickCool > 0) {
			return new Object[] { Integer.valueOf(-1), "gun hasn't cooled" };
		}
		this.tickCool = 200;

		this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
		markDirty();

		this.worldObj.spawnEntityInWorld(bolt);
		return new Object[] { Integer.valueOf(0) };
	}

	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		read(tag);
	}

	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		write(tag);
	}

	private void write(NBTTagCompound tag)
	{
		tag.setFloat("yaw", this.yaw);
		tag.setFloat("pitch", this.pitch);
		tag.setFloat("syaw", this.setpointYaw);
		tag.setFloat("spitch", this.setpointPitch);
		tag.setInteger("cool", this.tickCool);
	}

	private void read(NBTTagCompound tag)
	{
		this.yaw = tag.getFloat("yaw");
		this.pitch = tag.getFloat("pitch");
		this.setpointYaw = tag.getFloat("syaw");
		this.setpointPitch = tag.getFloat("spitch");
		this.tickCool = tag.getInteger("cool");
	}

	public float getRealYaw()
	{
		return 6.2831855F * this.yaw;
	}

	public float getRealPitch()
	{
		return 1.5707964F * -this.pitch;
	}
}