package pcl.opensecurity.networking;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketKeypadButton implements IMessage {
	int button;
	short id;
	short instance;
	int dimension;
	int x, y, z;
	
	public PacketKeypadButton() {
		//intentionally empty
	}
	
	public PacketKeypadButton(short instance, int dim, int x, int y, int z, int button) {
		this.id = 1;
		this.instance = instance;
		this.dimension = dim;
		this.x = x;
		this.y = y;
		this.z = z;
		this.button = button;
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(button);
		buf.writeShort(instance);
		buf.writeInt(dimension);
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		button = buf.readInt();
		instance = buf.readShort();
		dimension = buf.readInt();
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
	}
}