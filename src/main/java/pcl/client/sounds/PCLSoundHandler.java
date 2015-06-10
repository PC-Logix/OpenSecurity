package pcl.client.sounds;

import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.tileentity.AlarmTE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class PCLSoundHandler extends MovingSound {
    private final TileEntity tileentity;
    private AlarmTE tileAlarm; //again we make a value that will be used to get information from


    public PCLSoundHandler(TileEntity tile, String Soundname, float volume) { //we are taking in the tile entity and the sound name
        super(new ResourceLocation(OpenSecurity.MODID + ":" + Soundname)); //setting the location of the sound file
        this.tileentity = tile; //we tell it that tileentity is the TileEntity we passed it on creating this instance
        this.repeat = true; //we say that we want the sound to repeat
        this.xPosF = tileentity.xCoord; //so were getting the location of the tile entity and putting them into values that the sound player WILL use (where i went wrong before)
        this.yPosF = tileentity.yCoord;
        this.zPosF = tileentity.zCoord;
        tileAlarm = (AlarmTE) tileentity; // since we know that the TileEntity is going to be a TileEntitySpeaker we can safely cast this variable as one :)
    }


    public void update() { //this is run every tick
        // LogHelper.info("i should be making noise oh and volume is " + volume); //this is designed to give me feedback when debuging...its invaluable!
        if (tileAlarm.isShouldStop()) { //ok so each tick we are looking at the speaker tile entity to see if we should stop or not
            //LogHelper.info("ok ill shut up now");
        	System.out.println("Stop");
            this.donePlaying = true; //stop making that awful noise we say
        }
    }

    @Override
    public boolean isDonePlaying() {
    	System.out.println(this.donePlaying);
        return this.donePlaying;
    } //this is how the sound manager checks to see if it can shut the hell up yet :)
}
