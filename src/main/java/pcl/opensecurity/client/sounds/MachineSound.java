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