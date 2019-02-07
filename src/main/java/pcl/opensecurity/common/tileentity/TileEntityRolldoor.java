package pcl.opensecurity.common.tileentity;

import pcl.opensecurity.common.blocks.BlockRolldoor;

public class TileEntityRolldoor extends TileEntityOSBase {

    public TileEntityRolldoor() {
        super(BlockRolldoor.NAME);
        //node = Network.newNode(this, Visibility.Network).create();
    }
}
