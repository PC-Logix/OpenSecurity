package pcl.opensecurity.client.models;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

@SideOnly(Side.CLIENT)
public class ModelCube {
    Vector3f p1, p2;
    
    public ModelCube(float x1, float y1, float z1, float x2, float y2, float z2){
        p1 = new Vector3f(x1, y1, z1);
        p2 = new Vector3f(x2, y2, z2);
    }

    public void drawCube(){
        drawCube(p1.x, p1.y, p1.z, p2.x, p2.y, p2.z);
    }

    public void drawTop(){
        drawTop(p1.x, p1.y, p1.z, p2.x, p2.y, p2.z);
    }

    public void drawBottom(){
        drawBottom(p1.x, p1.y, p1.z, p2.x, p2.y, p2.z);
    }

    public void drawFront(){
        drawFront(p1.x, p1.y, p1.z, p2.x, p2.y, p2.z);
    }

    public void drawBack(){
        drawBack(p1.x, p1.y, p1.z, p2.x, p2.y, p2.z);
    }

    public void drawLeft(){
        drawLeft(p1.x, p1.y, p1.z, p2.x, p2.y, p2.z);
    }

    public void drawRight(){
        drawRight(p1.x, p1.y, p1.z, p2.x, p2.y, p2.z);
    }


    public static void drawCube(float x1, float y1, float z1, float x2, float y2, float z2){
        drawTop(x1, y1, z1, x2, y2, z2);
        drawBottom(x1, y1, z1, x2, y2, z2);
        drawFront(x1, y1, z1, x2, y2, z2);
        drawBack(x1, y1, z1, x2, y2, z2);
        drawLeft(x1, y1, z1, x2, y2, z2);
        drawRight(x1, y1, z1, x2, y2, z2);
    }

    public static void drawTop(float x1, float y1, float z1, float x2, float y2, float z2){
        GlStateManager.glBegin(GL11.GL_QUADS);
        GlStateManager.glVertex3f(x2, y2, z1); // Top Right Of The Quad (Top)
        GlStateManager.glVertex3f(x1, y2, z1); // Top Left Of The Quad (Top)
        GlStateManager.glVertex3f(x1, y2, z2); // Bottom Left Of The Quad (Top)
        GlStateManager.glVertex3f(x2, y2, z2); // Bottom Right Of The Quad (Top)
        GlStateManager.glEnd();
    }

    public static void drawBottom(float x1, float y1, float z1, float x2, float y2, float z2){
        GlStateManager.glBegin(GL11.GL_QUADS);
        GlStateManager.glVertex3f(x2, y1, z2); // Top Right Of The Quad (Bottom)
        GlStateManager.glVertex3f(x1, y1, z2); // Top Left Of The Quad (Bottom)
        GlStateManager.glVertex3f(x1, y1, z1); // Bottom Left Of The Quad (Bottom)
        GlStateManager.glVertex3f(x2, y1, z1); // Bottom Right Of The Quad (Bottom)
        GlStateManager.glEnd();
    }

    public static void drawFront(float x1, float y1, float z1, float x2, float y2, float z2){
        GlStateManager.glBegin(GL11.GL_QUADS);
        GlStateManager.glVertex3f(x2, y2, z2); // Top Right Of The Quad (Front)
        GlStateManager.glVertex3f(x1, y2, z2); // Top Left Of The Quad (Front)
        GlStateManager.glVertex3f(x1, y1, z2); // Bottom Left Of The Quad (Front)
        GlStateManager.glVertex3f(x2, y1, z2); // Bottom Right Of The Quad (Front)
        GlStateManager.glEnd();
    }

    public static void drawBack(float x1, float y1, float z1, float x2, float y2, float z2){
        GlStateManager.glBegin(GL11.GL_QUADS);
        GlStateManager.glVertex3f(x2, y1, z1); // Top Right Of The Quad (Back)
        GlStateManager.glVertex3f(x1, y1, z1); // Top Left Of The Quad (Back)
        GlStateManager.glVertex3f(x1, y2, z1); // Bottom Left Of The Quad (Back)
        GlStateManager.glVertex3f(x2, y2, z1); // Bottom Right Of The Quad (Back)
        GlStateManager.glEnd();
    }

    public static void drawLeft(float x1, float y1, float z1, float x2, float y2, float z2){
        GlStateManager.glBegin(GL11.GL_QUADS);
        GlStateManager.glVertex3f(x1, y2, z2); // Top Right Of The Quad (Left)
        GlStateManager.glVertex3f(x1, y2, z1); // Top Left Of The Quad (Left)
        GlStateManager.glVertex3f(x1, y1, z1); // Bottom Left Of The Quad (Left)
        GlStateManager.glVertex3f(x1, y1, z2); // Bottom Right Of The Quad (Left)
        GlStateManager.glEnd();
    }

    public static void drawRight(float x1, float y1, float z1, float x2, float y2, float z2){
        GlStateManager.glBegin(GL11.GL_QUADS);
        GlStateManager.glVertex3f(x2, y2, z1); // Top Right Of The Quad (Right)
        GlStateManager.glVertex3f(x2, y2, z2); // Top Left Of The Quad (Right)
        GlStateManager.glVertex3f(x2, y1, z2); // Bottom Left Of The Quad (Right)
        GlStateManager.glVertex3f(x2, y1, z1); // Bottom Right Of The Quad (Right)
        GlStateManager.glEnd();
    }
    
    
}
