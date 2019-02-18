package pcl.opensecurity.common.entity;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import pcl.opensecurity.common.blocks.BlockEnergyTurret;

import javax.annotation.Nonnull;
import java.util.HashSet;

public class EntityEnergyBolt extends EntityThrowable {
	public static final String NAME = "energybolt";

	private int life = 20 * 5;
	private float damage = 0.0F;
	private static DamageSource energy = new DamageSource(NAME).setProjectile();

	private static HashSet<Material> passableMaterials = new HashSet<>();
	private static HashSet<Material> breakAbleMaterials = new HashSet<>();

	private static final DataParameter<Boolean> NOTICEMESENPAI = EntityDataManager.<Boolean>createKey(EntityEnergyBolt.class, DataSerializers.BOOLEAN);

	static {
		passableMaterials.add(Material.AIR);
		passableMaterials.add(Material.GLASS);
		passableMaterials.add(Material.FIRE);
		passableMaterials.add(Material.WATER);
		passableMaterials.add(Material.LAVA);

		//todo: allow breaking of harder materials with upgrades installed?!
		breakAbleMaterials.add(Material.GRASS);
		breakAbleMaterials.add(Material.LEAVES);
		breakAbleMaterials.add(Material.VINE);
		breakAbleMaterials.add(Material.WEB);
	}

	public EntityEnergyBolt(World world) {
		super(world);
		setSize(0.5F, 0.5F);
		setNoGravity(true);
		setEntityInvulnerable(true);
	}

	public void setHeading(float yaw, float pitch) {
		setVelocity(Math.sin(yaw) * Math.cos(pitch), Math.sin(pitch), Math.cos(yaw) * Math.cos(pitch));
	}

	public void setDamage(float damageIn) {
		this.damage = damageIn;
	}

	protected void entityInit() {
		this.dataManager.register(NOTICEMESENPAI, true);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound tag) {
		super.readEntityFromNBT(tag);
		this.damage = tag.getFloat("damage");
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound tag) {
		super.writeEntityToNBT(tag);
		tag.setFloat("damage", this.damage);
	}

	@Override
	protected boolean canTriggerWalking() {
		return false;
	}

	private boolean canPassBlock(IBlockState blockState){
		return passableMaterials.contains(blockState.getMaterial()) || blockState.getBlock() instanceof BlockEnergyTurret;
	}

	public void onUpdate() {
		super.onUpdate();

		if (0 >= --this.life)
			setDead();
	}

	@Override
	protected void onImpact(@Nonnull RayTraceResult result) {
		switch(result.typeOfHit){
			case ENTITY:
				result.entityHit.attackEntityFrom(energy, this.damage);
				setDead();
				break;
			case BLOCK:
				IBlockState state = getEntityWorld().getBlockState(result.getBlockPos());
				if(breakAbleMaterials.contains(state.getMaterial())) {
					//todo: put block drops to world?!
					getEntityWorld().setBlockToAir(result.getBlockPos());
					setDead();
				}
				else if(!canPassBlock(state)) {
					setDead();
				}
		}
	}

	@Override
	public boolean shouldRenderInPass(int pass){
		return pass == 1;
	}

}