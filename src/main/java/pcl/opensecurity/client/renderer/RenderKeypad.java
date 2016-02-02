package pcl.opensecurity.client.renderer;

import pcl.opensecurity.ContentRegistry;
import pcl.opensecurity.tileentity.TileEntityKeypadLock;
import pcl.opensecurity.tileentity.TileEntityKeypadLock.ButtonState;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;

public class RenderKeypad extends TileEntitySpecialRenderer implements IItemRenderer {

	static float texPixel=1.0f/16f;
	
	static ButtonState itemButtonStates[];
	static String default_labels[] = new String[] {"1","2","3","4","5","6","7","8","9","*","0","#"};
	static byte default_colors[] = new byte[] {7,7,7, 7,7,7, 7,7,7, 7,7,7};
	static ButtonPosition buttons[] = null;
	static ButtonPosition display = null;

	static {
		itemButtonStates=new ButtonState[12];
		for(int i=0; i<12; ++i)
			itemButtonStates[i]=new ButtonState();

		buttons=new ButtonPosition[12];
		
		buttons[0] =new ButtonPosition(10f, 9.5f, 2f, 2f);
		buttons[1] =new ButtonPosition( 7f, 9.5f, 2f, 2f);
		buttons[2] =new ButtonPosition( 4f, 9.5f, 2f, 2f);

		buttons[3] =new ButtonPosition(10f,  7f, 2f, 2f);
		buttons[4] =new ButtonPosition( 7f,  7f, 2f, 2f);
		buttons[5] =new ButtonPosition( 4f,  7f, 2f, 2f);

		buttons[6] =new ButtonPosition(10f,  4.5f, 2f, 2f);
		buttons[7] =new ButtonPosition( 7f,  4.5f, 2f, 2f);
		buttons[8] =new ButtonPosition( 4f,  4.5f, 2f, 2f);

		buttons[9] =new ButtonPosition(10f,  2f, 2f, 2f);
		buttons[10]=new ButtonPosition( 7f,  2f, 2f, 2f);
		buttons[11]=new ButtonPosition( 4f,  2f, 2f, 2f);

		display = new ButtonPosition( 4f, 12f, 8f, 2f);
	}
	
	public static void renderButtonGeometry(Tessellator tessellator, float depth, ButtonPosition pos)
	{
		float tx=texPixel*2,ty=texPixel*2;
		float x=pos.x*texPixel;
		float y=pos.y*texPixel;
		float z=depth;
		float w=pos.w*texPixel;
		float h=pos.h*texPixel;
			
		tessellator.setNormal(0f,0f,-1f);
		tessellator.addVertexWithUV(x,   y,   z, tx, ty);
		tessellator.addVertexWithUV(x,   y+h, z, tx, ty+2f*texPixel);
		tessellator.addVertexWithUV(x+w, y+h, z, tx+2f*texPixel, ty+2f*texPixel);
		tessellator.addVertexWithUV(x+w, y,   z, tx+2f*texPixel, ty);
			
		tessellator.setNormal(-1f,0f,0f);
			
		tessellator.addVertexWithUV(x,   y,   z, tx, ty);
		tessellator.addVertexWithUV(x,   y,   z+1f, tx+2f*texPixel, ty);
		tessellator.addVertexWithUV(x,   y+h, z+1f, tx+2f*texPixel, ty+2f*texPixel);
		tessellator.addVertexWithUV(x,   y+h, z, tx, ty+2f*texPixel);
			
		tessellator.setNormal(1f,0f,0f);
		tessellator.addVertexWithUV(x+w,   y,   z+1f, tx+2f*texPixel, ty);
		tessellator.addVertexWithUV(x+w,   y,   z, tx, ty);
		tessellator.addVertexWithUV(x+w,   y+h, z, tx, ty+2f*texPixel);
		tessellator.addVertexWithUV(x+w,   y+h, z+1f, tx+2f*texPixel, ty+2f*texPixel);
			
		tessellator.setNormal(0f,-1f,0f);
		tessellator.addVertexWithUV(x,     y,   z, tx, ty);
		tessellator.addVertexWithUV(x+w,   y,   z, tx+2f*texPixel, ty);
		tessellator.addVertexWithUV(x+w,   y,   z+1f, tx+2f*texPixel, ty+2f*texPixel);
		tessellator.addVertexWithUV(x,     y,   z+1f, tx, ty+2f*texPixel);

		tessellator.setNormal(0f,1f,0f);
		tessellator.addVertexWithUV(x,     y+h,   z+1f, tx+2f*texPixel, ty);
		tessellator.addVertexWithUV(x+w,   y+h,   z+1f, tx+2f*texPixel, ty+2f*texPixel);
		tessellator.addVertexWithUV(x+w,   y+h,   z, tx, ty+2f*texPixel);
		tessellator.addVertexWithUV(x,     y+h,   z, tx, ty);
	}
		
	public static void writeButtonLabel(FontRenderer font, float depth, ButtonPosition pos, int color, String label)
	{
		float x=pos.x*texPixel;
		float y=pos.y*texPixel;
		float w=pos.w*texPixel;
		float h=pos.h*texPixel;

		GL11.glPushMatrix();
			
		GL11.glTranslatef(x+w/2f, y+h/2f, depth+texPixel*-.07f);
		int labelW=font.getStringWidth(label);
		float scale=Math.min(h/10F, 0.8F*w/labelW);
		GL11.glScalef(-scale,-scale,scale);
		GL11.glTranslatef(.5f,.5f,0f);
		GL11.glDepthMask(false);
		int argb = 0xFF000000;
		if((color&4)!=0) argb|=0xFF0000;
		if((color&2)!=0) argb|=0xFF00;
		if((color&1)!=0) argb|=0xFF;
		font.drawString(label, -labelW/2, -4, argb);
		GL11.glDepthMask(true);
			
		GL11.glPopMatrix();
	}
	
	static class ButtonPosition
	{
		public float x, y;
		public float w, h;

		ButtonPosition(float x, float y, float w, float h)
		{
			this.x=x;
			this.y=y;
			this.w=w;
			this.h=h;
		}
	}

	public RenderKeypad()
	{
		super();
	}
	
	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float f) 
	{
		TileEntityKeypadLock te=(TileEntityKeypadLock)tileEntity;
		Tessellator tessellator=Tessellator.instance;
		
		int bx=te.xCoord, by=te.yCoord, bz=te.zCoord;
		
		World world=te.getWorldObj();
		
		float brightness=ContentRegistry.keypadLock.getLightValue(world, bx, by, bz);
		int light=world.getLightBrightnessForSkyBlocks(bx,by,bz,0);
		
		tessellator.setColorOpaque_F(brightness,brightness,brightness);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)(light&0xffff),(float)(light>>16));
		
		GL11.glPushMatrix();
		GL11.glTranslatef((float)x, (float)y,(float)z);
		GL11.glTranslatef(.5f,0,.5f);
		GL11.glRotatef(te.getAngle(),0f,1f,0f);
		GL11.glTranslatef(-.5f,0,-.5f);
		
		long time = world.getTotalWorldTime();
		
		this.bindTexture(new ResourceLocation("opensecurity", "textures/blocks/machine_side.png"));

		drawKeypadBlock(te, time);
		
		GL11.glPopMatrix();
	}
	
	public void drawKeypadBlock(TileEntityKeypadLock keylock, long time)
	{
		Tessellator tessellator=Tessellator.instance;
		
		tessellator.startDrawingQuads();
		tessellator.setNormal(0f, 0f, -1f);
		//inset face
		tessellator.addVertexWithUV( texPixel,    texPixel,    texPixel, texPixel,    1f-texPixel);
		tessellator.addVertexWithUV( texPixel,    1f-texPixel, texPixel, texPixel,    texPixel);
		tessellator.addVertexWithUV( 1f-texPixel, 1f-texPixel, texPixel, 1f-texPixel, texPixel);
		tessellator.addVertexWithUV( 1f-texPixel, texPixel,    texPixel, 1f-texPixel, 1f-texPixel);

		
		//bottom lip front
		tessellator.addVertexWithUV( 0f,          0f,          0.001f,  0f,          0f);
		tessellator.addVertexWithUV( texPixel,    texPixel,    0.001f,  texPixel,    texPixel);
		tessellator.addVertexWithUV( 1f-texPixel, texPixel,    0.001f,  1f-texPixel, texPixel);
		tessellator.addVertexWithUV( 1f,          0f,          0.001f,  1f,          0f);
		//top lip front
		tessellator.addVertexWithUV( texPixel,    1f-texPixel, 0.001f,  texPixel,    1f-texPixel);
		tessellator.addVertexWithUV( 0f,          1f,          0.001f,  0f,          1f);
		tessellator.addVertexWithUV( 1f,          1f,          0.001f,  1f,          1f);
		tessellator.addVertexWithUV( 1f-texPixel, 1f-texPixel, 0.001f,   1f-texPixel, 1f-texPixel);
		//right lip front
		tessellator.addVertexWithUV( 0f,          0f,          0.001f,  0f,          0f);
		tessellator.addVertexWithUV( 0f,          1f,          0.001f,  0f,          1f);
		tessellator.addVertexWithUV( texPixel,    1f-texPixel, 0.001f,  texPixel,    1f-texPixel);
		tessellator.addVertexWithUV( texPixel,    texPixel,    0.001f,  texPixel,    texPixel);
		//left lip front
		tessellator.addVertexWithUV( 1f-texPixel, texPixel,    0.001f,  1f-texPixel, texPixel);
		tessellator.addVertexWithUV( 1f-texPixel, 1f-texPixel, 0.001f,  1f-texPixel, 1f-texPixel);
		tessellator.addVertexWithUV( 1f,          1f,          0.001f,  1f,          1f);
		tessellator.addVertexWithUV( 1f,          0f,          0.001f,  1f,          0f);

		//bottom lip inside
		tessellator.setNormal(0f,1f,0f);
		tessellator.addVertexWithUV( texPixel,    texPixel,    0f,       texPixel,    1f);
		tessellator.addVertexWithUV( texPixel,    texPixel,    texPixel, texPixel,    1f-texPixel);
		tessellator.addVertexWithUV( 1f-texPixel, texPixel,    texPixel, 1f-texPixel, 1f-texPixel);
		tessellator.addVertexWithUV( 1f-texPixel, texPixel,    0f,       1f-texPixel, 1f);
		//top lip inside
		tessellator.setNormal(0f,-1f,0f);
		tessellator.addVertexWithUV( texPixel,    1f-texPixel, texPixel, texPixel,    texPixel);
		tessellator.addVertexWithUV( texPixel,    1f-texPixel, 0f,       texPixel,    0f);
		tessellator.addVertexWithUV( 1f-texPixel, 1f-texPixel, 0f,       1f-texPixel, 0f);
		tessellator.addVertexWithUV( 1f-texPixel, 1f-texPixel, texPixel, 1f-texPixel, texPixel);
		//right lip inside
		tessellator.setNormal(1f,0f,0f);
		tessellator.addVertexWithUV( texPixel,    texPixel,    0f,       1f-texPixel, texPixel);
		tessellator.addVertexWithUV( texPixel,    1f-texPixel, 0f,       1f-texPixel, 1f-texPixel);
		tessellator.addVertexWithUV( texPixel,    1f-texPixel, texPixel, 1f,          1f-texPixel);
		tessellator.addVertexWithUV( texPixel,    texPixel,    texPixel, 1f,          texPixel);
		//left lip inside
		tessellator.setNormal(-1f,0f,0f);
		tessellator.addVertexWithUV( 1f-texPixel, texPixel,    texPixel, 1f,          texPixel);
		tessellator.addVertexWithUV( 1f-texPixel, 1f-texPixel, texPixel, 1f,          1f-texPixel);
		tessellator.addVertexWithUV( 1f-texPixel, 1f-texPixel, 0f,       1f-texPixel, 1f-texPixel);
		tessellator.addVertexWithUV( 1f-texPixel, texPixel,    0f,       1f-texPixel, texPixel);
				
		tessellator.draw();		

		tessellator.startDrawingQuads();
		tessellator.setBrightness(255);

		boolean pressed[] = new boolean[12];
		for(int i=0; i<pressed.length; ++i)
			pressed[i] = keylock!=null ? keylock.buttonStates[i].isPressed(time) : false;

		for (int i=0; i<12; ++i)
			renderButtonGeometry(tessellator, pressed[i]?texPixel*.75f:0f, buttons[i]);
		
		renderButtonGeometry(tessellator, texPixel*.5f, display);

		tessellator.draw();		
		
		FontRenderer font=this.func_147498_b();
		if (font!=null)
		{
			String btnLabels[] = keylock!=null ? keylock.buttonLabels : default_labels;
			byte btnColors[] = keylock!=null ? keylock.buttonColors : default_colors;
			String fbText = keylock!=null ? keylock.displayText : "";
			byte fbColor = keylock!=null ? keylock.displayColor : 7;
			
			for (int i=0; i<12; ++i)
			{
				String lbl = btnLabels[i];
				if(lbl==null) lbl=default_labels[i];
				if(lbl.length()>0)
					writeButtonLabel(font, pressed[i]?texPixel*.75f:0f, buttons[i], btnColors[i], lbl);
			}

			if (fbText!=null && fbText.length()>0)
				writeButtonLabel(font, texPixel*.5f, display, fbColor, fbText);
		}
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		GL11.glPushMatrix();		

		if (type==ItemRenderType.EQUIPPED_FIRST_PERSON)
		{
			GL11.glTranslatef(.5f,0,.5f);
			GL11.glRotatef(90,0f,1f,0f);
			GL11.glTranslatef(-.5f,0,-.5f);			
		}
		else if (type!=ItemRenderType.ENTITY)
		{
			GL11.glTranslatef(.5f,0,.5f);
			GL11.glRotatef(180,0f,1f,0f);
			GL11.glTranslatef(-.5f,0,-.5f);
		}
		else
		{
			//only entity left
			GL11.glTranslatef(-.5f,-.5f,-.5f);
		}
		
		//have to draw the sides ourselves here!
		this.bindTexture(new ResourceLocation("opensecurity", "textures/blocks/machine_side.png"));
		
		GL11.glBegin(GL11.GL_QUADS);
		
		GL11.glNormal3f(0f,1f,0f);
		GL11.glTexCoord2f(0, 0);  GL11.glVertex3f(0f, 1f, 0f);
		GL11.glTexCoord2f(0, 1);  GL11.glVertex3f(0f, 1f, 1f);
		GL11.glTexCoord2f(1, 1);  GL11.glVertex3f(1f, 1f, 1f);
		GL11.glTexCoord2f(1, 0);  GL11.glVertex3f(1f, 1f, 0f);
		
		GL11.glEnd();
		
//		this.bindTexture(new ResourceLocation("opensecurity", "textures/blocks/machine_side.png"));

		GL11.glBegin(GL11.GL_QUADS);
		
		GL11.glNormal3f(-1f,0f,0f);		
	 	GL11.glTexCoord2f(0, 0);  GL11.glVertex3f(0f, 1f, 0f);
		GL11.glTexCoord2f(0, 1);  GL11.glVertex3f(0f, 0f, 0f);
		GL11.glTexCoord2f(1, 1);  GL11.glVertex3f(0f, 0f, 1f);
		GL11.glTexCoord2f(1, 0);  GL11.glVertex3f(0f, 1f, 1f);
	
		if (type!=ItemRenderType.INVENTORY)
		{
			//other 3 faces
			GL11.glNormal3f(1f,0f,0f);
			GL11.glTexCoord2f(0, 0);  GL11.glVertex3f(1f, 1f, 0f);
			GL11.glTexCoord2f(1, 0);  GL11.glVertex3f(1f, 1f, 1f);
			GL11.glTexCoord2f(1, 1);  GL11.glVertex3f(1f, 0f, 1f);
			GL11.glTexCoord2f(0, 1);  GL11.glVertex3f(1f, 0f, 0f);
					
			GL11.glNormal3f(0f,0f,1f);
			GL11.glTexCoord2f(0, 1);  GL11.glVertex3f(0f, 0f, 1f);
			GL11.glTexCoord2f(1, 1);  GL11.glVertex3f(1f, 0f, 1f);
			GL11.glTexCoord2f(1, 0);  GL11.glVertex3f(1f, 1f, 1f);
			GL11.glTexCoord2f(0, 0);  GL11.glVertex3f(0f, 1f, 1f);
					
			GL11.glEnd();
	
//			this.bindTexture(new ResourceLocation("opensecurity", "textures/blocks/machine_side.png"));
			
			GL11.glBegin(GL11.GL_QUADS);

			GL11.glNormal3f(0f,-1f,0f);
			GL11.glTexCoord2f(0, 0);  GL11.glVertex3f(0f, 0f, 0f);
			GL11.glTexCoord2f(1, 0);  GL11.glVertex3f(1f, 0f, 0f);
			GL11.glTexCoord2f(1, 1);  GL11.glVertex3f(1f, 0f, 1f);
			GL11.glTexCoord2f(0, 1);  GL11.glVertex3f(0f, 0f, 1f);
			
		}
			
		GL11.glEnd();
			
//		this.bindTexture(new ResourceLocation("opensecurity", "textures/blocks/machine_side.png"));

		drawKeypadBlock( null, 10000);
			
		GL11.glPopMatrix();
	}

}