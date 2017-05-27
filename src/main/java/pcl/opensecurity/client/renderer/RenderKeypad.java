package pcl.opensecurity.client.renderer;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityBoat.Type;
import net.minecraft.item.ItemStack;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import pcl.opensecurity.ContentRegistry;
import pcl.opensecurity.common.tileentity.TileEntityKeypad;
import pcl.opensecurity.common.tileentity.TileEntityKeypad.ButtonState;

public class RenderKeypad  extends TileEntitySpecialRenderer<TileEntityKeypad> {

	static float texPixel=1.0f/16f;

	static ButtonState[] itemButtonStates;
	static String[] default_labels = new String[] {"1","2","3",
												   "4","5","6",
												   "7","8","9",
												   "*","0","#"};
	static byte[] default_colors = new byte[] {7,7,7,
											   7,7,7,
											   7,7,7,
											   7,7,7};
	static ButtonPosition[] buttons = null;
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

	public static void renderButtonGeometry(VertexBuffer vertexbuffer, float depth, ButtonPosition pos)
	{
		float tx=texPixel*2;
		float ty=texPixel*2;
		float x=pos.x*texPixel;
		float y=pos.y*texPixel;
		float z=depth;
		float w=pos.w*texPixel;
		float h=pos.h*texPixel;

		//tessellator.setNormal(0f,0f,-1f);
		vertexbuffer.pos(x,   y,   z   ).tex(tx,             ty            ).normal(0f,0f,-1f).endVertex();
		vertexbuffer.pos(x,   y+h, z   ).tex(tx,             ty+2f*texPixel).normal(0f,0f,-1f).endVertex();
		vertexbuffer.pos(x+w, y+h, z   ).tex(tx+2f*texPixel, ty+2f*texPixel).normal(0f,0f,-1f).endVertex();
		vertexbuffer.pos(x+w, y,   z   ).tex(tx+2f*texPixel, ty            ).normal(0f,0f,-1f).endVertex();

		//tessellator.setNormal(-1f,0f,0f);
		vertexbuffer.pos(x,   y,   z   ).tex(tx,             ty            ).normal(-1f,0f,0f).endVertex();
		vertexbuffer.pos(x,   y,   z+1f).tex(tx+2f*texPixel, ty            ).normal(-1f,0f,0f).endVertex();
		vertexbuffer.pos(x,   y+h, z+1f).tex(tx+2f*texPixel, ty+2f*texPixel).normal(-1f,0f,0f).endVertex();
		vertexbuffer.pos(x,   y+h, z   ).tex(tx,             ty+2f*texPixel).normal(-1f,0f,0f).endVertex();

		//tessellator.setNormal(1f,0f,0f);
		vertexbuffer.pos(x+w, y,   z+1f).tex(tx+2f*texPixel, ty            ).normal(1f,0f,0f).endVertex();
		vertexbuffer.pos(x+w, y,   z   ).tex(tx,             ty            ).normal(1f,0f,0f).endVertex();
		vertexbuffer.pos(x+w, y+h, z   ).tex(tx,             ty+2f*texPixel).normal(1f,0f,0f).endVertex();
		vertexbuffer.pos(x+w, y+h, z+1f).tex(tx+2f*texPixel, ty+2f*texPixel).normal(1f,0f,0f).endVertex();

		//tessellator.setNormal(0f,-1f,0f);
		vertexbuffer.pos(x,   y,   z   ).tex(tx,             ty            ).normal(0f,-1f,0f).endVertex();
		vertexbuffer.pos(x+w, y,   z   ).tex(tx+2f*texPixel, ty            ).normal(0f,-1f,0f).endVertex();
		vertexbuffer.pos(x+w, y,   z+1f).tex(tx+2f*texPixel, ty+2f*texPixel).normal(0f,-1f,0f).endVertex();
		vertexbuffer.pos(x,   y,   z+1f).tex(tx,             ty+2f*texPixel).normal(0f,-1f,0f).endVertex();

		//tessellator.setNormal(0f,1f,0f);
		vertexbuffer.pos(x,   y+h, z+1f).tex(tx+2f*texPixel, ty            ).normal(0f,1f,0f).endVertex();
		vertexbuffer.pos(x+w, y+h, z+1f).tex(tx+2f*texPixel, ty+2f*texPixel).normal(0f,1f,0f).endVertex();
		vertexbuffer.pos(x+w, y+h, z   ).tex(tx,             ty+2f*texPixel).normal(0f,1f,0f).endVertex();
		vertexbuffer.pos(x,   y+h, z   ).tex(tx,             ty            ).normal(0f,1f,0f).endVertex();
	}

	public static void writeButtonLabel(FontRenderer font, float depth, ButtonPosition pos, int color, String label)
	{
		//OpenSecurity.logger.info(label);
		float x=pos.x*texPixel;
		float y=pos.y*texPixel;
		float w=pos.w*texPixel;
		float h=pos.h*texPixel;

		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_LIGHTING);
		//GL11.glEnable(GL11.GL_BLEND);
		//GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
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
		GL11.glEnable(GL11.GL_LIGHTING);
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
	public void renderTileEntityAt(TileEntityKeypad tileEntity, double x, double y, double z, float partialTicks, int destroyStage)
	{
		//Temp fix until I can debug this.
		//float light = tileEntity.getWorldObj().getLightBrightnessForSkyBlocks(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord, 15);
		//OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, light, light);
		int li = tileEntity.getWorld().getCombinedLight(tileEntity.getPos(), 15728640);
		int i1 = li % 65536;
		int j1 = li / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) i1, (float) j1);
		TileEntityKeypad te=(TileEntityKeypad)tileEntity;
		Tessellator tessellator=Tessellator.getInstance();

		int bx=te.getPos().getX(), by=te.getPos().getY(), bz=te.getPos().getZ();

		World world=te.getWorld();
        BlockPos bp = te.getPos();
        //IBlockState b = access.getBlockState(bp);
		//float brightness=ContentRegistry.keypadLockBlock.getLightValue(world, bx, by, bz);
		int light=world.getLightFor(EnumSkyBlock.BLOCK, te.getPos());
		
		
		//tessellator.setColorOpaque_F(brightness,brightness,brightness);
		//OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)(light&0xffff),(float)(light>>16));

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

	public void drawKeypadBlock(TileEntityKeypad keylock, long time) {
		Tessellator tessellator=Tessellator.getInstance();
		VertexBuffer vertexbuffer = tessellator.getBuffer();

		vertexbuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL); //tessellator.startDrawingQuads();
		//tessellator.setNormal(0f, 0f, -1f);
		//inset face
		vertexbuffer.pos(texPixel,    texPixel,    texPixel).tex(texPixel,    1f-texPixel).normal(0f, 0f, -1f).endVertex();
		vertexbuffer.pos(texPixel,    1f-texPixel, texPixel).tex(texPixel,    texPixel   ).normal(0f, 0f, -1f).endVertex();
		vertexbuffer.pos(1f-texPixel, 1f-texPixel, texPixel).tex(1f-texPixel, texPixel   ).normal(0f, 0f, -1f).endVertex();
		vertexbuffer.pos(1f-texPixel, texPixel,    texPixel).tex(1f-texPixel, 1f-texPixel).normal(0f, 0f, -1f).endVertex();

		//bottom lip front
		vertexbuffer.pos(0f,          0f,          0.001f).tex(0f,          0f         ).normal(0f,0f,-1f).endVertex();
		vertexbuffer.pos(texPixel,    texPixel,    0.001f).tex(texPixel,    texPixel   ).normal(0f,0f,-1f).endVertex();
		vertexbuffer.pos(1f-texPixel, texPixel,    0.001f).tex(1f-texPixel, texPixel   ).normal(0f,0f,-1f).endVertex();
		vertexbuffer.pos(1f,          0f,          0.001f).tex(1f,          0f         ).normal(0f,0f,-1f).endVertex();
		//top lip front
		vertexbuffer.pos(texPixel,    1f-texPixel, 0.001f).tex(texPixel,    1f-texPixel).normal(0f,0f,-1f).endVertex();
		vertexbuffer.pos(0f,          1f,          0.001f).tex(0f,          1f         ).normal(0f,0f,-1f).endVertex();
		vertexbuffer.pos(1f,          1f,          0.001f).tex(1f,          1f         ).normal(0f,0f,-1f).endVertex();
		vertexbuffer.pos(1f-texPixel, 1f-texPixel, 0.001f).tex(1f-texPixel, 1f-texPixel).normal(0f,0f,-1f).endVertex();
		//right lip front
		vertexbuffer.pos(0f,          0f,          0.001f).tex(0f,          0f         ).normal(0f,0f,-1f).endVertex();
		vertexbuffer.pos(0f,          1f,          0.001f).tex(0f,          1f         ).normal(0f,0f,-1f).endVertex();
		vertexbuffer.pos(texPixel,    1f-texPixel, 0.001f).tex(texPixel,    1f-texPixel).normal(0f,0f,-1f).endVertex();
		vertexbuffer.pos(texPixel,    texPixel,    0.001f).tex(texPixel,    texPixel   ).normal(0f,0f,-1f).endVertex();
		//left lip front
		vertexbuffer.pos(1f-texPixel, texPixel,    0.001f).tex(1f-texPixel, texPixel   ).normal(0f,0f,-1f).endVertex();
		vertexbuffer.pos(1f-texPixel, 1f-texPixel, 0.001f).tex(1f-texPixel, 1f-texPixel).normal(0f,0f,-1f).endVertex();
		vertexbuffer.pos(1f,          1f,          0.001f).tex(1f,          1f         ).normal(0f,0f,-1f).endVertex();
		vertexbuffer.pos(1f,          0f,          0.001f).tex(1f,          0f         ).normal(0f,0f,-1f).endVertex();

		//bottom lip inside
		//tessellator.setNormal(0f,1f,0f);
		vertexbuffer.pos(texPixel,    texPixel,    0f      ).tex(texPixel,    1f         ).normal(0f,1f,0f).endVertex();
		vertexbuffer.pos(texPixel,    texPixel,    texPixel).tex(texPixel,    1f-texPixel).normal(0f,1f,0f).endVertex();
		vertexbuffer.pos(1f-texPixel, texPixel,    texPixel).tex(1f-texPixel, 1f-texPixel).normal(0f,1f,0f).endVertex();
		vertexbuffer.pos(1f-texPixel, texPixel,    0f      ).tex(1f-texPixel, 1f         ).normal(0f,1f,0f).endVertex();
		//top lip inside
		//tessellator.setNormal(0f,-1f,0f);
		vertexbuffer.pos(texPixel,    1f-texPixel, texPixel).tex(texPixel,    texPixel).normal(0f,-1f,0f).endVertex();
		vertexbuffer.pos(texPixel,    1f-texPixel, 0f      ).tex(texPixel,    0f      ).normal(0f,-1f,0f).endVertex();
		vertexbuffer.pos(1f-texPixel, 1f-texPixel, 0f      ).tex(1f-texPixel, 0f      ).normal(0f,-1f,0f).endVertex();
		vertexbuffer.pos(1f-texPixel, 1f-texPixel, texPixel).tex(1f-texPixel, texPixel).normal(0f,-1f,0f).endVertex();
		//right lip inside
		//tessellator.setNormal(1f,0f,0f);
		vertexbuffer.pos(texPixel,    texPixel,    0f      ).tex(1f-texPixel, texPixel   ).normal(1f,0f,0f).endVertex();
		vertexbuffer.pos(texPixel,    1f-texPixel, 0f      ).tex(1f-texPixel, 1f-texPixel).normal(1f,0f,0f).endVertex();
		vertexbuffer.pos(texPixel,    1f-texPixel, texPixel).tex(1f,          1f-texPixel).normal(1f,0f,0f).endVertex();
		vertexbuffer.pos(texPixel,    texPixel,    texPixel).tex(1f,          texPixel   ).normal(1f,0f,0f).endVertex();
		//left lip inside
		//tessellator.setNormal(-1f,0f,0f);
		vertexbuffer.pos(1f-texPixel, texPixel,    texPixel).tex(1f,          texPixel   ).normal(-1f,0f,0f).endVertex();
		vertexbuffer.pos(1f-texPixel, 1f-texPixel, texPixel).tex(1f,          1f-texPixel).normal(-1f,0f,0f).endVertex();
		vertexbuffer.pos(1f-texPixel, 1f-texPixel, 0f      ).tex(1f-texPixel, 1f-texPixel).normal(-1f,0f,0f).endVertex();
		vertexbuffer.pos(1f-texPixel, texPixel,    0f      ).tex(1f-texPixel, texPixel   ).normal(-1f,0f,0f).endVertex();

		tessellator.draw();

		vertexbuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL); //tessellator.startDrawingQuads();
		//tessellator.setBrightness(255);

		boolean[] pressed = new boolean[12];
		for(int i=0; i<pressed.length; ++i)
			pressed[i] = keylock!=null ? keylock.buttonStates[i].isPressed(time) : false;

		for (int i=0; i<12; ++i)
			renderButtonGeometry(vertexbuffer, pressed[i]?texPixel*.75f:0f, buttons[i]);

		renderButtonGeometry(vertexbuffer, 0f, display);

		tessellator.draw();

		FontRenderer font=this.getFontRenderer();
		if (font!=null)
		{
			String[] btnLabels = keylock!=null ? keylock.buttonLabels : default_labels;
			byte[] btnColors = keylock!=null ? keylock.buttonColors : default_colors;
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
				writeButtonLabel(font, 0f, display, fbColor, fbText);
		}
	}
}