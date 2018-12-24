package pcl.opensecurity.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.common.protection.Protection;

import java.util.logging.Logger;

public class PacketProtectionSync implements IMessage {
    NBTTagCompound protection;
    int dimension;

    public PacketProtectionSync() {
        //intentionally empty
    }

    public PacketProtectionSync(World world) {
        this.dimension = world.provider.getDimension();
        protection = Protection.get(world).writeToNBT(new NBTTagCompound());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(dimension);
        ByteBufUtils.writeTag(buf, protection);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        dimension = buf.readInt();
        protection = ByteBufUtils.readTag(buf);
    }

    public static class Handler implements IMessageHandler<PacketProtectionSync, IMessage> {
        @Override
        public IMessage onMessage(PacketProtectionSync message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(new Runnable() {
                public void run() {
                    Protection.get(Minecraft.getMinecraft().player.world).readFromNBT(message.protection);
                }
            });

            return null;
        }
    }

}
