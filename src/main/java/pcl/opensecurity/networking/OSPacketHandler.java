package pcl.opensecurity.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class OSPacketHandler implements IMessage {
    int x, y, z;
    int isEnabled = 0;
	int side;

    public OSPacketHandler() {
    }

    public OSPacketHandler(int i, int x, int y, int z, int isEnabled) {
        this.side = i;
        this.x = x;
        this.y = y;
        this.z = z;
        this.isEnabled = isEnabled;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        side = ByteBufUtils.readVarInt(buf, 1);
        x = ByteBufUtils.readVarInt(buf, 5);
        y = ByteBufUtils.readVarInt(buf, 5);
        z = ByteBufUtils.readVarInt(buf, 5);
        isEnabled = ByteBufUtils.readVarInt(buf, 1);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeVarInt(buf, side, 1);
        ByteBufUtils.writeVarInt(buf, x, 5);
        ByteBufUtils.writeVarInt(buf, y, 5);
        ByteBufUtils.writeVarInt(buf, z, 5);
        ByteBufUtils.writeVarInt(buf, isEnabled, 1);
    }


    public static class PacketHandler implements IMessageHandler<OSPacketHandler, IMessage> {

        @Override
        public IMessage onMessage(OSPacketHandler message, MessageContext ctx) {
            TileEntity te = ctx.getServerHandler().player.getEntityWorld().getTileEntity(new BlockPos(message.x, message.y, message.z));
            boolean isEnabled;
            int side = message.side;
            isEnabled = message.isEnabled == 1;
            //if(te instanceof TileEntityKVM) {
			//	((TileEntityKVM) te).setSide(side, isEnabled);
            //}
            return null;
        }
    }
}