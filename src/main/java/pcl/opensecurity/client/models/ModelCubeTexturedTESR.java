package pcl.opensecurity.client.models;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
/* this class might be buggy... */
public class ModelCubeTexturedTESR extends ModelCubeTESR {
    public ModelCubeTexturedTESR(float x1, float y1, float z1, float x2, float y2, float z2){
        super(x1, y1, z1, x2, y2, z2);
        vertexFormat = DefaultVertexFormats.POSITION_TEX;
    }


    @Override
    protected void drawTop(BufferBuilder buffer, float x1, float y1, float z1, float x2, float y2, float z2){
        buffer.pos(x2, y2, z1).tex(0, 1); // Top Right Of The Quad (Top)
        buffer.pos(x1, y2, z1).tex(0, 1); ; // Top Left Of The Quad (Top)
        buffer.pos(x1, y2, z2).tex(0, 1); ; // Bottom Left Of The Quad (Top)
        buffer.pos(x2, y2, z2).tex(0, 1); ; // Bottom Right Of The Quad (Top)
    }

    @Override
    protected void drawBottom(BufferBuilder buffer, float x1, float y1, float z1, float x2, float y2, float z2){
        buffer.pos(x2, y1, z2).tex(0, 1); ; // Top Right Of The Quad (Bottom)
        buffer.pos(x1, y1, z2).tex(0, 1); ; // Top Left Of The Quad (Bottom)
        buffer.pos(x1, y1, z1).tex(0, 1); ; // Bottom Left Of The Quad (Bottom)
        buffer.pos(x2, y1, z1).tex(0, 1); ; // Bottom Right Of The Quad (Bottom)
    }

    @Override
    protected void drawFront(BufferBuilder buffer, float x1, float y1, float z1, float x2, float y2, float z2){
        buffer.pos(x2, y2, z2).tex(0, 1); ; // Top Right Of The Quad (Front)
        buffer.pos(x1, y2, z2).tex(0, 1); ; // Top Left Of The Quad (Front)
        buffer.pos(x1, y1, z2).tex(0, 1); ; // Bottom Left Of The Quad (Front)
        buffer.pos(x2, y1, z2).tex(0, 1); ; // Bottom Right Of The Quad (Front)
    }

    @Override
    protected void drawBack(BufferBuilder buffer, float x1, float y1, float z1, float x2, float y2, float z2){
        buffer.pos(x2, y1, z1).tex(0, 1); ; // Top Right Of The Quad (Back)
        buffer.pos(x1, y1, z1).tex(0, 1); ; // Top Left Of The Quad (Back)
        buffer.pos(x1, y2, z1).tex(0, 1); ; // Bottom Left Of The Quad (Back)
        buffer.pos(x2, y2, z1).tex(0, 1); ; // Bottom Right Of The Quad (Back)
    }

    @Override
    protected void drawLeft(BufferBuilder buffer, float x1, float y1, float z1, float x2, float y2, float z2){
        buffer.pos(x1, y2, z2).tex(0, 1); ; // Top Right Of The Quad (Left)
        buffer.pos(x1, y2, z1).tex(0, 1); ; // Top Left Of The Quad (Left)
        buffer.pos(x1, y1, z1).tex(0, 1); ; // Bottom Left Of The Quad (Left)
        buffer.pos(x1, y1, z2).tex(0, 1); ; // Bottom Right Of The Quad (Left)
    }

    @Override
    protected void drawRight(BufferBuilder buffer, float x1, float y1, float z1, float x2, float y2, float z2){
        buffer.pos(x2, y2, z1).tex(0, 1); ; // Top Right Of The Quad (Right)
        buffer.pos(x2, y2, z2).tex(0, 1); ; // Top Left Of The Quad (Right)
        buffer.pos(x2, y1, z2).tex(0, 1); ; // Bottom Left Of The Quad (Right)
        buffer.pos(x2, y1, z1).tex(0, 1); ; // Bottom Right Of The Quad (Right)
    }


}
