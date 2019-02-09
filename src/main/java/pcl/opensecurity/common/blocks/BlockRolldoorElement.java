package pcl.opensecurity.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.common.ContentRegistry;

public class BlockRolldoorElement extends Block {
    public final static String NAME = "rolldoor_element";

    public BlockRolldoorElement(){
        super(Material.IRON);
        setUnlocalizedName(NAME);
        setRegistryName(OpenSecurity.MODID, NAME);
        setHardness(0.5f);
        setCreativeTab(ContentRegistry.creativeTab);
    }


}
