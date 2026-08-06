package pcl.opensecurity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * Animated Energy Turret model, ported from the original 1.12 ModelEnergyTurret.
 * The hierarchy is intentional: yaw rotates the entire rotor assembly, pitch rotates
 * the gun, and the nested shaft/barrel parts telescope independently.
 */
public final class EnergyTurretModel {
    private final ModelPart base;
    private final ModelPart rotorBase;
    private final ModelPart rotorShaft1;
    private final ModelPart rotorShaft2;
    private final ModelPart gunBase;
    private final ModelPart gunBarrel;
    private final ModelPart gunEnd;

    public EnergyTurretModel(ModelPart root) {
        this.base = root.getChild("base");
        this.rotorBase = base.getChild("rotor_base");
        this.rotorShaft1 = rotorBase.getChild("rotor_shaft_1");
        this.rotorShaft2 = rotorShaft1.getChild("rotor_shaft_2");
        this.gunBase = rotorShaft2.getChild("gun_base");
        this.gunBarrel = gunBase.getChild("gun_barrel");
        this.gunEnd = gunBarrel.getChild("gun_end");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition base = root.addOrReplaceChild("base",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-8.0F, -8.0F, -8.0F, 16.0F, 6.0F, 16.0F),
                PartPose.offset(8.0F, 8.0F, 8.0F));

        PartDefinition rotorBase = base.addOrReplaceChild("rotor_base",
                CubeListBuilder.create().texOffs(0, 23)
                        .addBox(-4.0F, -2.0F, -4.0F, 8.0F, 1.0F, 8.0F),
                PartPose.ZERO);

        PartDefinition shaft1 = rotorBase.addOrReplaceChild("rotor_shaft_1",
                CubeListBuilder.create().texOffs(32, 22)
                        .addBox(-3.0F, -1.0F, -1.5F, 1.0F, 7.0F, 3.0F)
                        .addBox(2.0F, -1.0F, -1.5F, 1.0F, 7.0F, 3.0F),
                PartPose.ZERO);

        PartDefinition shaft2 = shaft1.addOrReplaceChild("rotor_shaft_2",
                CubeListBuilder.create().texOffs(32, 22)
                        .addBox(-2.0F, -1.0F, -1.5F, 1.0F, 7.0F, 3.0F)
                        .addBox(1.0F, -1.0F, -1.5F, 1.0F, 7.0F, 3.0F),
                PartPose.ZERO);

        PartDefinition gunBase = shaft2.addOrReplaceChild("gun_base",
                CubeListBuilder.create().texOffs(40, 24)
                        .addBox(-1.0F, -2.0F, -2.0F, 2.0F, 4.0F, 4.0F),
                PartPose.offset(0.0F, 5.5F, 0.0F));

        PartDefinition barrel = gunBase.addOrReplaceChild("gun_barrel",
                CubeListBuilder.create().texOffs(0, 32)
                        .addBox(-2.0F, -2.0F, -7.5F, 4.0F, 4.0F, 15.0F),
                PartPose.ZERO);

        barrel.addOrReplaceChild("gun_end",
                CubeListBuilder.create().texOffs(38, 41)
                        .addBox(-1.0F, -1.0F, -4.0F, 2.0F, 2.0F, 8.0F),
                PartPose.ZERO);

        return LayerDefinition.create(mesh, 64, 64);
    }

    public void setup(boolean upright, float shaft, float barrel, float yawRadians, float pitchRadians) {
        base.zRot = upright ? 0.0F : (float) Math.PI;
        rotorBase.yRot = upright ? yawRadians : -yawRadians;
        gunBase.xRot = upright ? pitchRadians : -pitchRadians;

        // Preserve the original 1.12 telescoping geometry. The current API clamps
        // shaft to 0..0.5, but this still supports the old second-stage math if that
        // range is restored later.
        float dy1 = Math.max(0.0F, Math.min(6.0F, shaft * 6.0F));
        float dy2 = Math.max(0.0F, Math.min(6.0F, shaft * 6.0F - 6.0F));
        rotorShaft1.setPos(0.0F, dy1 - 6.0F, 0.0F);
        rotorShaft2.setPos(0.0F, dy2, 0.0F);

        // Arming/disarming telescopes the barrel just like the old model.
        gunBarrel.setPos(0.0F, 2.5F, 3.0F * barrel);
        gunEnd.setPos(0.0F, 0.0F, -10.0F * barrel);
    }

    public void render(PoseStack poseStack, VertexConsumer consumer, int packedLight, int packedOverlay) {
        base.render(poseStack, consumer, packedLight, packedOverlay);
    }
}
