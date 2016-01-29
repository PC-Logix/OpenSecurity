package pcl.opensecurity.client.renderer;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper;

import org.lwjgl.opengl.GL11;

import pcl.opensecurity.tileentity.TileEntityEnergyTurret;

public class RenderItemEnergyTurret
  implements IItemRenderer
{
  private RenderEnergyTurret render;
  private TileEntityEnergyTurret entity = new TileEntityEnergyTurret();
  
  public RenderItemEnergyTurret(RenderEnergyTurret render)
  {
    this.render = render;
  }
  
  public boolean handleRenderType(ItemStack item, IItemRenderer.ItemRenderType type)
  {
    return true;
  }
  
  public boolean shouldUseRenderHelper(IItemRenderer.ItemRenderType type, ItemStack item, IItemRenderer.ItemRendererHelper helper)
  {
    return true;
  }
  
  public void renderItem(IItemRenderer.ItemRenderType type, ItemStack item, Object... data)
  {
    if (type == IItemRenderer.ItemRenderType.ENTITY) {
      GL11.glTranslatef(-0.5F, 0.0F, -0.5F);
    }
    GL11.glTranslatef(0.0F, -0.2F, 0.0F);
    GL11.glScalef(0.85F, 0.85F, 0.85F);
    this.render.renderTileEntityAt(this.entity, 0.0D, 0.0D, 0.0D, 0.0F);
  }
}
