package pcl.opensecurity;

import pcl.opensecurity.block.EnergyTurretBlock;
import pcl.opensecurity.block.MagReaderBlock;
import pcl.opensecurity.block.NanoFogBlock;
import pcl.opensecurity.block.RollDoorBlock;
import pcl.opensecurity.block.RollDoorElementBlock;
import pcl.opensecurity.block.SecureDoorBlock;
import pcl.opensecurity.block.SecurityBlock;
import pcl.opensecurity.blockentity.AlarmBlockEntity;
import pcl.opensecurity.blockentity.BiometricReaderBlockEntity;
import pcl.opensecurity.blockentity.CardWriterBlockEntity;
import pcl.opensecurity.blockentity.DataBlockEntity;
import pcl.opensecurity.blockentity.DoorControllerBlockEntity;
import pcl.opensecurity.blockentity.EnergyTurretBlockEntity;
import pcl.opensecurity.blockentity.EntityDetectorBlockEntity;
import pcl.opensecurity.blockentity.KeypadBlockEntity;
import pcl.opensecurity.blockentity.MagReaderBlockEntity;
import pcl.opensecurity.blockentity.NanoFogBlockEntity;
import pcl.opensecurity.blockentity.NanoFogTerminalBlockEntity;
import pcl.opensecurity.blockentity.RFIDReaderBlockEntity;
import pcl.opensecurity.blockentity.RollDoorControllerBlockEntity;
import pcl.opensecurity.blockentity.SecureDoorBlockEntity;
import pcl.opensecurity.blockentity.SecurityTerminalBlockEntity;
import pcl.opensecurity.entity.EnergyBoltEntity;
import pcl.opensecurity.integration.opencomputers.RFIDReaderCardDriver;
import pcl.opensecurity.item.SecurityCardItem;
import pcl.opensecurity.item.TurretUpgradeItem;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(OpenSecurity.MOD_ID)
public final class OpenSecurity {
    public static final String MOD_ID = "opensecurity";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MOD_ID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, MOD_ID);

    public static final DeferredHolder<Block, SecurityBlock> ALARM = block("alarm", SecurityBlock.Kind.ALARM);
    public static final DeferredHolder<Block, SecurityBlock> BIOMETRIC_READER = block("biometric_reader", SecurityBlock.Kind.BIOMETRIC_READER);
    public static final DeferredHolder<Block, SecurityBlock> CARD_WRITER = block("card_writer", SecurityBlock.Kind.CARD_WRITER);
    public static final DeferredHolder<Block, SecurityBlock> DATA_BLOCK = block("data_block", SecurityBlock.Kind.DATA_BLOCK);
    public static final DeferredHolder<Block, SecurityBlock> DOOR_CONTROLLER = block("door_controller", SecurityBlock.Kind.DOOR_CONTROLLER);
    public static final DeferredHolder<Block, SecurityBlock> ENTITY_DETECTOR = block("entity_detector", SecurityBlock.Kind.ENTITY_DETECTOR);
    public static final DeferredHolder<Block, EnergyTurretBlock> ENERGY_TURRET = BLOCKS.register("energy_turret", () ->
            new EnergyTurretBlock(Block.Properties.of().mapColor(MapColor.METAL).strength(2.5F).noOcclusion().sound(SoundType.METAL)));
    public static final DeferredHolder<Block, SecurityBlock> KEYPAD = block("keypad", SecurityBlock.Kind.KEYPAD);
    public static final DeferredHolder<Block, SecurityBlock> NANOFOG_TERMINAL = block("nanofog_terminal", SecurityBlock.Kind.NANOFOG_TERMINAL);
    public static final DeferredHolder<Block, NanoFogBlock> NANOFOG = BLOCKS.register("nanofog", () ->
            new NanoFogBlock(Block.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).strength(-1.0F, 3600000.0F).noOcclusion().noLootTable()));
    public static final DeferredHolder<Block, MagReaderBlock> MAG_READER = BLOCKS.register("mag_reader", () ->
            new MagReaderBlock(Block.Properties.of().mapColor(MapColor.METAL).strength(2.5F).sound(SoundType.METAL)));
    public static final DeferredHolder<Block, MagReaderBlock> MAG_READER_CAMO = BLOCKS.register("mag_reader_camo", () ->
            new MagReaderBlock(Block.Properties.of().mapColor(MapColor.METAL).strength(2.5F).sound(SoundType.METAL)));
    public static final DeferredHolder<Block, SecurityBlock> RFID_READER = block("rfid_reader", SecurityBlock.Kind.RFID_READER);
    public static final DeferredHolder<Block, RollDoorBlock> ROLLDOOR = BLOCKS.register("rolldoor", () ->
            new RollDoorBlock(Block.Properties.of().mapColor(MapColor.METAL).strength(2.5F).sound(SoundType.METAL)));
    public static final DeferredHolder<Block, SecurityBlock> ROLLDOOR_CONTROLLER = block("rolldoor_controller", SecurityBlock.Kind.ROLLDOOR_CONTROLLER);
    public static final DeferredHolder<Block, RollDoorElementBlock> ROLLDOOR_ELEMENT = BLOCKS.register("rolldoor_element", () ->
            new RollDoorElementBlock(Block.Properties.of().mapColor(MapColor.METAL).strength(-1.0F, 3600000.0F).noOcclusion().sound(SoundType.METAL)));
    public static final DeferredHolder<Block, SecurityBlock> SECURITY_TERMINAL = block("security_terminal", SecurityBlock.Kind.SECURITY_TERMINAL);
    public static final DeferredHolder<Block, SecureDoorBlock> SECURE_DOOR = BLOCKS.register("secure_door", () ->
            new SecureDoorBlock(Block.Properties.of().mapColor(MapColor.METAL).strength(5.0F).sound(SoundType.METAL)));
    public static final DeferredHolder<Block, SecureDoorBlock> PRIVATE_SECURE_DOOR = BLOCKS.register("private_secure_door", () ->
            new SecureDoorBlock(Block.Properties.of().mapColor(MapColor.METAL).strength(5.0F).sound(SoundType.METAL)));
    public static final DeferredHolder<Block, SecureDoorBlock> MAG_SECURE_DOOR = BLOCKS.register("mag_secure_door", () ->
            new SecureDoorBlock(Block.Properties.of().mapColor(MapColor.METAL).strength(5.0F).sound(SoundType.METAL)));

    public static final DeferredHolder<Item, SecurityCardItem> RFID_CARD = ITEMS.register("rfid_card", () ->
            new SecurityCardItem(SecurityCardItem.Kind.RFID, new Item.Properties().stacksTo(16)));
    public static final DeferredHolder<Item, SecurityCardItem> MAG_CARD = ITEMS.register("mag_card", () ->
            new SecurityCardItem(SecurityCardItem.Kind.MAG, new Item.Properties().stacksTo(16)));
    public static final DeferredHolder<Item, Item> MAG_READER_ITEM = ITEMS.register("mag_reader", () ->
            new BlockItem(MAG_READER.get(), new Item.Properties()));
    public static final DeferredHolder<Item, Item> MAG_READER_CAMO_ITEM = ITEMS.register("mag_reader_camo", () ->
            new BlockItem(MAG_READER_CAMO.get(), new Item.Properties()));
    public static final DeferredHolder<Item, Item> RFID_READER_CARD = ITEMS.register("rfid_reader_card", () ->
            new Item(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> ENERGY_TURRET_ITEM = ITEMS.register("energy_turret", () ->
            new BlockItem(ENERGY_TURRET.get(), new Item.Properties()));
    public static final DeferredHolder<Item, TurretUpgradeItem> DAMAGE_UPGRADE = ITEMS.register("damage_upgrade", () ->
            new TurretUpgradeItem(TurretUpgradeItem.Kind.DAMAGE, new Item.Properties().stacksTo(8)));
    public static final DeferredHolder<Item, TurretUpgradeItem> COOLDOWN_UPGRADE = ITEMS.register("cooldown_upgrade", () ->
            new TurretUpgradeItem(TurretUpgradeItem.Kind.COOLDOWN, new Item.Properties().stacksTo(8)));
    public static final DeferredHolder<Item, TurretUpgradeItem> ENERGY_UPGRADE = ITEMS.register("energy_upgrade", () ->
            new TurretUpgradeItem(TurretUpgradeItem.Kind.ENERGY, new Item.Properties().stacksTo(8)));
    public static final DeferredHolder<Item, TurretUpgradeItem> MOVEMENT_UPGRADE = ITEMS.register("movement_upgrade", () ->
            new TurretUpgradeItem(TurretUpgradeItem.Kind.MOVEMENT, new Item.Properties().stacksTo(8)));
    public static final DeferredHolder<Item, Item> NANODNA = ITEMS.register("nanodna", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> ROLLDOOR_ITEM = ITEMS.register("rolldoor", () ->
            new BlockItem(ROLLDOOR.get(), new Item.Properties()));
    public static final DeferredHolder<Item, DoubleHighBlockItem> SECURE_DOOR_ITEM = ITEMS.register("secure_door", () ->
            new DoubleHighBlockItem(SECURE_DOOR.get(), new Item.Properties()));
    public static final DeferredHolder<Item, DoubleHighBlockItem> PRIVATE_SECURE_DOOR_ITEM = ITEMS.register("private_secure_door", () ->
            new DoubleHighBlockItem(PRIVATE_SECURE_DOOR.get(), new Item.Properties()));
    public static final DeferredHolder<Item, DoubleHighBlockItem> MAG_SECURE_DOOR_ITEM = ITEMS.register("mag_secure_door", () ->
            new DoubleHighBlockItem(MAG_SECURE_DOOR.get(), new Item.Properties()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AlarmBlockEntity>> ALARM_BE =
            BLOCK_ENTITIES.register("alarm", () -> BlockEntityType.Builder.of(AlarmBlockEntity::new, ALARM.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BiometricReaderBlockEntity>> BIOMETRIC_READER_BE =
            BLOCK_ENTITIES.register("biometric_reader", () -> BlockEntityType.Builder.of(BiometricReaderBlockEntity::new, BIOMETRIC_READER.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CardWriterBlockEntity>> CARD_WRITER_BE =
            BLOCK_ENTITIES.register("card_writer", () -> BlockEntityType.Builder.of(CardWriterBlockEntity::new, CARD_WRITER.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DataBlockEntity>> DATA_BLOCK_BE =
            BLOCK_ENTITIES.register("data_block", () -> BlockEntityType.Builder.of(DataBlockEntity::new, DATA_BLOCK.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EntityDetectorBlockEntity>> ENTITY_DETECTOR_BE =
            BLOCK_ENTITIES.register("entity_detector", () -> BlockEntityType.Builder.of(EntityDetectorBlockEntity::new, ENTITY_DETECTOR.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnergyTurretBlockEntity>> ENERGY_TURRET_BE =
            BLOCK_ENTITIES.register("energy_turret", () -> BlockEntityType.Builder.of(EnergyTurretBlockEntity::new, ENERGY_TURRET.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DoorControllerBlockEntity>> DOOR_CONTROLLER_BE =
            BLOCK_ENTITIES.register("door_controller", () -> BlockEntityType.Builder.of(DoorControllerBlockEntity::new, DOOR_CONTROLLER.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MagReaderBlockEntity>> MAG_READER_BE =
            BLOCK_ENTITIES.register("mag_reader", () -> BlockEntityType.Builder.of(MagReaderBlockEntity::new,
                    MAG_READER.get(), MAG_READER_CAMO.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RFIDReaderBlockEntity>> RFID_READER_BE =
            BLOCK_ENTITIES.register("rfid_reader", () -> BlockEntityType.Builder.of(RFIDReaderBlockEntity::new, RFID_READER.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<KeypadBlockEntity>> KEYPAD_BE =
            BLOCK_ENTITIES.register("keypad", () -> BlockEntityType.Builder.of(KeypadBlockEntity::new, KEYPAD.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SecurityTerminalBlockEntity>> SECURITY_TERMINAL_BE =
            BLOCK_ENTITIES.register("security_terminal", () -> BlockEntityType.Builder.of(SecurityTerminalBlockEntity::new, SECURITY_TERMINAL.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RollDoorControllerBlockEntity>> ROLLDOOR_CONTROLLER_BE =
            BLOCK_ENTITIES.register("rolldoor_controller", () -> BlockEntityType.Builder.of(RollDoorControllerBlockEntity::new, ROLLDOOR_CONTROLLER.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<NanoFogTerminalBlockEntity>> NANOFOG_TERMINAL_BE =
            BLOCK_ENTITIES.register("nanofog_terminal", () -> BlockEntityType.Builder.of(NanoFogTerminalBlockEntity::new, NANOFOG_TERMINAL.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<NanoFogBlockEntity>> NANOFOG_BE =
            BLOCK_ENTITIES.register("nanofog", () -> BlockEntityType.Builder.of(NanoFogBlockEntity::new, NANOFOG.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SecureDoorBlockEntity>> SECURE_DOOR_BE =
            BLOCK_ENTITIES.register("secure_door", () -> BlockEntityType.Builder.of(SecureDoorBlockEntity::new,
                    SECURE_DOOR.get(), PRIVATE_SECURE_DOOR.get(), MAG_SECURE_DOOR.get()).build(null));

    public static final DeferredHolder<SoundEvent, SoundEvent> KLAXON_1 = sound("klaxon1");
    public static final DeferredHolder<SoundEvent, SoundEvent> KLAXON_2 = sound("klaxon2");
    public static final DeferredHolder<SoundEvent, SoundEvent> CARD_SWIPE = sound("card_swipe");
    public static final DeferredHolder<SoundEvent, SoundEvent> KEYPAD_PRESS = sound("keypad_press");
    public static final DeferredHolder<SoundEvent, SoundEvent> TURRET_FIRE = sound("turretfire");
    public static final DeferredHolder<EntityType<?>, EntityType<EnergyBoltEntity>> ENERGY_BOLT = ENTITIES.register("energy_bolt", () ->
            EntityType.Builder.of(EnergyBoltEntity::new, MobCategory.MISC).sized(0.5F, 0.5F)
                    .clientTrackingRange(64).updateInterval(1).build("energy_bolt"));

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CREATIVE_TAB = TABS.register("main", () ->
            CreativeModeTab.builder().title(Component.translatable("itemGroup.opensecurity"))
                    .icon(() -> new ItemStack(ALARM.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ALARM.get());
                        output.accept(BIOMETRIC_READER.get());
                        output.accept(CARD_WRITER.get());
                        output.accept(DATA_BLOCK.get());
                        output.accept(DOOR_CONTROLLER.get());
                        output.accept(ENTITY_DETECTOR.get());
                        output.accept(ENERGY_TURRET_ITEM.get());
                        output.accept(KEYPAD.get());
                        output.accept(NANOFOG_TERMINAL.get());
                        output.accept(MAG_READER.get());
                        output.accept(MAG_READER_CAMO_ITEM.get());
                        output.accept(RFID_READER.get());
                        output.accept(SECURITY_TERMINAL.get());
                        output.accept(SECURE_DOOR_ITEM.get());
                        output.accept(PRIVATE_SECURE_DOOR_ITEM.get());
                        output.accept(MAG_SECURE_DOOR_ITEM.get());
                        output.accept(RFID_CARD.get());
                        output.accept(MAG_CARD.get());
                        output.accept(RFID_READER_CARD.get());
                        output.accept(ROLLDOOR_ITEM.get());
                        output.accept(ROLLDOOR_CONTROLLER.get());
                        output.accept(DAMAGE_UPGRADE.get());
                        output.accept(COOLDOWN_UPGRADE.get());
                        output.accept(ENERGY_UPGRADE.get());
                        output.accept(MOVEMENT_UPGRADE.get());
                        output.accept(NANODNA.get());
                    }).build());

    public OpenSecurity(IEventBus modBus) {
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        BLOCK_ENTITIES.register(modBus);
        TABS.register(modBus);
        SOUNDS.register(modBus);
        ENTITIES.register(modBus);
        modBus.addListener(this::registerCapabilities);
        modBus.addListener(OpenSecurity::commonSetup);
        NeoForge.EVENT_BUS.addListener(OpenSecurity::onBlockBreak);
        NeoForge.EVENT_BUS.addListener(OpenSecurity::onBlockPlace);
        NeoForge.EVENT_BUS.addListener(OpenSecurity::onBlockUse);
        NeoForge.EVENT_BUS.addListener(OpenSecurity::onExplosion);
    }

    private static DeferredHolder<Block, SecurityBlock> block(String name, SecurityBlock.Kind kind) {
        DeferredHolder<Block, SecurityBlock> holder = BLOCKS.register(name, () -> new SecurityBlock(kind,
                Block.Properties.of().mapColor(MapColor.METAL).strength(2.5F).sound(SoundType.METAL)));
        ITEMS.register(name, () -> new BlockItem(holder.get(), new Item.Properties()));
        return holder;
    }

    private static DeferredHolder<SoundEvent, SoundEvent> sound(String name) {
        ResourceLocation id = id(name);
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(li.cil.oc.common.Capabilities.EnvironmentCapability(), ALARM_BE.get(), (be, side) -> be);
        event.registerBlockEntity(li.cil.oc.common.Capabilities.EnvironmentCapability(), BIOMETRIC_READER_BE.get(), (be, side) -> be);
        event.registerBlockEntity(li.cil.oc.common.Capabilities.EnvironmentCapability(), CARD_WRITER_BE.get(), (be, side) -> be);
        event.registerBlockEntity(li.cil.oc.common.Capabilities.EnvironmentCapability(), DATA_BLOCK_BE.get(), (be, side) -> be);
        event.registerBlockEntity(li.cil.oc.common.Capabilities.EnvironmentCapability(), ENTITY_DETECTOR_BE.get(), (be, side) -> be);
        event.registerBlockEntity(li.cil.oc.common.Capabilities.EnvironmentCapability(), ENERGY_TURRET_BE.get(), (be, side) -> be);
        event.registerBlockEntity(li.cil.oc.common.Capabilities.EnvironmentCapability(), DOOR_CONTROLLER_BE.get(), (be, side) -> be);
        event.registerBlockEntity(li.cil.oc.common.Capabilities.EnvironmentCapability(), MAG_READER_BE.get(), (be, side) -> be);
        event.registerBlockEntity(li.cil.oc.common.Capabilities.EnvironmentCapability(), RFID_READER_BE.get(), (be, side) -> be);
        event.registerBlockEntity(li.cil.oc.common.Capabilities.EnvironmentCapability(), KEYPAD_BE.get(), (be, side) -> be);
        event.registerBlockEntity(li.cil.oc.common.Capabilities.EnvironmentCapability(), SECURITY_TERMINAL_BE.get(), (be, side) -> be);
        event.registerBlockEntity(li.cil.oc.common.Capabilities.EnvironmentCapability(), ROLLDOOR_CONTROLLER_BE.get(), (be, side) -> be);
        event.registerBlockEntity(li.cil.oc.common.Capabilities.EnvironmentCapability(), NANOFOG_TERMINAL_BE.get(), (be, side) -> be);
        event.registerBlockEntity(li.cil.oc.common.Capabilities.EnvironmentCapability(), SECURE_DOOR_BE.get(), (be, side) -> be);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, CARD_WRITER_BE.get(), (be, side) -> be.inventory());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ENERGY_TURRET_BE.get(), (be, side) -> be.inventory());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, NANOFOG_TERMINAL_BE.get(), (be, side) -> be.inventory());
    }

    private static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getPlayer().getAbilities().instabuild) return;
        if (SecurityTerminalBlockEntity.isProtected(event.getPlayer().level(), event.getPos(), event.getPlayer().getUUID())) {
            event.getPlayer().displayClientMessage(Component.literal("This block is protected"), true);
            event.setCanceled(true);
            return;
        }
        BlockState state = event.getState();
        BlockPos pos = event.getPos();
        if (state.getBlock() instanceof SecureDoorBlock) {
            pos = SecureDoorBlock.lowerPos(pos, state);
            if (event.getLevel().getBlockEntity(pos) instanceof SecureDoorBlockEntity door
                    && !door.canModify(event.getPlayer().getUUID())) {
                event.setCanceled(true);
            }
        } else if (event.getLevel().getBlockEntity(pos) instanceof DoorControllerBlockEntity controller
                && !controller.canModify(event.getPlayer().getUUID())) {
            event.setCanceled(true);
        }
    }

    private static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() instanceof net.minecraft.world.entity.player.Player player
                && !player.getAbilities().instabuild
                && SecurityTerminalBlockEntity.isProtected(player.level(), event.getPos(), player.getUUID())) {
            player.displayClientMessage(Component.literal("This area is protected"), true);
            event.setCanceled(true);
        }
    }

    private static void onBlockUse(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getEntity().getAbilities().instabuild
                && SecurityTerminalBlockEntity.isProtected(event.getLevel(), event.getPos(), event.getEntity().getUUID())) {
            event.getEntity().displayClientMessage(Component.literal("This block is protected"), true);
            event.setCanceled(true);
        }
    }

    private static void onExplosion(ExplosionEvent.Detonate event) {
        event.getAffectedBlocks().removeIf(pos -> SecurityTerminalBlockEntity.isProtected(event.getLevel(), pos, null));
    }

    private static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> li.cil.oc.api.Driver.add(new RFIDReaderCardDriver()));
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
