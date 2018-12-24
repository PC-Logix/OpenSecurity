package pcl.opensecurity.common.protection;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;


public class ProtectionAreaChunk {
    BlockPos controller;
    AxisAlignedBB area;

    public ProtectionAreaChunk(AxisAlignedBB areaIn, BlockPos controllerPosition){
        area = areaIn;
        controller = controllerPosition;
    }

    public ProtectionAreaChunk(NBTTagCompound nbt){
        readFromNBT(nbt);
    }

    public AxisAlignedBB getArea() {
        return area;
    }

    public BlockPos getControllerPosition() {
        return controller;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt){
        //area
        nbt.setInteger("x1", (int) area.minX);
        nbt.setInteger("y1", (int) area.minY);
        nbt.setInteger("z1", (int) area.minZ);
        nbt.setInteger("x2", (int) area.maxX);
        nbt.setInteger("y2", (int) area.maxY);
        nbt.setInteger("z2", (int) area.maxZ);

        //controller
        nbt.setInteger("xC", controller.getX());
        nbt.setInteger("yC", controller.getY());
        nbt.setInteger("zC", controller.getZ());

        return nbt;
    }

    public boolean intersects(BlockPos blockPos){
        return intersects(new AxisAlignedBB(blockPos));
    }

    public boolean intersects(AxisAlignedBB area){
        return area.intersects(getArea());
    }

    public void readFromNBT(NBTTagCompound nbt){
        area = new AxisAlignedBB(nbt.getInteger("x1"), nbt.getInteger("y1"), nbt.getInteger("z1"),
                nbt.getInteger("x2"), nbt.getInteger("y2"), nbt.getInteger("z2"));

        controller = new BlockPos(nbt.getInteger("xC"), nbt.getInteger("yC"), nbt.getInteger("zC"));
    }
}
