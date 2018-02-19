package pcl.opensecurity.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.opengl.GL11;
import pcl.opensecurity.common.tileentity.TileEntitySecurityTerminal;

import java.util.*;

public class SecurityTerminalRender {

    public static long time = -1;
    public static HashMap<BlockPos, TileEntitySecurityTerminal> foundPositions = new HashMap<>();

    public static void showFoundTerminals(RenderWorldLastEvent evt) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        List<TileEntity> tes =  player.world.loadedTileEntityList;
        foundPositions.clear();
        for(TileEntity te : tes) {
            if (te instanceof TileEntitySecurityTerminal && !foundPositions.containsKey(te.getPos())) {
                foundPositions.put(te.getPos(), (TileEntitySecurityTerminal) te);
            }
        }
        renderBlocks(evt, foundPositions);
    }

    private static void renderBlocks(RenderWorldLastEvent evt, HashMap<BlockPos, TileEntitySecurityTerminal> blocks) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;

        double doubleX = player.lastTickPosX + (player.posX - player.lastTickPosX) * evt.getPartialTicks();
        double doubleY = player.lastTickPosY + (player.posY - player.lastTickPosY) * evt.getPartialTicks();
        double doubleZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * evt.getPartialTicks();

        GlStateManager.pushMatrix();
        GlStateManager.translate(-doubleX, -doubleY, -doubleZ);

        GlStateManager.disableDepth();
        GlStateManager.enableTexture2D();

        for (Map.Entry<BlockPos, TileEntitySecurityTerminal> pos : blocks.entrySet()) {
            if (pos.getValue().isParticleEnabled() && pos.getValue().isEnabled() && (pos.getValue().getOwner().equals(player.getUniqueID().toString()) || pos.getValue().isUserAllowedToBypass(player.getUniqueID().toString())))
                renderBoxOutline(pos.getKey(), pos.getValue());
        }

        GlStateManager.enableDepth();

        GlStateManager.popMatrix();
    }

    private static void renderBoxOutline(BlockPos pos, TileEntitySecurityTerminal te) {
        net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
        Minecraft.getMinecraft().entityRenderer.disableLightmap();
        GlStateManager.disableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.disableLighting();
        GlStateManager.disableAlpha();
        GlStateManager.glLineWidth(2);
        GlStateManager.color(1, 1, 1);

        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getBuffer();
        float mx = pos.getX();
        float my = pos.getY();
        float mz = pos.getZ();
        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        renderHighLightedBlocksOutline(buffer, mx, my, mz, te.rangeMod * 8, .9f, .7f, 0, 1);

        tessellator.draw();

        Minecraft.getMinecraft().entityRenderer.enableLightmap();
        GlStateManager.enableTexture2D();
    }

    public static void renderHighLightedBlocksOutline(VertexBuffer buffer, float mx, float my, float mz, float range, float r, float g, float b, float a) {
        //Bottom left
        buffer.pos(mx-range, my-range, mz-range).color(r, g, b, a).endVertex();
        buffer.pos(mx+range+1, my-range, mz-range).color(r, g, b, a).endVertex();

        //front left upright
        buffer.pos(mx-range, my-range, mz-range).color(r, g, b, a).endVertex();
        buffer.pos(mx-range, my+range+1, mz-range).color(r, g, b, a).endVertex();

        //Bottom front
        buffer.pos(mx-range, my-range, mz-range).color(r, g, b, a).endVertex();
        buffer.pos(mx-range, my-range, mz+range+1).color(r, g, b, a).endVertex();

        //top right
        buffer.pos(mx+range+1, my+range+1, mz+range+1f).color(r, g, b, a).endVertex();
        buffer.pos(mx-range, my+range+1, mz+range+1f).color(r, g, b, a).endVertex();

        //Back right upright
        buffer.pos(mx+range+1, my+range+1, mz+range+1).color(r, g, b, a).endVertex();
        buffer.pos(mx+range+1, my-range, mz+range+1).color(r, g, b, a).endVertex();

        //Top back
        buffer.pos(mx+range+1, my+range+1, mz+range+1).color(r, g, b, a).endVertex();
        buffer.pos(mx+range+1, my+range+1, mz-range).color(r, g, b, a).endVertex();

        //Top Front
        buffer.pos(mx-range, my+range+1, mz-range).color(r, g, b, a).endVertex();
        buffer.pos(mx-range, my+range+1, mz+range+1).color(r, g, b, a).endVertex();
        //Top Left
        buffer.pos(mx-range, my+range+1, mz-range).color(r, g, b, a).endVertex();
        buffer.pos(mx+range+1, my+range+1, mz-range).color(r, g, b, a).endVertex();

        //Bottom Back
        buffer.pos(mx+range+1, my-range, mz-range).color(r, g, b, a).endVertex();
        buffer.pos(mx+range+1, my-range, mz+range+1).color(r, g, b, a).endVertex();

        //Back left upright
        buffer.pos(mx+range+1, my-range, mz-range).color(r, g, b, a).endVertex();
        buffer.pos(mx+range+1, my+range+1, mz-range).color(r, g, b, a).endVertex();

        //Bottom right
        buffer.pos(mx-range, my-range, mz+range+1).color(r, g, b, a).endVertex();
        buffer.pos(mx+range+1, my-range, mz+range+1).color(r, g, b, a).endVertex();

        //Front right upright
        buffer.pos(mx-range, my-range, mz+range+1).color(r, g, b, a).endVertex();
        buffer.pos(mx-range, my+range+1, mz+range+1).color(r, g, b, a).endVertex();
    }
}