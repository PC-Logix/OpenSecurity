package pcl.opensecurity.entity;

import pcl.opensecurity.blocks.BlockEnergyTurret;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityEnergyBolt
  extends Entity
{
  private int life = 600;
  private float yaw = 0.0F;
  private float pitch = 0.0F;
  private float damage = 0.0F;
  private static DamageSource energy = new DamageSource("boltComputer");
  
  static
  {
    energy.setProjectile();
  }
  
  public EntityEnergyBolt(World world)
  {
    super(world);
    setSize(0.5F, 0.5F);
  }
  
  public void setHeading(float yaw, float pitch)
  {
    DataWatcher d = getDataWatcher();
  //This will crash when run on a dedicated server, because Entity#setVelocity is marked with @SideOnly(Side.CLIENT), and therefore stripped from a dedicated server instance.
    //setVelocity((float)(-Math.sin(yaw) * Math.cos(pitch)), (float)-Math.sin(pitch), (float)(Math.cos(yaw) * Math.cos(pitch)));
    this.motionX = -Math.sin(yaw) * Math.cos(pitch);
    this.motionY = -Math.sin(pitch);
    this.motionZ = Math.cos(yaw) * Math.cos(pitch);
    d.updateObject(19, Float.valueOf(this.yaw = yaw));
    d.updateObject(20, Float.valueOf(this.pitch = pitch));
  }
  
  public void setDamage(float damage)
  {
    getDataWatcher().updateObject(21, Float.valueOf(this.damage = damage));
  }
  
  protected void entityInit()
  {
    DataWatcher d = getDataWatcher();
    d.addObject(19, Float.valueOf(this.yaw));
    d.addObject(20, Float.valueOf(this.pitch));
    d.addObject(21, Float.valueOf(this.damage));
  }
  
  protected void readEntityFromNBT(NBTTagCompound tag)
  {
    this.yaw = tag.getFloat("yaw");
    this.pitch = tag.getFloat("pitch");
  }
  
  protected void writeEntityToNBT(NBTTagCompound tag)
  {
    tag.setFloat("yaw", this.yaw);
    tag.setFloat("pitch", this.pitch);
  }
  
  protected boolean canTriggerWalking()
  {
    return false;
  }
  
  @SideOnly(Side.CLIENT)
  public float getShadowSize()
  {
    return 0.0F;
  }
  
  public void onUpdate()
  {
    super.onUpdate();
    if (0 >= --this.life) {
      this.isDead = true;
    }
    DataWatcher d = getDataWatcher();
    this.yaw = d.getWatchableObjectFloat(19);
    this.pitch = d.getWatchableObjectFloat(20);
    this.damage = d.getWatchableObjectFloat(21);
    if ((!this.worldObj.isAirBlock((int)Math.floor(this.posX), (int)Math.floor(this.posY), (int)Math.floor(this.posZ))) && 
      (!(this.worldObj.getBlock((int)Math.floor(this.posX), (int)Math.floor(this.posY), (int)Math.floor(this.posZ)) instanceof BlockEnergyTurret))) {
      this.isDead = true;
    }
    List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox);
    if (list.size() > 0)
    {
      ((Entity)list.get(0)).attackEntityFrom(energy, this.damage);
      this.isDead = true;
    }
    this.posX += this.motionX;
    this.posY += this.motionY;
    this.posZ += this.motionZ;
    
    setPosition(this.posX, this.posY, this.posZ);
    func_145775_I();
  }
  
  public float getYaw()
  {
    return this.yaw;
  }
  
  public float getPitch()
  {
    return this.pitch;
  }
}
