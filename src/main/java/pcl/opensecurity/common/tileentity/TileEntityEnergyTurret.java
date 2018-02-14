package pcl.opensecurity.common.tileentity;

import javax.annotation.Nullable;

import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Connector;
import li.cil.oc.api.network.Visibility;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.common.ContentRegistry;
import pcl.opensecurity.common.SoundHandler;
import pcl.opensecurity.common.entity.EntityEnergyBolt;
import pcl.opensecurity.common.items.ItemCooldownUpgrade;
import pcl.opensecurity.common.items.ItemDamageUpgrade;
import pcl.opensecurity.common.items.ItemEnergyUpgrade;
import pcl.opensecurity.common.items.ItemMovementUpgrade;

public class TileEntityEnergyTurret extends TileEntityOSBase implements IInventory {

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
	public boolean power = false;
	public boolean armed = false;
	boolean upRight = true;

	private ItemStack[] ItemStacks = new ItemStack[12];

	public TileEntityEnergyTurret() { 
		super();
		setSound(soundName); 
		node = Network.newNode(this, Visibility.Network).withComponent(getComponentName()).withConnector(32).create();
	}

	public boolean isUpright()
	{
		return upRight;
	}

	public float getRealYaw() {
		//return ((float)Math.PI) * yaw / 180;
		if (OpenSecurity.cfg.turretReverseRotation)
			return ((float)Math.PI) * (0 - yaw) / 180; // TODO: set legacy compatible offset (90? -90?)
		else
			return ((float)Math.PI) * yaw / 180;
	}

	public float getRealPitch() {
		return ((float)Math.PI) * pitch / 180;
	}

	public int getSizeInventory() {
		return this.ItemStacks.length;
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

	public void rescan(BlockPos pos) {
		IBlockState blockDown = world.getBlockState(this.pos.offset(EnumFacing.DOWN));
		IBlockState blockUp = world.getBlockState(this.pos.offset(EnumFacing.UP));

		if (blockDown.getBlock() instanceof Block && !blockDown.getMaterial().equals(Material.AIR)) {
			upRight = true;
		}

		if (blockUp.getBlock()  instanceof Block && !blockUp.getMaterial().equals(Material.AIR)) {
			upRight = false;
		}
	}

	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		read(tag);
	}

	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		return write(tag);
	}

	private static String getComponentName() {
		return "os_energyturret";
	}

	private NBTTagCompound write(NBTTagCompound tag) {
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
		tag.setBoolean("upright", upRight);
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
		return tag;
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
		this.upRight = tag.getBoolean("upright");
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

	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
	}

	@Nullable
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound nbtTag = new NBTTagCompound();
		this.writeToNBT(nbtTag);
		return new SPacketUpdateTileEntity(getPos(), 1, nbtTag);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		// Here we get the packet from the server and read it into our client side tile entity
		this.readFromNBT(packet.getNbtCompound());
	}

	@Override
	public void update() {
		super.update();
		boolean upright = isUpright();
		boolean shaftLengthValid = true;
		boolean moveSound = false;

		movePerTick = 4;

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
			if (power && !this.node.tryChangeBuffer(-10)) {
				doPowerOff();
			}
		}
		if (this.ItemStacks[2] != null && this.ItemStacks[2].getItem() instanceof ItemMovementUpgrade) {
			movePerTick = movePerTick + 2.5F;
		}
		if (this.ItemStacks[3] != null && this.ItemStacks[3].getItem() instanceof ItemMovementUpgrade) {
			movePerTick = movePerTick + 2.5F;
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
				world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundHandler.turretMove, SoundCategory.BLOCKS, 15 / 15 + 0.5F, 1.0F, false);
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
		int otherY = this.pos.getY()+(isUpright()?1:-1);
		if(newExt>maxShaftLengthForOneBlock && (otherY<0 || otherY>255 /*|| world.isAirBlock(this.pos.getX(), otherY, this.pos.getZ())*/))
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

			this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 2);
			getUpdateTag();
			markDirty();
		}

		return newlen;
	}

	public void setShouldStart(boolean b) {
		shouldPlay = b;
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
	public Object[] extendShaft(Context context, Arguments args) {
		float setTo = setShaft((float)args.checkDouble(0));
		this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 2);
		getUpdateTag();
		markDirty();
		return new Object[] { setTo };
	}
	@Callback(doc="function():boolean -- Get gun shaft extension", direct=true)
	public Object[] getShaftLength(Context context, Arguments args) {
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
			this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 2);
			getUpdateTag();
			markDirty();
			return new Object[] { true };	
		} else {
			throw new Exception("powered off");
		}
	}

	@Callback(doc="function(yaw:number, pitch:number) -- Changes the gun's setpoint (Yaw ranges (0.0..360) Pitch ranges (-45..90))")
	public Object[] moveBy(Context context, Arguments args) throws Exception {
		if (power) {
			soundName = "turretMove";
			setSound(soundName);
			this.setShouldStart(true);
			setYaw((float)args.checkDouble(0) + yaw);
			setPitch((float)args.checkDouble(1) + pitch);
			this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 2);
			getUpdateTag();
			markDirty();
			return new Object[] { true };	
		} else {
			throw new Exception("powered off");
		}
	}

	@Callback(doc="function(yaw:number, pitch:number) -- Changes the gun's setpoint in radians")
	public Object[] moveToRadians(Context context, Arguments args) throws Exception {
		if (power) {
			soundName = "turretMove";
			setSound(soundName);
			this.setShouldStart(true);

			double rad = args.checkDouble(0);
			double deg = rad*180/Math.PI;

			double rad2 = args.checkDouble(1);
			double deg2 = rad2*180/Math.PI;

			setYaw((float)deg);
			setPitch((float)deg2);

			this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 2);
			getUpdateTag();
			markDirty();
			return new Object[] { true };	
		} else {
			throw new Exception("powered off");
		}
	}

	@Callback
	public Object[] setArmed(Context context, Arguments args) {
		boolean newArmed = args.checkBoolean(0);
		if (armed!=newArmed)
		{
			armed = newArmed;
			this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 2);
			getUpdateTag();
			markDirty();
		}
		return new Object[] { true };
	}

	@Callback
	public Object[] powerOn(Context context, Arguments args) {
		power = true;
		this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 2);
		getUpdateTag();
		markDirty();
		return new Object[] { true };
	}

	void doPowerOff() {
		power = false;
		setPitch(pitch);
		setYaw(yaw);
		setShaft(shaft);
		this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 2);
		getUpdateTag();
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
			if (!armed || barrel<1F) throw new Exception("Not armed");

			float damage = 5F;

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
				throw new Exception("gun hasn't cooled");
			}

			
			if (!(this.node).tryChangeBuffer(-energy*2)) {
				throw new Exception("not enough energy");
			}

			this.tickCool = 100;
			float p = getRealPitch();
			float a = getRealYaw() + (float)Math.PI;
			EntityEnergyBolt bolt = new EntityEnergyBolt(this.world);
			float dY = 0.5F + (isUpright() ? 1F : -1F) * (0.125F + shaft*0.375F);
			bolt.setPosition(this.pos.getX() + 0.5F, this.pos.getY() + dY, this.pos.getZ() + 0.5F);
			bolt.setHeading(a, p);
			bolt.setDamage(damage);
			world.playSound(null, this.pos.getX() + 0.5F, this.pos.getY() + dY, this.pos.getZ() + 0.5F, SoundHandler.turretFire, SoundCategory.BLOCKS, 15 / 15 + 0.5F, 1.0F);

			this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 2);
			getUpdateTag();
			markDirty();

			this.world.spawnEntity(bolt);
			return new Object[] { true };
		} else {
			throw new Exception("powered off");
		}

	}

	@Override
	public String getName() {
		return "os_energyturret";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return this.ItemStacks[index];
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
	public ItemStack removeStackFromSlot(int i) {
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
	public int getInventoryStackLimit() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void closeInventory(EntityPlayer player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack item) {
		if((slot == 0 || slot == 1) && item.getItem() == ContentRegistry.damageUpgradeItem) return true;
		if((slot == 2 || slot == 3) && item.getItem() == ContentRegistry.movementUpgradeItem) return true;
		if((slot == 4 || slot == 5) && item.getItem() == ContentRegistry.cooldownUpgradeItem) return true;
        return (slot == 6 || slot == 7) && item.getItem() == ContentRegistry.energyUpgradeItem;
    }
	
	@Override
	public int getField(int id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setField(int id, int value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getFieldCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

}
