package pcl.opensecurity.client.models;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
/* this class might be buggy... */
public class ModelCubeTESR extends ModelCube {
    Tessellator tess;
    BufferBuilder buffer;
    VertexFormat vertexFormat;
    
    public ModelCubeTESR(float x1, float y1, float z1, float x2, float y2, float z2){
        super(x1, y1, z1, x2, y2, z2);
        vertexFormat = DefaultVertexFormats.POSITION;
    }

    protected void preRender(){
        tess = Tessellator.getInstance();
        buffer = tess.getBuffer();
        buffer.begin(GL11.GL_QUADS, vertexFormat);
    }
    
    public void drawCube(){
        preRender();
        drawCube(buffer, p1.x, p1.y, p1.z, p2.x, p2.y, p2.z);
        tess.draw();
    }

    public void drawTop(){
        preRender();
        drawTop(buffer, p1.x, p1.y, p1.z, p2.x, p2.y, p2.z);
        tess.draw();
    }

    public void drawBottom(){
        preRender();
        drawBottom(buffer, p1.x, p1.y, p1.z, p2.x, p2.y, p2.z);
        tess.draw();
    }

    public void drawFront(){
        preRender();
        drawFront(buffer, p1.x, p1.y, p1.z, p2.x, p2.y, p2.z);
        tess.draw();
    }

    public void drawBack(){
        preRender();
        drawBack(buffer, p1.x, p1.y, p1.z, p2.x, p2.y, p2.z);
        tess.draw();
    }

    public void drawLeft(){
        preRender();
        drawLeft(buffer, p1.x, p1.y, p1.z, p2.x, p2.y, p2.z);
        tess.draw();
    }

    public void drawRight(){
        preRender();
        drawRight(buffer, p1.x, p1.y, p1.z, p2.x, p2.y, p2.z);
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

    protected void drawTop(BufferBuilder buffer, float x1, float y1, float z1, float x2, float y2, float z2){
        buffer.pos(x2, y2, z2); // Bottom Right Of The Quad (Top)
        buffer.pos(x1, y2, z2); // Bottom Left Of The Quad (Top)
        buffer.pos(x1, y2, z1); // Top Left Of The Quad (Top)
        buffer.pos(x2, y2, z1); // Top Right Of The Quad (Top)
    }

    protected void drawBottom(BufferBuilder buffer, float x1, float y1, float z1, float x2, float y2, float z2){
        buffer.pos(x2, y1, z1); // Bottom Right Of The Quad (Bottom)
        buffer.pos(x1, y1, z1); // Bottom Left Of The Quad (Bottom)
        buffer.pos(x1, y1, z2); // Top Left Of The Quad (Bottom)
        buffer.pos(x2, y1, z2); // Top Right Of The Quad (Bottom)
    }

    protected void drawFront(BufferBuilder buffer, float x1, float y1, float z1, float x2, float y2, float z2){
        buffer.pos(x2, y1, z2); // Bottom Right Of The Quad (Front)
        buffer.pos(x1, y1, z2); // Bottom Left Of The Quad (Front)
        buffer.pos(x1, y2, z2); // Top Left Of The Quad (Front)
        buffer.pos(x2, y2, z2); // Top Right Of The Quad (Front)
    }

    protected void drawBack(BufferBuilder buffer, float x1, float y1, float z1, float x2, float y2, float z2){
        buffer.pos(x2, y2, z1); // Bottom Right Of The Quad (Back)
        buffer.pos(x1, y2, z1); // Bottom Left Of The Quad (Back)
        buffer.pos(x1, y1, z1); // Top Left Of The Quad (Back)
        buffer.pos(x2, y1, z1); // Top Right Of The Quad (Back)
    }

    protected void drawLeft(BufferBuilder buffer, float x1, float y1, float z1, float x2, float y2, float z2){
        buffer.pos(x1, y1, z2); // Bottom Right Of The Quad (Left)
        buffer.pos(x1, y1, z1); // Bottom Left Of The Quad (Left)
        buffer.pos(x1, y2, z1); // Top Left Of The Quad (Left)
        buffer.pos(x1, y2, z2); // Top Right Of The Quad (Left)
    }

    protected void drawRight(BufferBuilder buffer, float x1, float y1, float z1, float x2, float y2, float z2){
        buffer.pos(x2, y1, z1); // Bottom Right Of The Quad (Right)
        buffer.pos(x2, y1, z2); // Bottom Left Of The Quad (Right)
        buffer.pos(x2, y2, z2); // Top Left Of The Quad (Right)
        buffer.pos(x2, y2, z1); // Top Right Of The Quad (Right)
    }

}
