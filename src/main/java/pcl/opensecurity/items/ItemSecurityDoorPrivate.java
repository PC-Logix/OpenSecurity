package pcl.opensecurity.items;

import pcl.opensecurity.ContentRegistry;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.tileentity.TileEntitySecureDoor;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemSecurityDoorPrivate extends ItemDoor {
	public Block doorBlock;

	public ItemSecurityDoorPrivate(Block block) {
		super(Material.iron);
		this.doorBlock = block;
		this.maxStackSize = 16;
		this.setUnlocalizedName("securityDoorPrivate");
		this.setTextureName("opensecurity:door_secure_nowindow");
		this.setCreativeTab(ContentRegistry.CreativeTab);
	}


    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
        if (par7 != 1)
        {
            return false;
        }
        else
        {
            ++par5;
            Block block;

            block = ContentRegistry.SecurityDoorPrivateBlock;

            if (par2EntityPlayer.canPlayerEdit(par4, par5, par6, par7, par1ItemStack) && par2EntityPlayer.canPlayerEdit(par4, par5 + 1, par6, par7, par1ItemStack))
            {
                if (!block.canPlaceBlockAt(par3World, par4, par5, par6))
                {
                    return false;
                }
                else
                {
                    int i1 = MathHelper.floor_double((double)((par2EntityPlayer.rotationYaw + 180.0F) * 4.0F / 360.0F) - 0.5D) & 3;
                    placeDoorBlock(par3World, par4, par5, par6, i1, block, par2EntityPlayer);
                    --par1ItemStack.stackSize;
                    return true;
                }
            }
            else
            {
                return false;
            }
        }
    }

    public static void placeDoorBlock(World world, int x, int y, int z, int direction, Block block, EntityPlayer entityPlayer)
    {
        byte b0 = 0;
        byte b1 = 0;

        if (direction == 0)
        {
            b1 = 1;
        }

        if (direction == 1)
        {
            b0 = -1;
        }

        if (direction == 2)
        {
            b1 = -1;
        }

        if (direction == 3)
        {
            b0 = 1;
        }

        int i1 = (world.getBlock(x - b0, y, z - b1).isNormalCube() ? 1 : 0) + (world.getBlock(x - b0, y + 1, z - b1).isNormalCube() ? 1 : 0);
        int j1 = (world.getBlock(x + b0, y, z + b1).isNormalCube() ? 1 : 0) + (world.getBlock(x + b0, y + 1, z + b1).isNormalCube() ? 1 : 0);
        boolean flag = world.getBlock(x - b0, y, z - b1) == block || world.getBlock(x - b0, y + 1, z - b1) == block;
        boolean flag1 = world.getBlock(x + b0, y, z + b1) == block || world.getBlock(x + b0, y + 1, z + b1) == block;
        boolean flag2 = false;

        if (flag && !flag1)
        {
            flag2 = true;
        }
        else if (j1 > i1)
        {
            flag2 = true;
        }

        world.setBlock(x, y, z, block, direction, 2);
        world.setBlock(x, y + 1, z, block, 8 | (flag2 ? 1 : 0), 2);
        TileEntitySecureDoor tile = (TileEntitySecureDoor) world.getTileEntity(x, y, z);
        tile.setOwner(entityPlayer.getUniqueID().toString());
        TileEntitySecureDoor tileTop = (TileEntitySecureDoor) world.getTileEntity(x, y + 1, z);
        tileTop.setOwner(entityPlayer.getUniqueID().toString());
        world.notifyBlocksOfNeighborChange(x, y, z, block);
        world.notifyBlocksOfNeighborChange(x, y + 1, z, block);
    }
}