package pcl.opensecurity.client.sounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class AlarmSoundHandler extends PositionedSound implements ITickableSound {
	
	private int x,y,z;
	public boolean shouldBePlaying = true;

	public AlarmSoundHandler(ResourceLocation resource, TileEntity parent) {
		super(resource);
		repeat = true;
		xPosF = parent.xCoord+0.5f;
		yPosF = parent.yCoord+0.5f;
		zPosF = parent.zCoord+0.5f;
		x = parent.xCoord;
		y = parent.yCoord;
		z = parent.zCoord;
	}

	@Override
	public void update() {
	}

	@Override
	public boolean isDonePlaying() {
		if (shouldBePlaying)
		{
			// we should only be playing if parent still exists and says we are playing - assume not
			shouldBePlaying = false; 
			// should never be here on the server, but just in case
			World world = Minecraft.getMinecraft().theWorld;
			if (world.isRemote)
			{
				TileEntity parent = world.getTileEntity(x, y, z);
				if (parent != null)
				{
					if (parent instanceof IShouldLoop)
					{
						IShouldLoop iShouldLoop = (IShouldLoop)parent;
						shouldBePlaying = iShouldLoop.continueLoopingAudio();
					}
				}
			}
		}
		return !shouldBePlaying;
	}
}
