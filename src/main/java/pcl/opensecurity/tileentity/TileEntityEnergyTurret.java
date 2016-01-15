package pcl.opensecurity.tileentity;

import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.client.sounds.ISoundTile;
import pcl.opensecurity.entity.EntityEnergyBolt;
import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ComponentConnector;
import li.cil.oc.api.network.Connector;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class TileEntityEnergyTurret extends TileEntityMachineBase implements Environment, ISoundTile {
	public float yaw = 0.0F;
	public float pitch = 0.0F;
	public float setpointYaw = 0.0F;
	public float setpointPitch = 0.0F;
	public int tickCool = 0;
	public boolean onPoint = true;
	private final float movePerTick = 0.005F;
	public Boolean shouldPlay = false;
	public String soundName = "turretMove";
	public Boolean computerPlaying = false;
	public float volume = 1.0F;
	public int ticks = 0;
	public boolean power = true;

	protected ComponentConnector node = Network.newNode(this, Visibility.Network).withComponent(getComponentName()).withConnector(32).create();


	public TileEntityEnergyTurret() { 
		super();
		setSound(soundName); 
	}

	public Packet getDescriptionPacket() {
		NBTTagCompound syncData = new NBTTagCompound();
		write(syncData);
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, syncData);
	}

	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		NBTTagCompound tag = pkt.func_148857_g();
		read(tag);
	}

	@Override
	public boolean shouldPlaySound() {
		return shouldPlay;
	}

	@Override
	public String getSoundName() {
		return soundName;
	}

	@Override
	public float getVolume() {
		return volume;
	}

	@Override
	public ResourceLocation setSound(String sound) {
		setSoundRes(new ResourceLocation(OpenSecurity.MODID + ":" + sound));
		return getSoundRes();
	}

	public void setShouldStart(boolean b) {
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		getDescriptionPacket();
		shouldPlay = b;

	}

	public void setShouldStop(boolean b) {
		shouldPlay = b;
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		getDescriptionPacket();
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (node != null && node.network() == null) {
			Network.joinOrCreateNetwork(this);
		}
		--tickCool;
		float dy = setpointYaw - yaw;
		float dp = setpointPitch - pitch;
		float my = Math.min(movePerTick, Math.abs(dy));
		float mp = Math.min(movePerTick, Math.abs(dp));
		yaw += my * Math.signum(dy);
		pitch += mp * Math.signum(dp);
		onPoint = (Math.abs(dy) < movePerTick) && (Math.abs(dp) < movePerTick);
		if (!this.onPoint && ticks == 0) {
			ticks++;
			worldObj.playSoundEffect(this.xCoord, this.yCoord, this.zCoord, "opensecurity:turretMove", 10 / 15 + 0.5F, 1.0F);
		} else if (ticks > 5) {
			ticks = 0;
		} else {
			ticks++;
		}
	}

	@Callback(doc="function():number -- Current real yaw")
	public Object[] getYaw(Context context, Arguments args) {
		return new Object[] { Float.valueOf(this.yaw) };
	}

	@Callback(doc="function():number -- Current real pitch")
	public Object[] getPitch(Context context, Arguments args) {
		return new Object[] { Float.valueOf(this.pitch) };
	}

	@Callback(doc="function():boolean -- Returns whether the gun has reached the set position")
	public Object[] isOnTarget(Context context, Arguments args) {
		return new Object[] { Boolean.valueOf(this.onPoint) };
	}

	@Callback(doc = "function():boolean -- Returns whether the gun is cool enough to fire again")
	public Object[] isReady(Context context, Arguments args)  {
		return new Object[] { !(tickCool > 0) };
	}

	@Callback(doc="function(yaw:number, pitch:number) -- Changes the gun's setpoint (Yaw ranges (0.0..360) Pitch ranges (-45..90)")
	public Object[] moveTo(Context context, Arguments args) {
		if (power) {
			soundName = "turretMove";
			setSound(soundName);
			this.setShouldStart(true);

			this.setpointYaw = MathHelper.clamp_float((float)args.checkDouble(0), 0.0F, 360.0F) / 360;
			this.setpointPitch = MathHelper.clamp_float((float)args.checkDouble(1), -45.0F, 90.0F) / 90;
			this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
			getDescriptionPacket();
			markDirty();
			//this.setShouldStop(true);
			computerPlaying = true;
			return new Object[0];	
		} else {
			return new Object[] { Integer.valueOf(-1), "powered off" };
		}
	}
	
	@Callback
	public Object[] powerOn(Context context, Arguments args) {
		power = true;
		this.setpointYaw = 0;
		this.setpointPitch = 0;
		this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
		getDescriptionPacket();
		markDirty();
		return new Object[0];
	}

	@Callback
	public Object[] powerOff(Context context, Arguments args) {
		power = false;
		this.setpointYaw = 0;
		this.setpointPitch = (float) -0.2777778;
		this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
		getDescriptionPacket();
		markDirty();
		return new Object[0];
	}
	
	@Callback(doc="function(damage:number):table -- Fires the gun.  More damage means longer cooldown and more energy draw.  Returns 0 for success and -1 with a message for failure")
	public Object[] fire(Context context, Arguments args) {
		if (power) {
			float p = getRealPitch();
			float a = getRealYaw() + (float)Math.PI;
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

			soundName = "turretFire";
			setSound(soundName);

			if (!worldObj.isRemote)
				worldObj.playSoundEffect(this.xCoord, this.yCoord, this.zCoord, "opensecurity:" + this.soundName, 10 / 15 + 0.5F, 1.0F);

			this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
			markDirty();

			this.worldObj.spawnEntityInWorld(bolt);
			return new Object[] { Integer.valueOf(0) };
		} else {
			return new Object[] { Integer.valueOf(-1), "powered off" };
		}

	}

	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		read(tag);
	}

	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		write(tag);
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
		return "os_energyturret";
	}

	private void write(NBTTagCompound tag) {
		super.writeToNBT(tag);
		if (node != null && node.host() == this) {
			final NBTTagCompound nodeNbt = new NBTTagCompound();
			node.save(nodeNbt);
			tag.setTag("oc:node", nodeNbt);
		}
		tag.setFloat("yaw", this.yaw);
		tag.setFloat("pitch", this.pitch);
		tag.setFloat("syaw", this.setpointYaw);
		tag.setFloat("spitch", this.setpointPitch);
		tag.setInteger("cool", this.tickCool);
		writeSyncableDataToNBT(tag);
	}

	private void read(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if (node != null && node.host() == this) {
			node.load(tag.getCompoundTag("oc:node"));
		}
		this.yaw = tag.getFloat("yaw");
		this.pitch = tag.getFloat("pitch");
		this.setpointYaw = tag.getFloat("syaw");
		this.setpointPitch = tag.getFloat("spitch");
		this.tickCool = tag.getInteger("cool");
		readSyncableDataFromNBT(tag);
	}

	private void readSyncableDataFromNBT(NBTTagCompound tag) {
		//shouldPlay = tag.getBoolean("isPlayingSound");
		soundName = tag.getString("soundName");
		volume = tag.getFloat("volume");
		//computerPlaying = tag.getBoolean("computerPlaying");
	}

	private void writeSyncableDataToNBT(NBTTagCompound tag) {
		//tag.setBoolean("isPlayingSound", shouldPlay);
		tag.setString("soundName", soundName);
		tag.setFloat("volume", volume);
		//tag.setBoolean("computerPlaying", computerPlaying);
	}

	public float getRealYaw() {
		return ((float)Math.PI) * 2.0F * yaw;
	}

	public float getRealPitch() {
		return ((float)Math.PI) * 0.5F * -pitch;
	}

	@Override
	public Node node() {
		// TODO Auto-generated method stub
		return this.node;
	}

	@Override
	public void onConnect(Node arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDisconnect(Node arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMessage(Message arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean playSoundNow() {
		// TODO Auto-generated method stub
		return false;
	}
}