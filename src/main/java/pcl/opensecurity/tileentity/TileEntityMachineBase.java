package pcl.opensecurity.tileentity;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.client.sounds.ISoundTile;
import pcl.opensecurity.client.sounds.MachineSound;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityMachineBase extends TileEntity {

	public TileEntityMachineBase() {
		super();
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
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

	public boolean shouldPlaySound() {
		return false;
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
		return true;
	}

	@SideOnly(Side.CLIENT)
	public void updateSound() {
		if (hasSound()) {
			if (shouldPlaySound() && !isInvalid()) {
				if (sound == null) {
					if (this instanceof ISoundTile) {
						ISoundTile tile = (ISoundTile) this;
						if (tile instanceof TileEntityAlarm) {
							soundRes = new ResourceLocation("opensecurity_external:" + tile.getSoundName());
						} else {
							soundRes = new ResourceLocation("opensecurity:" + tile.getSoundName());
						}
						sound = new MachineSound(soundRes, xCoord + 0.5f, yCoord + 0.5f, zCoord + 0.5f, getVolume(), getPitch(), shouldRepeat());
						FMLClientHandler.instance().getClient().getSoundHandler().playSound(sound);
					}
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

}
