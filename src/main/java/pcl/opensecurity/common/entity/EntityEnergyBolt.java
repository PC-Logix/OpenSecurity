package pcl.opensecurity.common.entity;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pcl.opensecurity.common.blocks.BlockEnergyTurret;

import java.util.List;

public class EntityEnergyBolt extends EntityThrowable {
	private int life = 600;
	private float yaw = 0.0F;
	private float pitch = 0.0F;
	private float damage = 0.0F;
	private static DamageSource energy = new DamageSource("boltComputer");
	static { energy.setProjectile(); }

	public EntityEnergyBolt(World world) {
		super(world);
		setSize(0.5F, 0.5F);
	}

	public void setHeading(float yaw, float pitch) {
		this.motionX = Math.sin(yaw) * Math.cos(pitch);
		this.motionY = Math.sin(pitch);
		this.motionZ = Math.cos(yaw) * Math.cos(pitch);
	}

	public void setDamage(float damageIn) {
		this.damage = damageIn;
	}

	protected void entityInit() { 

	}

	@Override
	public void readEntityFromNBT(NBTTagCompound tag) {
		this.yaw = tag.getFloat("yaw");
		this.pitch = tag.getFloat("pitch");
		this.damage = tag.getFloat("damage");
	}
	@Override
	public void writeEntityToNBT(NBTTagCompound tag) {
		tag.setFloat("yaw", this.yaw);
		tag.setFloat("pitch", this.pitch);
		tag.setFloat("damage", this.damage);
	}
	@Override
	protected boolean canTriggerWalking() {
		return false;
	}

	@SideOnly(Side.CLIENT)
	public float getShadowSize() {
		return 0.0F;
	}

	public void onUpdate() {
		super.onUpdate();
		if (0 >= --this.life) {
			this.isDead = true;
		}

		if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
			float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
			this.rotationYaw = (float)(MathHelper.atan2(this.motionX, this.motionZ) * (180D / Math.PI));
			this.rotationPitch = (float)(MathHelper.atan2(this.motionY, (double)f) * (180D / Math.PI));
			this.prevRotationYaw = this.rotationYaw;
			this.prevRotationPitch = this.rotationPitch;
		}
		BlockPos blockPos = new BlockPos((int)Math.floor(this.posX), (int)Math.floor(this.posY), (int)Math.floor(this.posZ));
		if ((!this.world.isAirBlock(blockPos)) &&
				!(this.world.getBlockState(blockPos).getBlock().equals(Blocks.GLASS)) &&
				!(this.world.getBlockState(blockPos).getBlock().equals(Blocks.GLASS_PANE)) &&
				!(this.world.getBlockState(blockPos).getBlock().equals(Blocks.STAINED_GLASS)) &&
				!(this.world.getBlockState(blockPos).getBlock().equals(Blocks.STAINED_GLASS_PANE)) &&
				(!(this.world.getBlockState(blockPos).getBlock() instanceof BlockEnergyTurret))) {
			this.isDead = true;
			}
		List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().grow(this.motionX, this.motionY, this.motionZ).expand(1.0D,1.0D,1.0D));
		if (!list.isEmpty()) {
			list.get(0).attackEntityFrom(energy, this.damage);
			this.isDead = true;
		}
		this.posX += this.motionX;
		this.posY += this.motionY;
		this.posZ += this.motionZ;

		setPosition(this.posX, this.posY, this.posZ);
		doBlockCollisions();
	}

	public float getYaw() {
		return this.yaw;
	}

	public float getPitch() {
		return this.pitch;
	}

	@Override
	protected void onImpact(RayTraceResult result) {
		// TODO Auto-generated method stub

	}

	@Override
	protected float getGravityVelocity()
	{
		return 0.00F;
	}
}