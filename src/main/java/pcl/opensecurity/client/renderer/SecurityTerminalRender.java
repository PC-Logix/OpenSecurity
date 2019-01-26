package pcl.opensecurity.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import pcl.opensecurity.common.tileentity.TileEntitySecurityTerminal;

import java.util.*;

@SideOnly(Side.CLIENT)
public class SecurityTerminalRender {

    public static void showFoundTerminals(RenderWorldLastEvent evt) {
        HashMap<BlockPos, TileEntitySecurityTerminal> foundPositions = new HashMap<>();
        for(TileEntity te : Minecraft.getMinecraft().player.world.loadedTileEntityList) {
            if (te != null && te instanceof TileEntitySecurityTerminal) {
                foundPositions.putIfAbsent(te.getPos(), (TileEntitySecurityTerminal) te);
            }
        }
        if(foundPositions.size() > 0)
            renderBlocks(evt, foundPositions);
    }

    private static void renderBlocks(RenderWorldLastEvent evt, HashMap<BlockPos, TileEntitySecurityTerminal> blocks) {
        EntityPlayer player = Minecraft.getMinecraft().player;

        double doubleX = player.lastTickPosX + (player.posX - player.lastTickPosX) * evt.getPartialTicks();
        double doubleY = player.lastTickPosY + (player.posY - player.lastTickPosY) * evt.getPartialTicks();
        double doubleZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * evt.getPartialTicks();

        GlStateManager.pushMatrix();
        GlStateManager.translate(-doubleX, -doubleY, -doubleZ);

        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.disableLighting();
        GlStateManager.disableAlpha();
        GlStateManager.glLineWidth(2);
        GlStateManager.color(1, 1, 1);

        for (Map.Entry<BlockPos, TileEntitySecurityTerminal> pos : blocks.entrySet()) {
            if (pos.getValue().isParticleEnabled() && pos.getValue().isEnabled() && (pos.getValue().getOwner().equals(player.getUniqueID().toString()) || pos.getValue().isUserAllowedToBypass(player.getUniqueID().toString())))
                renderBoxOutline(pos.getKey(), pos.getValue());
        }

        GlStateManager.popMatrix();

        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
    }

    private static void renderBoxOutline(BlockPos pos, TileEntitySecurityTerminal te) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        float mx = pos.getX();
        float my = pos.getY();
        float mz = pos.getZ();

        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        renderHighLightedBlocksOutline(buffer, mx, my, mz, te.rangeMod * 8, .9f, .7f, 0, 1);
        tessellator.draw();
    }

    public static void renderHighLightedBlocksOutline(BufferBuilder buffer, float mx, float my, float mz, float range, float r, float g, float b, float a) {
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