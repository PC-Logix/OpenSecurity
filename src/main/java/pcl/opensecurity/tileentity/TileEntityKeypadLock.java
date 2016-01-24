package pcl.opensecurity.tileentity;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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
import pcl.opensecurity.networking.packet.PacketKeypadButton;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;


public class TileEntityKeypadLock extends TileEntityMachineBase implements Environment  {

	public String data;
	public String eventName = "keypad";

	protected ComponentConnector node = Network.newNode(this, Visibility.Network).withComponent(getComponentName()).withConnector(32).create();

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

	private String getComponentName() {
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
	}
	
	@Callback
	public Object[] greet(Context context, Arguments args) {
		return new Object[] { "Lasciate ogne speranza, voi ch'intrate" };
	}

	@Callback(doc = "function(String:name):boolean; Sets the name of the event that gets sent when a card is swipped", direct = true)
	public Object[] setEventName(Context context, Arguments args) throws Exception {
		eventName = args.checkString(0);
		return new Object[]{ true };
	}
	
	static String[] keyChars=new String[] {"1","2","3","4","5","6","7","8","9","*","0","#"};
	static int maxCodeLength=8;
	protected int instanceID;
	static Set<String> validChars = new HashSet<String>();
	static {
		 validChars.add("0");
		 validChars.add("1");
		 validChars.add("2");
		 validChars.add("3");
		 validChars.add("4");
		 validChars.add("5");
		 validChars.add("6");
		 validChars.add("7");
		 validChars.add("8");
		 validChars.add("9");
		 validChars.add("#");
		 validChars.add("*");
		 
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
	
	public static class LockCode {
		String name;
		String code;
		byte accessLevel;
		
		public LockCode(String name, String code, byte accessLevel)
		{
			this.name=name;
			this.code=code;
			this.accessLevel=accessLevel;
		}

		public boolean matchesBuffer(String pressedBuffer) 
		{
			int skip=pressedBuffer.length()-code.length();
			if (skip>=0) 
			{
				String relevant=pressedBuffer.substring(skip,pressedBuffer.length());
				return relevant.equals(code);
			}
			return false;
		}
		
	}
	public ButtonState buttonStates[];
	
	Map<String,LockCode> storedCodes;
	
	// true if the blocks redstone output state has changed
	boolean outputChanged;
	//true if one or more activation programs are running
	boolean programsActive;

	String pressedBuffer;

	public TileEntityKeypadLock()
	{		
		super();
		buttonStates=new ButtonState[] { 
			new ButtonState(), new ButtonState(), new ButtonState(),
			new ButtonState(), new ButtonState(), new ButtonState(),
			new ButtonState(), new ButtonState(), new ButtonState(),
			new ButtonState(), new ButtonState(), new ButtonState(),
		};
		
		storedCodes=Collections.synchronizedMap(new HashMap<String,LockCode>());
		
		programsActive=false;
		outputChanged=false;
		pressedBuffer="";
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
	@SideOnly(Side.SERVER)
	public Packet getDescriptionPacket() 
	{
		NBTTagCompound nbtTag = new NBTTagCompound();
		this.writeToNBT(nbtTag);
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, nbtTag);		
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) 
	{
		readFromNBT(packet.func_148857_g());
	}

	public int getFacing() 
	{		
		return worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
	}
	
	public void pressedButton(EntityPlayer player, int buttonIndex) {
		if (!worldObj.isRemote) {
			
			PacketKeypadButton packet = new PacketKeypadButton((short) 1, worldObj.provider.dimensionId, xCoord, yCoord, zCoord, buttonIndex);		
			EntityPlayerMP p=(EntityPlayerMP)player;			
			OpenSecurity.network.sendToAllAround(packet, new NetworkRegistry.TargetPoint(p.dimension, (double)xCoord, (double)yCoord, (double)zCoord, 64d));
			
			if (pressedBuffer.length()==maxCodeLength)
				pressedBuffer=pressedBuffer.substring(2, maxCodeLength);
			pressedBuffer = pressedBuffer+keyChars[buttonIndex];
			node.sendToReachable("computer.signal", eventName, keyChars[buttonIndex]);
		}
	}
	public static float[] facingToAngle={0,0,0,180,90,270};
	public float getAngle() 
	{
		return facingToAngle[getFacing()];
	}
}
