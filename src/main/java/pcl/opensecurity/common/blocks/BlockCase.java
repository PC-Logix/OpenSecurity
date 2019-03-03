package pcl.opensecurity.common.blocks;

import li.cil.oc.common.block.Case;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.common.ContentRegistry;

public class BlockCase extends Case {
    public static BlockCase DEFAULTITEM_TIER1;
    public static BlockCase DEFAULTITEM_TIER2;
    public static BlockCase DEFAULTITEM_TIER3;

    public BlockCase(String name, int tier){
        super(tier);
        setHardness(2.0f);
        setResistance(1000000);
        setRegistryName(OpenSecurity.MODID, name);
        setUnlocalizedName(name);
        setCreativeTab(ContentRegistry.creativeTab);
    }
}
