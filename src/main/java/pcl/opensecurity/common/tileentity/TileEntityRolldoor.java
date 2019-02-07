package pcl.opensecurity.common.tileentity;

import net.minecraft.util.math.AxisAlignedBB;
import pcl.opensecurity.common.blocks.BlockRolldoor;
import pcl.opensecurity.util.IOwner;
import pcl.opensecurity.util.IPasswordProtected;

import java.util.UUID;

import static net.minecraft.block.Block.FULL_BLOCK_AABB;

public class TileEntityRolldoor extends TileEntityOSBase implements IOwner, IPasswordProtected {
    final static int MAX_LENGTH = 16;

    private UUID ownerUUID;
    private String password = "";

    private int height = 0;
    private double currentPosition = 0;
    private double speed = 0;

    private AxisAlignedBB bb = FULL_BLOCK_AABB;



    public TileEntityRolldoor() {
        super("os_" + BlockRolldoor.NAME);
        updateHeight();
    }

    @Override
    public void update(){
        super.update();

        if(speed == 0)
            return;
    }

    public void open(){
        speed = -0.1;
    }

    public void close(){
        updateHeight();
        speed = 0.1;
    }

    public void toggle(){
        speed*=-1;
    }

    public void updateHeight(){
        height = 0;
        while(getWorld().isAirBlock(getPos().add(0, -(1 + height), 0)))
            height++;

        bb = FULL_BLOCK_AABB.grow(0, -height(), 0);
    }

    public int height(){
        return height;
    }

    public AxisAlignedBB getBoundingBox(){
        return bb;
    }

    public void setOwner(UUID uuid){
        ownerUUID = uuid;
    }

    public UUID getOwner(){
        return ownerUUID;
    }

    public void setPassword(String pass){
        password = pass;
    }

    public String getPass(){ return password; }

    public boolean isOpen(){
        return speed != 0 && currentPosition == height;
    }


}
