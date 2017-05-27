package pcl.opensecurity.networking;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.common.tileentity.TileEntityKeypad;

public class HandlerKeypadButton implements IMessageHandler<PacketKeypadButton, IMessage> {

	@Override
	public IMessage onMessage(PacketKeypadButton message, MessageContext ctx) {
		short instanceID = message.id;
		int dim = message.dimension;
		int x = message.x;
		int y = message.y;
		int z = message.z;
		
		//System.out.format("Keypad packet at %d, %d, %d in dim %d", x, y, z, dim);
		
		World world = OpenSecurity.proxy.getWorld(dim);
		
		if (world != null) {
			TileEntity te=world.getTileEntity(new BlockPos(x, y, z));
			
			int button = message.button;
			//System.out.format("Got button for button # %d", button);
			
			TileEntityKeypad tek=(TileEntityKeypad)te;
			tek.buttonStates[button].press(te.getWorld().getTotalWorldTime());
		}
		return null;
	}

}