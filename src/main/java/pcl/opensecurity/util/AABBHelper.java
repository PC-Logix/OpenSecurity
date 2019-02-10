package pcl.opensecurity.util;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;

public class AABBHelper {
    // no idea why the heck this wont work as switch for EnumFacing, so we use the ordinal() values -.-

    public static AxisAlignedBB rotateVertical(AxisAlignedBB bb, EnumFacing newFacing){
        //allways assume bb facing NORTH as input
        switch(newFacing.ordinal()){
            case 1: return new AxisAlignedBB(bb.minX, 1d-bb.minZ, bb.minY, bb.maxX, 1d-bb.maxZ, bb.maxY); // 90  (down)
            case 0: return new AxisAlignedBB(bb.minX, bb.minZ, 1d-bb.minY, bb.maxX, bb.maxZ, 1d-bb.maxY); // 270(up)
            default: return bb;
        }
    }

    public static AxisAlignedBB rotateHorizontal(AxisAlignedBB bb, EnumFacing newFacing){
        //allways assume bb facing NORTH as input
        switch(newFacing.ordinal()){
            case 4: return new AxisAlignedBB(bb.minZ, bb.minY, 1d - bb.minX, bb.maxZ, bb.maxY, 1d - bb.maxX); // (east)
            case 5: return new AxisAlignedBB(1d - bb.maxZ, bb.minY, bb.minX, 1d - bb.minZ, bb.maxY, bb.maxX); // (west)
            case 3: return new AxisAlignedBB(1d - bb.maxX, bb.minY, 1d - bb.maxZ, 1d - bb.minX, bb.maxY, 1d - bb.minZ); // (south)
            case 2: default: return bb; // (north)
        }
    }

}
