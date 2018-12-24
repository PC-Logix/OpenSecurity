package pcl.opensecurity.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import pcl.opensecurity.common.protection.Protection;
import pcl.opensecurity.common.protection.ProtectionAreaChunk;

public class PacketProtectionAdd implements IMessage {
    NBTTagCompound protection;

    public PacketProtectionAdd() {
        //intentionally empty
    }

    public PacketProtectionAdd(World world, BlockPos controller, AxisAlignedBB area) {
        protection = new ProtectionAreaChunk(area, controller).writeToNBT(new NBTTagCompound());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, protection);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        protection = ByteBufUtils.readTag(buf);
    }

    public static class Handler implements IMessageHandler<PacketProtectionAdd, IMessage> {
        @Override
        public IMessage onMessage(PacketProtectionAdd message, MessageContext ctx) {
            ProtectionAreaChunk pac = new ProtectionAreaChunk(message.protection);
            Protection.addArea(Minecraft.getMinecraft().player.world, pac.getArea(), pac.getControllerPosition());
            return null;
        }
    }

}
