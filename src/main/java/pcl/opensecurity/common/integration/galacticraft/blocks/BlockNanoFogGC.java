package pcl.opensecurity.common.integration.galacticraft.blocks;

import micdoodle8.mods.galacticraft.api.block.IPartialSealableBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pcl.opensecurity.common.blocks.BlockNanoFog;

// this is for supporting GalactiCraft sealable Blocks
public class BlockNanoFogGC extends BlockNanoFog implements IPartialSealableBlock {
    public boolean isSealed(World var1, BlockPos var2, EnumFacing var3){
        return true;
    }

}
