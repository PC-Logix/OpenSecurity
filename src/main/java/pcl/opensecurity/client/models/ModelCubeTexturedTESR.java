package pcl.opensecurity.client.models;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
/* this class might be buggy... */
public class ModelCubeTexturedTESR extends ModelCubeTESR {
    double w = 0, h = 0;

    public ModelCubeTexturedTESR(float x1, float y1, float z1, float x2, float y2, float z2){
        super(x1, y1, z1, x2, y2, z2);
        vertexFormat = DefaultVertexFormats.POSITION_TEX;
        updateWH();
    }

    private void updateWH(){
        w = Math.abs(p1.x) + Math.abs(p2.x);
        h = Math.abs(p1.y) + Math.abs(p2.y);
    }

    @Override
    public void setP1(float x, float y, float z){
        super.setP1(x, y, z);
        updateWH();
    }

    @Override
    public void setP2(float x, float y, float z){
        super.setP2(x, y, z);
        updateWH();
    }

    public void drawCube(){
        preRender();
        drawCube(buffer, p1.x, p1.y, p1.z, p2.x, p2.y, p2.z);
        tess.draw();
    }

    protected void drawCube(BufferBuilder buffer, float x1, float y1, float z1, float x2, float y2, float z2){
        drawTop(buffer, x1, y1, z1, x2, y2, z2);
        drawBottom(buffer, x1, y1, z1, x2, y2, z2);
        drawFront(buffer, x1, y1, z1, x2, y2, z2);
        drawBack(buffer, x1, y1, z1, x2, y2, z2);
        drawLeft(buffer, x1, y1, z1, x2, y2, z2);
        drawRight(buffer, x1, y1, z1, x2, y2, z2);
    }

    @Override
    protected void drawTop(BufferBuilder buffer, float x1, float y1, float z1, float x2, float y2, float z2){
        buffer.pos(x2, y2, z2).tex(0, 1).endVertex(); ; // Bottom Right Of The Quad (Top)
        buffer.pos(x1, y2, z2).tex(1, 1).endVertex(); ; // Bottom Left Of The Quad (Top)
        buffer.pos(x1, y2, z1).tex(1, 0).endVertex(); ; // Top Left Of The Quad (Top)
        buffer.pos(x2, y2, z1).tex(0, 0).endVertex(); // Top Right Of The Quad (Top)
    }

    @Override
    protected void drawBottom(BufferBuilder buffer, float x1, float y1, float z1, float x2, float y2, float z2){
        buffer.pos(x2, y1, z1).tex(0, 1).endVertex(); ; // Bottom Right Of The Quad (Bottom)
        buffer.pos(x1, y1, z1).tex(0, 1).endVertex(); ; // Bottom Left Of The Quad (Bottom)
        buffer.pos(x1, y1, z2).tex(0, 1).endVertex(); ; // Top Left Of The Quad (Bottom)
        buffer.pos(x2, y1, z2).tex(0, 1).endVertex(); ; // Top Right Of The Quad (Bottom)
    }

    @Override
    protected void drawFront(BufferBuilder buffer, float x1, float y1, float z1, float x2, float y2, float z2){
        buffer.pos(x2, y1, z2).tex(0, h).endVertex(); ; // Bottom Right Of The Quad (Front)
        buffer.pos(x1, y1, z2).tex(w, h).endVertex(); ; // Bottom Left Of The Quad (Front)
        buffer.pos(x1, y2, z2).tex(w, 0).endVertex(); ; // Top Left Of The Quad (Front)
        buffer.pos(x2, y2, z2).tex(0, 0).endVertex(); ; // Top Right Of The Quad (Front)
    }

    @Override
    protected void drawBack(BufferBuilder buffer, float x1, float y1, float z1, float x2, float y2, float z2){
        buffer.pos(x2, y2, z1).tex(0, h).endVertex(); ; // Bottom Right Of The Quad (Back)
        buffer.pos(x1, y2, z1).tex(w, h).endVertex(); ; // Bottom Left Of The Quad (Back)
        buffer.pos(x1, y1, z1).tex(w, 0).endVertex(); ; // Top Left Of The Quad (Back)
        buffer.pos(x2, y1, z1).tex(0, 0).endVertex(); ; // Top Right Of The Quad (Back)
    }

    @Override
    protected void drawLeft(BufferBuilder buffer, float x1, float y1, float z1, float x2, float y2, float z2){
        buffer.pos(x1, y1, z2).tex(0, 1).endVertex(); ; // Bottom Right Of The Quad (Left)
        buffer.pos(x1, y1, z1).tex(0, 1).endVertex(); ; // Bottom Left Of The Quad (Left)
        buffer.pos(x1, y2, z1).tex(0, 1).endVertex(); ; // Top Left Of The Quad (Left)
        buffer.pos(x1, y2, z2).tex(0, 1).endVertex(); ; // Top Right Of The Quad (Left)
    }

    @Override
    protected void drawRight(BufferBuilder buffer, float x1, float y1, float z1, float x2, float y2, float z2){
        buffer.pos(x2, y1, z1).tex(0, 1).endVertex(); ; // Bottom Right Of The Quad (Right)
        buffer.pos(x2, y1, z2).tex(0, 1).endVertex(); ; // Bottom Left Of The Quad (Right)
        buffer.pos(x2, y2, z2).tex(0, 1).endVertex(); ; // Top Left Of The Quad (Right)
        buffer.pos(x2, y2, z1).tex(0, 1).endVertex(); ; // Top Right Of The Quad (Right)
    }

}
