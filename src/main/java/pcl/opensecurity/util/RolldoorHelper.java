package pcl.opensecurity.util;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import pcl.opensecurity.common.tileentity.TileEntityRolldoor;
import pcl.opensecurity.common.tileentity.TileEntityRolldoorController;

import java.util.ArrayList;
import java.util.HashMap;

public class RolldoorHelper {
    public static HashMap<BlockPos, TileEntityRolldoor> getDoors(TileEntityRolldoorController controller) {
        HashMap<BlockPos, TileEntityRolldoor> doors = new HashMap<>();

        TileEntityRolldoor firstElement = getAdjacentRolldoor(controller);

        if(firstElement == null)
            return doors;

        doors.put(firstElement.getPos(), firstElement);

        ArrayList<EnumFacing> faces = getAdjacentRolldoors(firstElement.getWorld(), firstElement.getPos());

        if(faces.size() == 0) // no doors adjacent to the first rolldoor block
            return doors;

        Vec3i searchVector = faces.get(0).getDirectionVec(); // do further searchs only on the same axis

        BlockPos searchPos = firstElement.getPos();
        for(int i=0; doors.size() < TileEntityRolldoor.MAX_LENGTH; i++){
            searchPos = searchPos.add(searchVector);
            TileEntity tile = firstElement.getWorld().getTileEntity(searchPos);
            if(tile instanceof TileEntityRolldoor)
                doors.put(searchPos, (TileEntityRolldoor) tile);
            else
                break;
        }

        searchPos = firstElement.getPos();
        for(int i=0; doors.size() < TileEntityRolldoor.MAX_LENGTH; i--){
            searchPos = searchPos.subtract(searchVector);
            TileEntity tile = firstElement.getWorld().getTileEntity(searchPos);
            if(tile instanceof TileEntityRolldoor)
                doors.put(searchPos, (TileEntityRolldoor) tile);
            else
                break;
        }


        return doors;
    }

    public static ArrayList<EnumFacing> getAdjacentRolldoors(World world, BlockPos pos) {
        ArrayList<EnumFacing> doors = new ArrayList<>();

        for (EnumFacing direction : EnumFacing.VALUES)
            if (world.getTileEntity(pos.add(direction.getDirectionVec())) instanceof TileEntityRolldoor)
                doors.add(direction);

        return doors;
    }

    public static TileEntityRolldoor getAdjacentRolldoor(TileEntity controller){
        ArrayList<EnumFacing> faces = RolldoorHelper.getAdjacentRolldoors(controller.getWorld(), controller.getPos());

        if(faces.size() == 0) //no door adjacent to controller
            return null;

        return (TileEntityRolldoor) controller.getWorld().getTileEntity(controller.getPos().add(faces.get(0).getDirectionVec()));
    }

}


