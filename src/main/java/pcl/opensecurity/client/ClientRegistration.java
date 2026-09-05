package pcl.opensecurity.client;

import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.data.CardData;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;
import pcl.opensecurity.blockentity.RollDoorBlockEntity;
import pcl.opensecurity.blockentity.DoorControllerBlockEntity;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

@EventBusSubscriber(modid = OpenSecurity.MOD_ID, value = Dist.CLIENT)
public final class ClientRegistration {
    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> tintIndex == 1 ? CardData.read(stack).color() : 0xFFFFFFFF,
                OpenSecurity.RFID_CARD.get(), OpenSecurity.MAG_CARD.get());
    }

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register((state, level, pos, tintIndex) -> {
            if (level == null || pos == null) return 0xFFFFFFFF;
            if (state.is(OpenSecurity.DOOR_CONTROLLER.get())
                    && level.getBlockEntity(pos) instanceof DoorControllerBlockEntity controller
                    && controller.getCamouflage() != null) {
                return Minecraft.getInstance().getBlockColors().getColor(controller.getCamouflage(), level, pos, tintIndex);
            }
            if (state.is(OpenSecurity.DOOR_CONTROLLER.get())) return 0xFFFFFFFF;
            RollDoorBlockEntity exact = RollDoorBlockEntity.at(level, pos);
            if (exact != null && exact.getCamouflage() != null) {
                return Minecraft.getInstance().getBlockColors().getColor(exact.getCamouflage(), level, pos, tintIndex);
            }
            RollDoorBlockEntity door = RollDoorBlockEntity.findHeader(level, pos);
            if (door == null) return 0xFFFFFFFF;
            DyeColor color = door.getColor();
            return color == null ? 0xFFFFFFFF : color.getTextureDiffuseColor();
        }, OpenSecurity.DOOR_CONTROLLER.get(), OpenSecurity.ROLLDOOR.get(), OpenSecurity.ROLLDOOR_ELEMENT.get());
    }

    @SubscribeEvent
    public static void registerRollDoorModels(ModelEvent.ModifyBakingResult event) {
        for (var block : new net.minecraft.world.level.block.Block[]{
                OpenSecurity.DOOR_CONTROLLER.get(), OpenSecurity.ROLLDOOR.get(), OpenSecurity.ROLLDOOR_ELEMENT.get()}) {
            for (BlockState state : block.getStateDefinition().getPossibleStates()) {
                ModelResourceLocation location = BlockModelShaper.stateToModelLocation(state);
                BakedModel model = event.getModels().get(location);
                if (model != null && !(model instanceof RollDoorBakedModel)) {
                    event.getModels().put(location, new RollDoorBakedModel(model));
                }
            }
        }
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(EnergyTurretRenderer.LAYER, EnergyTurretModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(OpenSecurity.ENERGY_BOLT.get(), context -> new ThrownItemRenderer<>(context, 1.0F, true));
        event.registerEntityRenderer(OpenSecurity.NANO_FOG_SWARM.get(), NanoFogSwarmRenderer::new);
        event.registerBlockEntityRenderer(OpenSecurity.ENERGY_TURRET_BE.get(), EnergyTurretRenderer::new);
    }

    private ClientRegistration() {}
}
