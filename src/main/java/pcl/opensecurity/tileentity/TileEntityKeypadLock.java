package pcl.opensecurity.tileentity;

import java.util.Map;
import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ComponentConnector;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.networking.PacketKeypadButton;
import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;


public class TileEntityKeypadLock extends TileEntityMachineBase implements Environment  {
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

	protected ComponentConnector node = Network.newNode(this, Visibility.Network).withComponent(getComponentName()).withConnector(32).create();

	static String trimString(String str, int len)
	{
		if (str==null || str.length()<=len) return str;
		return str.substring(0,len);
	}

	@Override
	public Node node() {
		return node;
	}

	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		if (node != null)
			node.remove();
	}

	@Override
	public void invalidate() {
		super.invalidate();
		if (node != null)
			node.remove();
	}

	private static String getComponentName() {
		return "os_keypad";
	}

	@Override
	public void onConnect(Node arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDisconnect(final Node node) {

	}

	@Override
	public void onMessage(Message arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		if (node != null && node.host() == this) {
			node.load(nbt.getCompoundTag("oc:node"));
		}
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
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		if (node != null && node.host() == this) {
			final NBTTagCompound nodeNbt = new NBTTagCompound();
			node.save(nodeNbt);
			nbt.setTag("oc:node", nodeNbt);
		}
		nbt.setString("eventName", eventName);
		for(int i=0;i<12;++i)
			nbt.setString("btn:"+i, buttonLabels[i]);
		nbt.setByteArray("btn:colors", buttonColors);
		nbt.setString("fbText",displayText);
		nbt.setInteger("fbColor",displayColor);
		nbt.setBoolean("shouldBeep", shouldBeep);
	}

	@Callback(doc = "function(String:name):boolean; Sets the name of the event that gets sent when a key is pressed")
	public Object[] setEventName(Context context, Arguments args) throws Exception {
		eventName = args.checkString(0);
		return new Object[]{ true };
	}
	
	@Callback(doc = "function(Boolean):boolean; Sets if the keys should beep when pressed")
	public Object[] setShouldBeep(Context context, Arguments args) throws Exception {
		shouldBeep = args.checkBoolean(0);
		return new Object[]{ true };
	}

	@Callback(doc = "function(String:text[, color:number]):boolean; Sets the display string (0-8 chars), color (0-7) - 1 bit per channel")
	public Object[] setDisplay(Context context, Arguments args) throws Exception {
		String text = args.checkString(0);

		displayColor = (byte)(args.optInteger(1, displayColor)&7);

		displayText = trimString(text, MAX_DISPLAY_LENGTH);

		this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
		markDirty();
		return new Object[]{ true };
	}

		@Callback(doc = "function(String:text[, color:number]):boolean; Sets the display string (0-8 chars), color (0-7) - 1 bit per channel")
	public Object[] setDisplay(Context context, Arguments args) throws Exception {
		String text = args.checkString(0);

		displayColor = (byte)(args.optInteger(1, displayColor)&7);

		displayText = trimString(text, MAX_DISPLAY_LENGTH);

		this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
		markDirty();
		return new Object[]{ true };
	}
	
	@Callback(doc = "function(idx:number, text:string, color:number):boolean; Sets the key text (1-2 chars)")
	public Object[] setKey(Context context, Arguments args) throws Exception {
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

		this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
		markDirty();

		return new Object[]{ true };
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

	public ButtonState[] buttonStates;

	public TileEntityKeypadLock()
	{		
		super();
		buttonStates=new ButtonState[] { 
				new ButtonState(), new ButtonState(), new ButtonState(),
				new ButtonState(), new ButtonState(), new ButtonState(),
				new ButtonState(), new ButtonState(), new ButtonState(),
				new ButtonState(), new ButtonState(), new ButtonState(),
		};
	}

	public static String getBaseInstanceFileName()
	{
		return "keypad";
	}	

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (node != null && node.network() == null) {
			Network.joinOrCreateNetwork(this);
		}
	}


	@Override
	public Packet getDescriptionPacket() 
	{
		NBTTagCompound nbtTag = new NBTTagCompound();
		this.writeToNBT(nbtTag);
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, nbtTag);		
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) 
	{
		readFromNBT(packet.func_148857_g());
	}

	public int getFacing() 
	{		
		return worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
	}

	public void pressedButton(EntityPlayer player, int buttonIndex) {
		if (shouldBeep)
			worldObj.playSoundEffect(xCoord + 0.5D, yCoord + 0.5D,  zCoord + 0.5D, "opensecurity:keypad_press", 0.4F, 1);
		if (!worldObj.isRemote) {
			PacketKeypadButton packet = new PacketKeypadButton((short) 1, worldObj.provider.dimensionId, xCoord, yCoord, zCoord, buttonIndex);
			EntityPlayerMP p=(EntityPlayerMP)player;			
			OpenSecurity.network.sendToAllAround(packet, new NetworkRegistry.TargetPoint(p.dimension, (double)xCoord, (double)yCoord, (double)zCoord, 64d));
			node.sendToReachable("computer.signal", eventName, buttonIndex+1, buttonIndex>=0 && buttonIndex<buttonLabels.length ? buttonLabels[buttonIndex] : "");
		}
	}
	public static float[] facingToAngle={0,0,0,180,90,270};
	public float getAngle() 
	{
		return facingToAngle[getFacing()];
	}
} 
