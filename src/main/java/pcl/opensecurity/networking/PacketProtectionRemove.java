package pcl.opensecurity.networking;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import pcl.opensecurity.common.protection.Protection;
import pcl.opensecurity.common.protection.ProtectionAreaChunk;


public class PacketProtectionRemove extends PacketProtectionAdd {
    public PacketProtectionRemove(World world, BlockPos controller) {
        ProtectionAreaChunk pac = new ProtectionAreaChunk(new AxisAlignedBB(0, 0, 0, 0, 0, 0), controller);
        protection = pac.writeToNBT(new NBTTagCompound());
    }

    public static class Handler implements IMessageHandler<PacketProtectionRemove, IMessage> {
        @Override
        public IMessage onMessage(PacketProtectionRemove message, MessageContext ctx) {
            ProtectionAreaChunk pac = new ProtectionAreaChunk(message.protection);
            Protection.removeArea(Minecraft.getMinecraft().player.world, pac.getControllerPosition());
            return null;
        }
    }

}