package pcl.opensecurity.common.tileentity;

import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ComponentConnector;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pcl.opensecurity.client.sounds.ISoundTile;
import pcl.opensecurity.client.sounds.MachineSound;

public class TileEntityMachineBase extends TileEntity implements ITickable, Environment {
	
	public Boolean shouldPlay = false;
	public ComponentConnector node;
	public ManagedEnvironment oc_fs;
	
	public TileEntityMachineBase() {
		super();
	}
	
	@Override
	public void update() {
		if (node != null && node.network() == null) {
			Network.joinOrCreateNetwork(this);
		}
		if (worldObj.isRemote && hasSound()) {
			updateSound();
		}
	}

	// Sound related, thanks to EnderIO code for this!

	@SideOnly(Side.CLIENT)
	private MachineSound sound;

	private ResourceLocation soundRes;

	public String getSoundName() {
		return null;
	}

	public ResourceLocation setSound(String sound) {
		return null;
	}

	public ResourceLocation getSoundRes() {
		return soundRes;
	}

	public boolean getShouldPlay() {
		return shouldPlay;
	}

	public void setShouldPlay(boolean b) {
		shouldPlay = b;
	}
	
	public boolean hasSound() {
		return getSoundName() != null;
	}

	public float getVolume() {
		return 1.0f;
	}

	public float getPitch() {
		return 1.0f;
	}

	public boolean shouldRepeat() {
		return getShouldPlay();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		if (node != null && node.host() == this) {
			node.load(nbt.getCompoundTag("oc:node"));
		}
		if (oc_fs != null && oc_fs.node() != null) {
			oc_fs.node().load(nbt.getCompoundTag("oc:fs"));
		}

	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		if (node != null && node.host() == this) {
			final NBTTagCompound nodeNbt = new NBTTagCompound();
			node.save(nodeNbt);
			nbt.setTag("oc:node", nodeNbt);
		}
		if (oc_fs != null && oc_fs.node() != null) {
			final NBTTagCompound fsNbt = new NBTTagCompound();
			oc_fs.node().save(fsNbt);
			nbt.setTag("oc:fs", fsNbt);
		}
		return nbt;
	}
	
	@SideOnly(Side.CLIENT)
	public void updateSound() {
		if (hasSound()) {
			if ((getShouldPlay()) && !isInvalid()) {
				if (sound == null && this instanceof ISoundTile) {
						ISoundTile tile = (ISoundTile) this;
						soundRes = new ResourceLocation("opensecurity:" + tile.getSoundName());
						sound = new MachineSound(soundRes, this.getPos(), getVolume(), getPitch(), shouldRepeat());
						FMLClientHandler.instance().getClient().getSoundHandler().playSound(sound);
				}
			} else if (sound != null) {
				sound.endPlaying();
				sound = null;
			}
		}
	}

	public void setSoundRes(ResourceLocation soundRes) {
		this.soundRes = soundRes;
	}
	
	@Callback
	public Object[] greet(Context context, Arguments args) {
		return new Object[] { "Lasciate ogne speranza, voi ch'entrate" };
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

}