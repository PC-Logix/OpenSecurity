package pcl.opensecurity.common.tileentity;

import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.client.sounds.ISoundTile;

import javax.annotation.Nullable;
import java.util.Arrays;

public class TileEntityAlarm extends TileEntityOSBase implements ISoundTile {

	private static final String[] AVAILABLE_SOUNDS = { "klaxon1", "klaxon2" };
	private String soundName = AVAILABLE_SOUNDS[0];
	private float volume = 1.0F;
	private Boolean computerPlaying = false;

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

	public TileEntityAlarm() {
		super();
		setSound(soundName);
		node = Network.newNode(this, Visibility.Network).withComponent(getComponentName()).withConnector(32).create();
	}

	public String getComponentName() {
		return "os_alarm";
	}

	@Override
	public boolean getShouldPlay() {
		return shouldPlay;
	}

	@Override
	public String getSoundName() {
		return soundName;
	}

	@Override
	public float getVolume() {
		return volume;
	}

	@Override
	public ResourceLocation setSound(String sound) {
		setSoundRes(new ResourceLocation("opensecurity:" + sound));
		return getSoundRes();
	}

	private void setShouldStart() {
		setShouldPlay(true);
		world.notifyBlockUpdate(this.pos, world.getBlockState(this.pos), world.getBlockState(this.pos), 3);
		getUpdateTag();
		markDirty();
	}

	private void setShouldStop() {
		setShouldPlay(false);
		world.notifyBlockUpdate(this.pos, world.getBlockState(this.pos), world.getBlockState(this.pos), 3);
		getUpdateTag();
		markDirty();
	}

	// OC Methods.

	@Callback(doc = "function(range:integer):string; Sets the range in blocks of the alarm", direct = true)
	public Object[] setRange(Context context, Arguments args) {
		Float newVolume = (float) args.checkInteger(0);
		if (newVolume >= 15 && newVolume <= 150) {
			volume = newVolume / 15 + 0.5F;
			return new Object[] { "Success" };
		} else {
			return new Object[] { "Error, range should be between 15-150" };
		}
	}

	@Callback(doc = "fucntion(): table; Returns the list of available sound names", direct = true)
	public Object[] getAvailableSounds(Context context, Arguments args) {
		return new Object[] { AVAILABLE_SOUNDS };
	}

	@Callback(doc = "function(soundName:string):string; Sets the alarm sound", direct = true)
	public Object[] setAlarm(Context context, Arguments args) throws Exception {
		String alarm = args.checkString(0);
		if (!Arrays.asList(AVAILABLE_SOUNDS).contains(alarm)) {
			throw new Exception("alarm '" + alarm + "' not found");
		}
		soundName = alarm;
		setSound(alarm);
		getUpdateTag();
		markDirty();
		return new Object[] { "Success" };
	}

	@Callback(doc = "function():string; Activates the alarm", direct = true)
	public Object[] activate(Context context, Arguments args) {
		this.setShouldStart();
		computerPlaying = true;
		return new Object[] { "Ok" };
	}

	@Callback(doc = "function():string; Deactivates the alarm", direct = true)
	public Object[] deactivate(Context context, Arguments args) {
		this.setShouldStop();
		computerPlaying = false;
		return new Object[] { "Ok" };
	}

	@Callback(doc = "function(int:x, int:y, int:z, string:sound, float:range(1-10 recommended)):string; Plays sound at x y z", direct = true)
	public Object[] playSoundAt(Context context, Arguments args) {
		if (OpenSecurity.enableplaySoundAt) {
			double x = args.checkDouble(0);
			double y = args.checkDouble(1);
			double z = args.checkDouble(2);
			String sound = args.checkString(3);
			float range = args.checkInteger(4);
			world.playSound(x, y, z, new SoundEvent(new ResourceLocation(sound)), SoundCategory.BLOCKS, range / 15 + 0.5F, 1.0F, false);
			getUpdateTag();
			markDirty();
			return new Object[] { "Ok" };
		} else {
			return new Object[] { "Disabled" };
		}
	}

	@Override
	public boolean playSoundNow() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if (node != null && node.host() == this) {
			node.load(tag.getCompoundTag("oc:node"));
		}
		setShouldPlay(tag.getBoolean("isPlayingSound"));
		soundName = tag.getString("alarmName");
		volume = tag.getFloat("volume");
		computerPlaying = tag.getBoolean("computerPlaying");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		if (node != null && node.host() == this) {
			final NBTTagCompound nodeNbt = new NBTTagCompound();
			node.save(nodeNbt);
			tag.setTag("oc:node", nodeNbt);
		}
		tag.setBoolean("isPlayingSound", getShouldPlay());
		tag.setString("alarmName", soundName);
		tag.setFloat("volume", volume);
		tag.setBoolean("computerPlaying", computerPlaying);
		return tag;
	}

	@Override
	@Nullable
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		readFromNBT(packet.getNbtCompound());
	}

	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		this.readFromNBT(tag);
	}
}
