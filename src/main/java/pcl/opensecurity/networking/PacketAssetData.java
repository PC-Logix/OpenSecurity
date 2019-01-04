package pcl.opensecurity.networking;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketAssetData implements IMessage {
    String name = "";
    byte[] data;

    public PacketAssetData() {
        //intentionally empty
    }

    public PacketAssetData(String fileName, byte[] fileData) {
        name = fileName;
        data = fileData;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, name);
        buf.writeBytes(data);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        name = ByteBufUtils.readUTF8String(buf);
        buf.readBytes(data);
    }

    public static class Handler implements IMessageHandler<PacketAssetData, IMessage> {
        @Override
        public IMessage onMessage(PacketAssetData message, MessageContext ctx) {





            return null;
        }
    }

}

