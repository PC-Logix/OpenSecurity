package pcl.opensecurity.client.sounds;

import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MachineSound extends PositionedSound implements ITickableSound {

	private boolean donePlaying;

	public MachineSound(ResourceLocation sound, BlockPos pos, float volume, float pitch) {
		this(sound, pos, volume, pitch, true);
	}

	public MachineSound(ResourceLocation sound, BlockPos pos, float volume, float pitch, boolean repeat) {
		super(sound, SoundCategory.BLOCKS);
		this.xPosF = pos.getX();
		this.yPosF = pos.getY();
		this.zPosF = pos.getZ();
		this.volume = volume;
		this.pitch = pitch;
		this.repeat = repeat;
		// Dynamic alarm resources do not always loop reliably through OpenAL's
		// native loop flag. A one-tick repeat delay uses SoundManager's explicit
		// repeat path instead and still keeps the sound continuously active.
		this.repeatDelay = repeat ? 1 : 0;
	}

	@Override
	public void update() {
	}

	@SideOnly(Side.CLIENT)
	public void endPlaying() {
		if(sound != null)
			FMLClientHandler.instance().getClient().getSoundHandler().stopSound(this);

		donePlaying = true;
	}

	public void startPlaying() {
		donePlaying = false;
	}

	@Override
	public boolean isDonePlaying() {
		return donePlaying;
	}
}
