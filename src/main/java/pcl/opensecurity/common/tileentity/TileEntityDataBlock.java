package pcl.opensecurity.common.tileentity;

import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;

import li.cil.oc.Settings;
import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Component;
import li.cil.oc.api.network.ComponentConnector;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import net.minecraft.nbt.NBTTagCompound;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.mindrot.jbcrypt.BCrypt;

import pcl.opensecurity.OpenSecurity;

import com.google.common.hash.Hashing;

public class TileEntityDataBlock extends TileEntityMachineBase {
	protected ComponentConnector node = Network.newNode(this, Visibility.Network).withComponent(getComponentName()).withConnector(32).create();

	public TileEntityDataBlock() {
		if (this.node() != null) {
			initOCFilesystem();
		}
	}

	private li.cil.oc.api.network.ManagedEnvironment oc_fs;

	private void initOCFilesystem() {
		oc_fs = li.cil.oc.api.FileSystem.asManagedEnvironment(li.cil.oc.api.FileSystem.fromClass(OpenSecurity.class, OpenSecurity.MODID, "/lua/datablock/"), "datablock");
		((Component) oc_fs.node()).setVisibility(Visibility.Neighbors);
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
		return "os_datablock";
	}

	@Override
	public void onConnect(final Node node) {
		if (node.host() instanceof Context) {
			node.connect(oc_fs.node());
		}
	}

	@Override
	public void onDisconnect(final Node node) {
		if (node.host() instanceof Context) {
			node.disconnect(oc_fs.node());
		} else if (node == this.node) {
			oc_fs.node().remove();
		}
	}

	@Override
	public void onMessage(Message arg0) {

	}

	@Override
	public void update() {
		super.update();
		if (node != null && node.network() == null) {
			Network.joinOrCreateNetwork(this);
		}
	}

	@Callback(direct = true, doc = "function():number -- The maximum size of data that can be passed to other functions of the card.")
	public Object[] getLimit(Context context, Arguments args) {
		return new Object[] { Settings.get().dataCardHardLimit() };
	}

	@Callback(direct = true, limit = 32, doc = "function(data:string):string -- Applies base64 encoding to the data.")
	public Object[] encode64(Context context, Arguments args) throws Exception {
		return new Object[] { Base64.encodeBase64(checkLimits(context, args, Settings.get().dataCardComplex())) };
	}

	@Callback(direct = true, limit = 32, doc = "function(data:string):string -- Applies base64 decoding to the data.")
	public Object[] decode64(Context context, Arguments args) throws Exception {
		return new Object[] { Base64.decodeBase64(checkLimits(context, args, Settings.get().dataCardComplex())) };
	}

	@Callback(direct = true, limit = 6, doc = "function(data:string):string -- Applies deflate compression to the data.")
	public Object[] deflate(Context context, Arguments args) throws Exception {
		byte[] data = checkLimits(context, args, Settings.get().dataCardComplex());
		ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
		DeflaterOutputStream deos = new DeflaterOutputStream(baos);
		deos.write(data);
		deos.finish();
		deos.close();
		return new Object[] { baos.toByteArray() };
	}

	@Callback(direct = true, limit = 6, doc = "function(data:string):string -- Applies inflate decompression to the data.")
	public Object[] inflate(Context context, Arguments args) throws Exception {
		byte[] data = checkLimits(context, args, Settings.get().dataCardComplex());
		ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
		InflaterOutputStream inos = new InflaterOutputStream(baos);
		inos.write(data);
		inos.finish();
		inos.close();
		return new Object[] { baos.toByteArray() };
	}

	@Callback(direct = true, limit = 32, doc = "function(data:string):string -- Computes SHA2-256 hash of the data. Result is in binary format.")
	public Object[] sha256(Context context, Arguments args) throws Exception {
		byte[] data = checkLimits(context, args, Settings.get().dataCardSimple());
		return new Object[] { Hashing.sha256().hashBytes(data).asBytes() };
	}

	@Callback(direct = true, limit = 32, doc = "function(data:string):string -- function(data:string):string -- Computes MD5 hash of the data. Result is in binary format")
	public Object[] md5(Context context, Arguments args) throws Exception {
		byte[] data = checkLimits(context, args, Settings.get().dataCardSimple());
		return new Object[] { Hashing.md5().hashBytes(data).asBytes() };
	}

	@Callback(direct = true, limit = 32, doc = "function(data:string):string -- Computes CRC-32 hash of the data. Result is in binary format")
	public Object[] crc32(Context context, Arguments args) throws Exception {
		byte[] data = checkLimits(context, args, Settings.get().dataCardSimple());
		return new Object[] { Hashing.crc32().hashBytes(data).asBytes() };
	}

	@Callback(direct = true, limit = 32, doc = "function(data:string):string -- Applies rot13 to the data. ")
	public Object[] rot13(Context context, Arguments args) throws Exception {
		return new Object[] { rot13(args.checkString(0)) };
	}

	@Callback(direct = true, limit = 32, doc = "function(plain-text:string, optional rounds:int):string -- Computes the bCryptHash of the input plaintext, optionally supply rounds (4-15)")
	public Object[] bCryptHash(Context context, Arguments args) throws Exception {
		int rounds = (int) args.optDouble(1, 10);
		if (rounds < 4) {
			rounds = 4;
		} else if (rounds > 15) {
			rounds = 15;
		}
		String hashed = BCrypt.hashpw(args.checkString(0), BCrypt.gensalt(rounds));
		return new Object[] { hashed };
	}
	
	@Callback(direct = true, limit = 32, doc = "function(plain-text:string, hash:string):boolean -- Checks bCrypt input vs hash supplied")
	public Object[] bCryptCheck(Context context, Arguments args) throws Exception {
		return new Object[] { BCrypt.checkpw(args.checkString(0), args.checkString(1)) };
	}
	
	private byte[] checkLimits(Context context, Arguments args, Double cost) throws Exception {
		byte[] data = args.checkByteArray(0);
		if (data.length > Settings.get().dataCardHardLimit())
			throw new IllegalArgumentException("data size limit exceeded");
		if (!node.tryChangeBuffer(-cost))
			throw new Exception("not enough energy");
		if (data.length > Settings.get().dataCardSoftLimit())
			context.pause(Settings.get().dataCardTimeout());
		return data;
	}
	
	public static String rot13(String input) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (c >= 'a' && c <= 'm')
				c += 13;
			else if (c >= 'A' && c <= 'M')
				c += 13;
			else if (c >= 'n' && c <= 'z')
				c -= 13;
			else if (c >= 'N' && c <= 'Z')
				c -= 13;
			sb.append(c);
		}
		return sb.toString();
	}
}