package pcl.opensecurity.blocks;

import pcl.opensecurity.ContentRegistry;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.tileentity.TileEntityEnergyTurret;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockEnergyTurret
  extends Block
  implements ITileEntityProvider
{
  public BlockEnergyTurret()
  {
    super(Material.anvil);
    setHardness(6.0F);
    setStepSound(soundTypeMetal);
    setBlockName("energyTurret");
  }
  
  public TileEntity createNewTileEntity(World world, int metadata)
  {
    return new TileEntityEnergyTurret();
  }
  
  public int getRenderType()
  {
    return -1;
  }
  
  public boolean isOpaqueCube()
  {
    return false;
  }
  
  public boolean renderAsNormalBlock()
  {
    return false;
  }
  
  public IIcon getIcon(int side, int meta)
  {
    return Blocks.iron_block.getIcon(side, meta);
  }
}