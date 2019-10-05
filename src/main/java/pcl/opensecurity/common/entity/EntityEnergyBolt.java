package pcl.opensecurity.common.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import pcl.opensecurity.common.blocks.BlockEnergyTurret;

import javax.annotation.Nonnull;
import java.util.*;

public class EntityEnergyBolt extends EntityThrowable {
	public static final String NAME = "opensecurity.energybolt";

	private int life = 20 * 5;
	private float damage = 0.0F;
	private static DamageSource energy = new DamageSource(NAME).setProjectile();

	private static HashSet<Material> passableMaterials = new HashSet<>();
	private static HashSet<Material> breakAbleMaterials = new HashSet<>();

	private static final DataParameter<Boolean> NOTICEMESENPAI = EntityDataManager.<Boolean>createKey(EntityEnergyBolt.class, DataSerializers.BOOLEAN);

	private FakePlayer fakePlayer;

	static {
		passableMaterials.add(Material.AIR);
		passableMaterials.add(Material.GLASS);
		passableMaterials.add(Material.FIRE);
		passableMaterials.add(Material.WATER);
		passableMaterials.add(Material.LAVA);

		//todo: allow breaking of harder materials with upgrades installed?!
		breakAbleMaterials.add(Material.PLANTS);
		breakAbleMaterials.add(Material.LEAVES);
		breakAbleMaterials.add(Material.VINE);
		breakAbleMaterials.add(Material.WEB);
	}

	public EntityEnergyBolt(World world) {
		super(world);
		setSize(0.5F, 0.5F);
		setNoGravity(true);
		setEntityInvulnerable(true);

		if(!world.isRemote)
			fakePlayer = new FakePlayer(FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(world.provider.getDimension()), new GameProfile(UUID.randomUUID(), NAME));
	}


	public void setHeading(float yaw, float pitch) {
		// dont use setVelocity() because its client side only and wouldnt work on dedicated servers
		this.motionX = Math.sin(yaw) * Math.cos(pitch);
		this.motionY = Math.sin(pitch);
		this.motionZ = Math.cos(yaw) * Math.cos(pitch);
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

	private boolean canPassBlock(World world, BlockPos pos){
		IBlockState state = world.getBlockState(pos);
		return passableMaterials.contains(state.getMaterial()) || state.getBlock() instanceof BlockEnergyTurret;
	}

	@Override
	public void onEntityUpdate() {
		super.onEntityUpdate();

		if (0 >= --this.life) {
			setDead();
		}
	}

	boolean breakBlock(World world, BlockPos pos){
		IBlockState state = world.getBlockState(pos);

		if(world.isRemote)
			return false;

		if(!breakAbleMaterials.contains(state.getMaterial()))
			return false;

		Block block = state.getBlock();
		if (block.removedByPlayer(state, world, pos, fakePlayer, true)) {
			TileEntity tile = world.getTileEntity(pos);
			block.onBlockDestroyedByPlayer(world, pos, state);
			block.harvestBlock(world, fakePlayer, pos, state, tile, getDiamondHoe());
		}

		return true;
	}

	ItemStack setToolDefaultEnchants(ItemStack stack){
		HashMap<Enchantment, Integer> enchantments = new HashMap<>();
		enchantments.put(Enchantments.FORTUNE, 1);
		EnchantmentHelper.setEnchantments(enchantments, stack);
		return stack;
	}

	ItemStack getDiamondHoe(){
		return setToolDefaultEnchants(new ItemStack(Items.DIAMOND_HOE));
	}

	ItemStack getDiamondPick(){
		return setToolDefaultEnchants(new ItemStack(Items.DIAMOND_PICKAXE));
	}

	ItemStack getDiamondSword(){
		return setToolDefaultEnchants(new ItemStack(Items.DIAMOND_SWORD));
	}

	@Override
	protected void onImpact(@Nonnull RayTraceResult result) {
		if(getEntityWorld().isRemote)
			return;

		switch(result.typeOfHit){
			case ENTITY:
				//todo: attack as fake player?!
				//if(result.entityHit instanceof EntityLivingBase)
				//	getDiamondSword().hitEntity((EntityLivingBase) result.entityHit, fakePlayer);
				// else
				result.entityHit.attackEntityFrom(energy, this.damage);
				setDead();
				break;
			case BLOCK:
				if(breakBlock(getEntityWorld(), result.getBlockPos()))
					setDead();
				if(!canPassBlock(getEntityWorld(), result.getBlockPos()))
					setDead();
				break;
		}
	}

	@Override
	public boolean shouldRenderInPass(int pass){
		return pass == 1;
	}

}