package pcl.opensecurity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.blockentity.EnergyTurretBlockEntity;

public final class EnergyTurretRenderer implements BlockEntityRenderer<EnergyTurretBlockEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(OpenSecurity.id("energy_turret"), "main");
    private static final ResourceLocation TEXTURE = OpenSecurity.id("textures/model/turret.png");

    private final EnergyTurretModel model;

    public EnergyTurretRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new EnergyTurretModel(context.bakeLayer(LAYER));
    }

    @Override
    public void render(EnergyTurretBlockEntity turret, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        model.setup(
                turret.isUpright(),
                turret.getRenderShaft(partialTick),
                turret.getRenderBarrel(partialTick),
                (float) Math.toRadians(turret.getRenderYaw(partialTick)),
                (float) Math.toRadians(turret.getRenderPitch(partialTick))
        );

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutout(TEXTURE));
        model.render(poseStack, consumer, packedLight, packedOverlay);
    }
}
