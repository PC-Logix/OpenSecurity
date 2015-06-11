package pcl.client.sounds;

import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.tileentity.TileEntityAlarm;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class AlarmSoundHandler extends MovingSound {
    private final TileEntity tileentity;
    private TileEntityAlarm tileAlarm;
    protected boolean repeat = true;
    protected int repeatDelay = 0;

    
    public AlarmSoundHandler(TileEntity tile, String Soundname, float volume) {
        super(new ResourceLocation(OpenSecurity.MODID + ":" + Soundname));
        this.tileentity = tile;
        this.repeat = true;
        this.xPosF = tileentity.xCoord;
        this.yPosF = tileentity.yCoord;
        this.zPosF = tileentity.zCoord;
        tileAlarm = (TileEntityAlarm) tileentity;
    }

    public void setDonePlaying()
    {
        this.repeat = false;
        this.donePlaying = true;
        this.repeatDelay = 0;
    }

    @Override
    public boolean isDonePlaying()
    {
        return this.donePlaying;
    }
    
    public void update() {
    	System.out.println(this.donePlaying);
        if (tileAlarm.isShouldStop()) {
        	System.out.println("Stop");
        	setDonePlaying();
        }
    }

    @Override
    public boolean canRepeat()
    {
        return this.repeat;
    }

    @Override
    public float getVolume()
    {
        return this.volume;
    }

    @Override
    public int getRepeatDelay(){ return this.repeatDelay; }

    @Override
    public AttenuationType getAttenuationType()
    {
        return AttenuationType.LINEAR;
    }
}
