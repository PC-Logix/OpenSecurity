package pcl.opensecurity.tileentity;

import pcl.opensecurity.ContentRegistry;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.client.sounds.ISoundTile;
import pcl.opensecurity.entity.EntityEnergyBolt;
import pcl.opensecurity.items.ItemCooldownUpgrade;
import pcl.opensecurity.items.ItemDamageUpgrade;
import pcl.opensecurity.items.ItemEnergyUpgrade;
import pcl.opensecurity.items.ItemMovementUpgrade;
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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class TileEntityEnergyTurret extends TileEntityMachineBase implements Environment, IInventory, ISoundTile {
        static final float maxShaftLengthForOneBlock = 0.5f;

	public float yaw = 0.0F;
	public float pitch = 0.0F;
	public float setpointYaw = 0.0F;
	public float setpointPitch = 0.0F;
	public float shaft = 1.0F;
	public float setShaft = 1.0F;
	public float barrel = 1.0F;
	public int tickCool = 0;
	public boolean onPoint = true;
	private float movePerTick = 0.005F;
	public Boolean shouldPlay = false;
	public String soundName = "turretMove";
	public float volume = 1.0F;
	public int soundTicks = 0;
	public boolean power = true;
	public boolean armed = true;

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

	public boolean isUpright()
	{
		return worldObj==null ? true : 1 != worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
	}

	public void setShouldStart(boolean b) {
		shouldPlay = b;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		boolean upright = isUpright();
		boolean shaftLengthValid = true;
		boolean moveSound = false;

		movePerTick = 3;

		float maxShaft = getMaxAvailableShaftLength(shaft);
		if (shaft > maxShaft)
		{
			shaftLengthValid = false;
			
			if (setShaft > maxShaft)
				setShaft(maxShaft);
		}

		if (node != null) {
			if(node.network() == null)
				Network.joinOrCreateNetwork(this);
			if (power && !((Connector)this.node).tryChangeBuffer(-10)) {
				doPowerOff();
			}
		}
		if (this.ItemStacks[2] != null && this.ItemStacks[2].getItem() instanceof ItemMovementUpgrade) {
			movePerTick = movePerTick + 1.5F;
		}
		if (this.ItemStacks[3] != null && this.ItemStacks[3].getItem() instanceof ItemMovementUpgrade) {
			movePerTick = movePerTick + 1.5F;
		}

		if (this.ItemStacks[4] != null && this.ItemStacks[4].getItem() instanceof ItemCooldownUpgrade) {
			--tickCool;
			--tickCool;
			--tickCool;
		}
		if (this.ItemStacks[5] != null && this.ItemStacks[5].getItem() instanceof ItemCooldownUpgrade) {
			--tickCool;
			--tickCool;
			--tickCool;
		}
		--tickCool;

		float tmpSetPitch=setpointPitch;
		float ms=0F, my=0F, mp=0F;

		if (power || !shaftLengthValid) {
			if(Float.isNaN(shaft) || Float.isInfinite(shaft)) shaft = 0;
			float ds = setShaft - shaft;
			ms = Math.min(0.05F, Math.abs(ds));
			shaft += ms * Math.signum(ds);
			if(ms>0F) moveSound = true;
		}
		if (power) {
			if(Float.isNaN(yaw) || Float.isInfinite(yaw)) yaw = 0;

			float dy = (setpointYaw - yaw)%360;
			if(dy>180) dy = dy - 360;
			else if(dy<-180) dy = 360 + dy;
			my = Math.min(movePerTick, Math.abs(dy));
			yaw += my * Math.signum(dy);
			yaw = yaw%360;
			if(yaw<0F) yaw+=360F;
			if(my>0F) moveSound = true;
		} else {
			tmpSetPitch = -90F;
			movePerTick = 6;
		}

		if (upright)
		{
		    tmpSetPitch = Math.min(tmpSetPitch, (float)(Math.atan(shaft)*360/Math.PI));
		    tmpSetPitch = Math.max(tmpSetPitch, (float)(-Math.atan(shaft)*180/Math.PI));
		}
		else
		{
		    tmpSetPitch = Math.min(tmpSetPitch, (float)(Math.atan(shaft)*180/Math.PI));
		    tmpSetPitch = Math.max(tmpSetPitch, (float)(-Math.atan(shaft)*360/Math.PI));
		}

		if(Float.isNaN(pitch) || Float.isInfinite(pitch)) pitch = 0;
		float dp = tmpSetPitch - pitch;
		mp = Math.min(movePerTick, Math.abs(dp));
		if(power && mp>0F) moveSound = true;
		
		pitch += mp * Math.signum(dp);
		if(pitch < -90F) pitch=-90F;
		else if(pitch > 90F) pitch=90F;
	
		if (power) {
			if (armed) {
				if(barrel<1F) {
					barrel=Math.min(1F, barrel+0.1F);
					moveSound = true;
				}
			}
			else {
				if (barrel>0F) {
					barrel=Math.max(0F, barrel-0.1F);
					moveSound = true;
				}
			}
		}

		if (moveSound) {
        		if(soundTicks == 0) {
	        		worldObj.playSoundEffect(this.xCoord, this.yCoord, this.zCoord, "opensecurity:turretMove", 10 / 15 + 0.5F, 1.0F);
	        	}
        		soundTicks++;
        		if (soundTicks > 5) {
        			soundTicks = 0;
	        	}
        	} else {
        		soundTicks = 0;
        	}
	}

	@Callback(doc="function():number -- Current real yaw", direct=true)
	public Object[] getYaw(Context context, Arguments args) {
		return new Object[] { yaw };
	}

	@Callback(doc="function():number -- Current real pitch", direct=true)
	public Object[] getPitch(Context context, Arguments args) {
		return new Object[] { pitch };
	}

	@Callback(doc="function():boolean -- Returns whether the gun has reached the set position", direct=true)
	public Object[] isOnTarget(Context context, Arguments args) {
	        double dPitch = Math.abs(pitch-setpointPitch);
	        double dYaw = Math.abs(yaw-setpointYaw);
	        double delta = dPitch + dYaw;
		boolean onPoint = delta < 0.5F;
		return new Object[] { onPoint, delta };
	}

	@Callback(doc = "function():boolean -- Returns whether the gun is ready to fire again (cooled down and armed)", direct=true)
	public Object[] isReady(Context context, Arguments args)  {
		return new Object[] { !(tickCool > 0) && armed && barrel==1F };
	}

	@Callback(doc = "function():boolean,number -- Returns whether the gun is powered", direct=true)
	public Object[] isPowered(Context context, Arguments args)  {
		return new Object[] { power };
	}

	float getMaxAvailableShaftLength(float newExt)
	{
	        if (newExt<0F) {
		        newExt = 0F;
	        }
	        if (newExt>2F) {
		        newExt = 2F;
	        }
	        int otherY = this.yCoord+(isUpright()?1:-1);
	        if(newExt>maxShaftLengthForOneBlock && (otherY<0 || otherY>255 || !worldObj.isAirBlock(this.xCoord, otherY, this.zCoord)))
	        {
	            return maxShaftLengthForOneBlock;
	        }
	        return newExt;
	}

	float setShaft(float newlen)
	{
	        if (newlen<0F) {
		        newlen = 0F;
	        }
	        float most = getMaxAvailableShaftLength(newlen);
	        if (newlen>most) {
		        newlen = most;
	        }
	        if(setShaft != newlen)
	        {
		        setShaft = newlen;

			this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
			markDirty();
		}
		
		return newlen;
	}

	void setYaw(float value)
	{
      		this.setpointYaw = value % 360;
      		if (this.setpointYaw < 0) this.setpointYaw += 360;
	}

	void setPitch(float value)
	{
		this.setpointPitch = value;
		if (this.setpointPitch < -90F) this.setpointPitch = -90F;
		else if(this.setpointPitch > 90F) this.setpointPitch = 90F;
	}

	@Callback(doc="function(length:boolean):number -- Extends gun shaft (0-2)")
	public Object[] extendShaft(Context context, Arguments args) throws Exception {
	        float setTo = setShaft((float)args.checkDouble(0));
		this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
		markDirty();
		return new Object[] { setTo };
	}
	@Callback(doc="function():boolean -- Get gun shaft extension", direct=true)
	public Object[] getShaftLength(Context context, Arguments args) throws Exception {
		return new Object[] { this.shaft };
	}

	@Callback(doc="function(yaw:number, pitch:number) -- Changes the gun's setpoint (Yaw ranges (0.0..360) Pitch ranges (-45..90))")
	public Object[] moveTo(Context context, Arguments args) throws Exception {
		if (power) {
			soundName = "turretMove";
			setSound(soundName);
			this.setShouldStart(true);

			setYaw((float)args.checkDouble(0));
			setPitch((float)args.checkDouble(1));

			this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
			markDirty();
			return new Object[] { true };	
		} else {
			throw new IllegalArgumentException("powered off");
		}
	}
	
	@Callback
	public Object[] setArmed(Context context, Arguments args) {
		boolean newArmed = args.checkBoolean(0);
		if (armed!=newArmed)
		{
			armed = newArmed;
			this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
			markDirty();
		}
		return new Object[] { true };
	}

	@Callback
	public Object[] powerOn(Context context, Arguments args) {
		power = true;
		this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
		markDirty();
		return new Object[] { true };
	}

	void doPowerOff()
	{
		power = false;
		setPitch(pitch);
		setYaw(yaw);
		setShaft(shaft);
		this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
		markDirty();
	}

	@Callback
	public Object[] powerOff(Context context, Arguments args) {
		doPowerOff();
		return new Object[] { true };
	}

	@Callback(doc="function():table -- Fires the gun.  More damage means longer cooldown and more energy draw. Returns true for success and throws error with a message for failure")
	public Object[] fire(Context context, Arguments args) throws Exception {
		if (power) {
			if (!armed || barrel<1F) throw new IllegalArgumentException("Not armed");

			float p = getRealPitch();
			float a = getRealYaw() + (float)Math.PI;
			float damage = 3F;
			if (this.ItemStacks[0] != null && this.ItemStacks[0].getItem() instanceof ItemDamageUpgrade) {
				damage = damage * 3F;
			}
			if (this.ItemStacks[1] != null && this.ItemStacks[1].getItem() instanceof ItemDamageUpgrade) {
				damage = damage * 3F;
			}

			float energy = damage;
			if (this.ItemStacks[6] != null && this.ItemStacks[6].getItem() instanceof ItemEnergyUpgrade) {
				energy = energy * 0.7F;
			}
			if (this.ItemStacks[7] != null && this.ItemStacks[7].getItem() instanceof ItemEnergyUpgrade) {
				energy = energy * 0.7F;
			}

			if (this.tickCool > 0) {
				throw new IllegalArgumentException("gun hasn't cooled");
			}
			if (!((Connector)this.node).tryChangeBuffer(-energy*25)) {
				throw new IllegalArgumentException("not enough energy");
			}
			this.tickCool = 200;

			EntityEnergyBolt bolt = new EntityEnergyBolt(this.worldObj);
			float dY = 0.5F + (isUpright() ? 1F : -1F) * (0.125F + shaft*0.375F);
			bolt.setPosition(this.xCoord + 0.5F, this.yCoord + dY, this.zCoord + 0.5F);
			bolt.setHeading(a, p);
			bolt.setDamage(damage);

			soundName = "turretFire";
			setSound(soundName);

			if (!worldObj.isRemote)
				worldObj.playSoundEffect(this.xCoord, this.yCoord, this.zCoord, "opensecurity:" + this.soundName, 10 / 15 + 0.5F, 1.0F);

			this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
			markDirty();

			this.worldObj.spawnEntityInWorld(bolt);
			return new Object[] { true };
		} else {
			throw new IllegalArgumentException("powered off");
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

	private static String getComponentName() {
		return "os_energyturret";
	}

	private void write(NBTTagCompound tag) {
		super.writeToNBT(tag);
		if (node != null && node.host() == this) {
			final NBTTagCompound nodeNbt = new NBTTagCompound();
			node.save(nodeNbt);
			tag.setTag("oc:node", nodeNbt);
		}
		tag.setBoolean("powered", this.power);
		tag.setBoolean("armed", this.armed);
		tag.setFloat("yaw", this.yaw);
		tag.setFloat("syaw", this.setpointYaw);
		tag.setFloat("pitch", this.pitch);
		tag.setFloat("spitch", this.setpointPitch);
		tag.setFloat("shaft", this.shaft);
		tag.setFloat("sshaft", this.setShaft);
		tag.setFloat("barrel", this.barrel);
		tag.setInteger("cool", this.tickCool);
		writeSyncableDataToNBT(tag);

		NBTTagList var2 = new NBTTagList();
		for (int var3 = 0; var3 < this.ItemStacks.length; ++var3) {
			if (this.ItemStacks[var3] != null) {
				NBTTagCompound var4 = new NBTTagCompound();
				var4.setByte("Slot", (byte) var3);
				this.ItemStacks[var3].writeToNBT(var4);
				var2.appendTag(var4);
			}
		}
		tag.setTag("Items", var2);
	}

	private void read(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if (node != null && node.host() == this) {
			node.load(tag.getCompoundTag("oc:node"));
		}
		this.power = tag.getBoolean("powered");
		this.armed = tag.getBoolean("armed");
		this.yaw = tag.getFloat("yaw");
		this.setpointYaw = tag.getFloat("syaw");
		this.pitch = tag.getFloat("pitch");
		this.setpointPitch = tag.getFloat("spitch");
		this.shaft = tag.getFloat("shaft");
		this.setShaft = tag.getFloat("sshaft");
		this.barrel = tag.getFloat("barrel");
		this.tickCool = tag.getInteger("cool");
		readSyncableDataFromNBT(tag);

		NBTTagList var2 = tag.getTagList("Items", tag.getId());
		this.ItemStacks = new ItemStack[this.getSizeInventory()];
		for (int var3 = 0; var3 < var2.tagCount(); ++var3) {
			NBTTagCompound var4 = var2.getCompoundTagAt(var3);
			byte var5 = var4.getByte("Slot");
			if (var5 >= 0 && var5 < this.ItemStacks.length) {
				this.ItemStacks[var5] = ItemStack.loadItemStackFromNBT(var4);
			}
		}
	}

	private void readSyncableDataFromNBT(NBTTagCompound tag) {
		soundName = tag.getString("soundName");
		volume = tag.getFloat("volume");
	}

	private void writeSyncableDataToNBT(NBTTagCompound tag) {
		tag.setString("soundName", soundName);
		tag.setFloat("volume", volume);
	}

	public float getRealYaw() {
		//return ((float)Math.PI) * yaw / 180;
		if (pcl.opensecurity.OpenSecurity.cfg.turretReverseRotation)
			 return ((float)Math.PI) * (0 - yaw) / 180; // TODO: set legacy compatible offset (90? -90?)
		else
			 return ((float)Math.PI) * yaw / 180;
	}

	public float getRealPitch() {
		return ((float)Math.PI) * pitch / 180;
	}

	@Override
	public Node node() {
		return this.node;
	}

	@Override
	public void onConnect(Node arg0) { }

	@Override
	public void onDisconnect(Node arg0) { }

	@Override
	public void onMessage(Message arg0) { }

	@Override
	public boolean playSoundNow() {
		return false;
	}


	private ItemStack[] ItemStacks = new ItemStack[12];


	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return worldObj.getTileEntity(xCoord, yCoord, zCoord) == this && player.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5) < 64;
	}

	@Override
	public int getSizeInventory() {
		return this.ItemStacks.length;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return this.ItemStacks[i];
	}

	@Override
	public ItemStack decrStackSize(int slot, int amt) {
		ItemStack stack = getStackInSlot(slot);
		if (stack != null) {
			if (stack.stackSize <= amt) {
				setInventorySlotContents(slot, null);
			} else {
				stack = stack.splitStack(amt);
				if (stack.stackSize == 0) {
					setInventorySlotContents(slot, null);
				}
			}
		}
		return stack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		if (getStackInSlot(i) != null) {
			ItemStack var2 = getStackInSlot(i);
			setInventorySlotContents(i, null);
			return var2;
		} else {
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		this.ItemStacks[i] = itemstack;
		if (itemstack != null && itemstack.stackSize > this.getInventoryStackLimit()) {
			itemstack.stackSize = this.getInventoryStackLimit();
		}
	}

	@Override
	public String getInventoryName() {
		return "os_energyturret";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack item) {
		if((slot == 0 || slot == 1) && item.getItem() == ContentRegistry.damageUpgrade) return true;
		if((slot == 2 || slot == 3) && item.getItem() == ContentRegistry.movementUpgrade) return true;
		if((slot == 4 || slot == 5) && item.getItem() == ContentRegistry.cooldownUpgrade) return true;
		if((slot == 6 || slot == 7) && item.getItem() == ContentRegistry.energyUpgrade) return true;
		return false;
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox()
	{
	        if(isUpright())
			return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 0.75 + shaft*0.5, zCoord + 1);
		else
			return AxisAlignedBB.getBoundingBox(xCoord, yCoord+0.25-shaft*0.5, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
	}
}