package pcl.opensecurity.common.tileentity;

import java.util.Map;

import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Visibility;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.common.SoundHandler;
import pcl.opensecurity.networking.PacketKeypadButton;

public class TileEntityKeypad extends TileEntityOSBase {
	final static int MAX_LABEL_LENGTH = 3;
	final static int MAX_DISPLAY_LENGTH = 8;

	private boolean shouldBeep = true;
	public String data;
	public String eventName = "keypad";
	public String[] buttonLabels = new String[] {"1", "2", "3",
												 "4", "5", "6",
												 "7", "8", "9",
												 "*", "0", "#"};
	public byte[] buttonColors = new byte[] {7,7,7,
											 7,7,7,
											 7,7,7,
											 7,7,7};
	public String displayText = "";
	public byte displayColor = 7;

	public ButtonState[] buttonStates;

	
	public TileEntityKeypad(){
		super("os_keypad");
		buttonStates=new ButtonState[] { 
				new ButtonState(), new ButtonState(), new ButtonState(),
				new ButtonState(), new ButtonState(), new ButtonState(),
				new ButtonState(), new ButtonState(), new ButtonState(),
				new ButtonState(), new ButtonState(), new ButtonState(),
		};
		node = Network.newNode(this, Visibility.Network).withComponent(getComponentName()).withConnector(32).create();
	}
	
	static String trimString(String str, int len)
	{
		if (str==null || str.length()<=len) return str;
		return str.substring(0,len);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		if (nbt.hasKey("eventName") && !nbt.getString("eventName").isEmpty()) {
			eventName = nbt.getString("eventName");
		} else {
			eventName = "keypad";
		}
		if (nbt.hasKey("shouldBeep")) {
			shouldBeep = nbt.getBoolean("shouldBeep");
		} else {
			shouldBeep = true;
		}
		for(int i=0;i<12;++i)
			buttonLabels[i] = trimString(nbt.getString("btn:"+i), MAX_LABEL_LENGTH);
		byte[] colors = nbt.getByteArray("btn:colors");
		if(colors!=null)
			for(int i=0; i<12 && i<colors.length; ++i)
				buttonColors[i] = colors[i];
		displayText = trimString(nbt.getString("fbText"), MAX_DISPLAY_LENGTH);
		displayColor = nbt.getByte("fbColor");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setString("eventName", eventName);
		for(int i=0;i<12;++i)
			nbt.setString("btn:"+i, buttonLabels[i]);
		nbt.setByteArray("btn:colors", buttonColors);
		nbt.setString("fbText",displayText);
		nbt.setInteger("fbColor",displayColor);
		nbt.setBoolean("shouldBeep", shouldBeep);
		return nbt;
	}

	@Callback(doc = "function(String:name):boolean; Sets the name of the event that gets sent when a key is pressed")
	public Object[] setEventName(Context context, Arguments args) {
		eventName = args.checkString(0);
		return new Object[]{ true };
	}
	
	@Callback(doc = "function(Boolean):boolean; Sets if the keys should beep when pressed")
	public Object[] setShouldBeep(Context context, Arguments args) {
		shouldBeep = args.checkBoolean(0);
		return new Object[]{ true };
	}

	@Callback(doc = "function(String:text[, color:number]):boolean; Sets the display string (0-8 chars), color (0-7) - 1 bit per channel")
	public Object[] setDisplay(Context context, Arguments args) {
		String text = args.checkString(0);

		displayColor = (byte)(args.optInteger(1, displayColor)&7);

		displayText = trimString(text, MAX_DISPLAY_LENGTH);

		this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 2);
		getUpdateTag();
		markDirty();
		return new Object[]{ true };
	}
	
	@Callback(doc = "function(idx:number, text:string, color:number):boolean; Sets the key text (1-2 chars)")
	public Object[] setKey(Context context, Arguments args) {
		if(args.count()==0) throw new IllegalArgumentException("Not enough arguments");
		if(args.isInteger(0))
		{
			int idx = args.checkInteger(0);
			if (idx<1 || idx>12) throw new IllegalArgumentException("Index "+idx+" is out of range");
			buttonLabels[idx-1] = trimString(args.checkString(1), MAX_LABEL_LENGTH);
			buttonColors[idx-1] = (byte)(args.optInteger(2, buttonColors[idx-1])&7);
		}
		else if(args.isTable(0))
		{
			Map labels = args.checkTable(0);
			Map colors = args.optTable(1, null);
			for(int i=0;i<12;++i)
			{
				Integer id = new Integer(i+1);
				Object val = labels.get(id);
				if(val!=null && val instanceof String)
				{
					buttonLabels[i] = trimString((String)val, MAX_LABEL_LENGTH);
				}
				if (colors!=null)
				{
					val = Integer.parseInt((String) colors.get(id));
					if(val!=null && val instanceof Number)
					{
						Number color = (Number)val;
						buttonColors[i] = (byte)(color.intValue()&7);
					}
				}
			}
		}
		else throw new IllegalArgumentException("First argument must be index or table");

		this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 2);
		getUpdateTag();
		markDirty();

		return new Object[]{ true };
	}

	public IBlockState getFacing() 
	{		
		return world.getBlockState(new BlockPos(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ()));
	}
	
	public float getAngle() 
	{
		//this was getFacing()
		return getFacing().getBlock().getMetaFromState(getFacing()) * 270;
	}
	
	public static class ButtonState {
		public static int pressDelay=5;
		public long pressedTime;

		public ButtonState()
		{
			pressedTime=0;
		}

		public boolean isPressed(long time)
		{
			return time-pressedTime<pressDelay;			
		}

		public void press(long time)
		{
			pressedTime=time;			
		}		
	}

	public void pressedButton(EntityPlayer player, int buttonIndex) {
		if (shouldBeep)
            world.playSound(null, pos.getX() + 0.5F, pos.getY() + 0.5f, pos.getZ() + 0.5F, SoundHandler.keypad_press, SoundCategory.BLOCKS, 15 / 15 + 0.5F, 1.0F);
		if (!world.isRemote) {
			PacketKeypadButton packet = new PacketKeypadButton((short) 1, world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), buttonIndex);
			EntityPlayerMP p=(EntityPlayerMP)player;			
			OpenSecurity.network.sendToAllAround(packet, new NetworkRegistry.TargetPoint(p.dimension, pos.getX(), pos.getY(), pos.getZ(), 64d));
			node.sendToReachable("computer.signal", eventName, buttonIndex+1, buttonIndex>=0 && buttonIndex<buttonLabels.length ? buttonLabels[buttonIndex] : "");
		}
	}
	
}
