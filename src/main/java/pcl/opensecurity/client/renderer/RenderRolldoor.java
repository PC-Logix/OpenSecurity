package pcl.opensecurity.client.renderer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import pcl.opensecurity.common.tileentity.TileEntityKeypad;
import pcl.opensecurity.common.tileentity.TileEntityRolldoor;

public class RenderRolldoor extends TileEntitySpecialRenderer<TileEntityRolldoor> {



    @Override
    public void render(TileEntityRolldoor tileEntity, double x, double y, double z, float partialTicks, int destroyStage, float alpha){
        GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);





		GlStateManager.popMatrix();
    }
}
