package pcl.opensecurity.common;

import li.cil.oc.api.driver.DriverItem;
import li.cil.oc.api.driver.EnvironmentProvider;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.common.blocks.*;
import pcl.opensecurity.common.drivers.*;
import pcl.opensecurity.common.entity.EntityEnergyBolt;
import pcl.opensecurity.common.entity.EntityNanoFogSwarm;
import pcl.opensecurity.common.items.*;
import pcl.opensecurity.common.tileentity.*;

import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber
public class ContentRegistry {
    public static CreativeTabs creativeTab = getCreativeTab();

    public static Block alarmBlock = new BlockAlarm();
    public static Block securityTerminal = new BlockSecurityTerminal();
    public static Block biometricReaderBlock = new BlockBiometricReader();
    public static Block dataBlock = new BlockData();
    public static Block cardWriter = new BlockCardWriter();
    public static Block magReader = new BlockMagReader();
    public static Block keypadBlock = new BlockKeypad();
    public static Block entityDetector = new BlockEntityDetector();
    public static Block energyTurret = new BlockEnergyTurret();
    public static Block rfidReader = new BlockRFIDReader();
    public static Block secureDoor = new BlockSecureDoor();
    public static Block privateSecureDoor = new BlockSecurePrivateDoor();
    public static Block nanoFogTerminal = new BlockNanoFogTerminal();
    public static Block rolldoor = new BlockRolldoor();
    public static Block rolldoorElement = new BlockRolldoorElement();

    public static BlockDoorController doorController = new BlockDoorController();
    public static BlockRolldoorController rolldoorController = new BlockRolldoorController();
    public static BlockNanoFog nanoFog = new BlockNanoFog();

    public static Item doorControllerItem;
    public static Item entityDetectorItem;
    public static Item rfidReaderItem;
    public static Item alarmItem;


    // TODO: block and item names normalization
    public static ItemRFIDCard itemRFIDCard = new ItemRFIDCard();
    public static ItemMagCard itemMagCard = new ItemMagCard();

    public static Item secureDoorItem = new ItemSecureDoor();
    public static Item securePrivateDoorItem = new ItemSecurePrivateDoor();
    public static Item rfidReaderCardItem = new ItemRFIDReaderCard();
    public static Item damageUpgradeItem = new ItemDamageUpgrade();
    public static Item cooldownUpgradeItem = new ItemCooldownUpgrade();
    public static Item energyUpgradeItem = new ItemEnergyUpgrade();
    public static Item movementUpgradeItem = new ItemMovementUpgrade();

    public static Item nanoDNAItem = new ItemNanoDNA();

    public ContentRegistry() {
    }

    public static final Set<Block> blocks = new HashSet<>();

    // Called on mod preInit()
    public static void preInit() {
        registerEvents();
    }

    private static void registerEvents() {
        MinecraftForge.EVENT_BUS.register(new OSBreakEvent());

        if(OpenSecurity.debug)
            OpenSecurity.logger.info("Registered Events");
    }

    //Called on mod init()
    public static void init() {
        li.cil.oc.api.Driver.add((EnvironmentProvider) DoorControllerDriver.driver);
        li.cil.oc.api.Driver.add((DriverItem) DoorControllerDriver.driver);

        li.cil.oc.api.Driver.add((EnvironmentProvider) EntityDetectorDriver.driver);
        li.cil.oc.api.Driver.add((DriverItem) EntityDetectorDriver.driver);

        li.cil.oc.api.Driver.add((EnvironmentProvider) AlarmDriver.driver);
        li.cil.oc.api.Driver.add((DriverItem) AlarmDriver.driver);

        //block/upgrade
        li.cil.oc.api.Driver.add((EnvironmentProvider) RFIDReaderDriver.driver);
        li.cil.oc.api.Driver.add((DriverItem) RFIDReaderDriver.driver);

        //card
        li.cil.oc.api.Driver.add(RFIDReaderCardDriver.driver);
    }


    @SubscribeEvent
    public void registerEntities(RegistryEvent.Register<EntityEntry> event){
        EntityRegistry.registerModEntity(new ResourceLocation(OpenSecurity.MODID, EntityEnergyBolt.NAME), EntityEnergyBolt.class, EntityEnergyBolt.NAME, 0, OpenSecurity.instance, 80, 3, true);
        EntityRegistry.registerModEntity(new ResourceLocation(OpenSecurity.MODID, EntityNanoFogSwarm.NAME), EntityNanoFogSwarm.class, EntityNanoFogSwarm.NAME, 1, OpenSecurity.instance, 80, 3, true);
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(
                alarmBlock,
                doorController,
                securityTerminal,
                biometricReaderBlock,
                dataBlock,
                cardWriter,
                magReader,
                keypadBlock,
                entityDetector,
                energyTurret,
                rfidReader,
                secureDoor,
                privateSecureDoor,
                nanoFogTerminal,
                nanoFog,
                rolldoor,
                rolldoorController,
                rolldoorElement
        );

        registerTileEntity(TileEntityAlarm.class, BlockAlarm.NAME);
        registerTileEntity(TileEntityDoorController.class, BlockDoorController.NAME);
        registerTileEntity(TileEntitySecurityTerminal.class, BlockSecurityTerminal.NAME);
        registerTileEntity(TileEntityBiometricReader.class, BlockBiometricReader.NAME);
        registerTileEntity(TileEntityDataBlock.class, BlockData.NAME);
        registerTileEntity(TileEntityCardWriter.class, BlockCardWriter.NAME);
        registerTileEntity(TileEntityMagReader.class, BlockMagReader.NAME);
        registerTileEntity(TileEntityKeypad.class, BlockKeypad.NAME);
        registerTileEntity(TileEntityEntityDetector.class, BlockEntityDetector.NAME);
        registerTileEntity(TileEntityEnergyTurret.class, BlockEnergyTurret.NAME);
        registerTileEntity(TileEntityRFIDReader.class, BlockRFIDReader.NAME);
        registerTileEntity(TileEntitySecureDoor.class, BlockSecureDoor.NAME);
        registerTileEntity(TileEntityNanoFogTerminal.class, BlockNanoFogTerminal.NAME);
        registerTileEntity(TileEntityNanoFog.class, BlockNanoFog.NAME);
        registerTileEntity(TileEntityRolldoor.class, BlockRolldoor.NAME);
        registerTileEntity(TileEntityRolldoorController.class, BlockRolldoorController.NAME);
        registerTileEntity(TileEntityRolldoorElement.class, BlockRolldoorElement.NAME);
    }

    private static void registerTileEntity(Class<? extends TileEntity> tileEntityClass, String key) {
        // For better readability
        GameRegistry.registerTileEntity(tileEntityClass, new ResourceLocation(OpenSecurity.MODID, key));
    }

    @SuppressWarnings("ConstantConditions")
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        alarmItem = new ItemBlock(alarmBlock).setRegistryName(alarmBlock.getRegistryName());
        doorControllerItem = new ItemBlock(doorController).setRegistryName(doorController.getRegistryName());
        entityDetectorItem = new ItemBlock(entityDetector).setRegistryName(entityDetector.getRegistryName());
        rfidReaderItem = new ItemBlock(rfidReader).setRegistryName(rfidReader.getRegistryName());

        event.getRegistry().registerAll(
                doorControllerItem,
                entityDetectorItem,
                rfidReaderItem,
                alarmItem,
                new ItemBlock(biometricReaderBlock).setRegistryName(biometricReaderBlock.getRegistryName()),
                new ItemBlock(cardWriter).setRegistryName(cardWriter.getRegistryName()),
                new ItemBlock(dataBlock).setRegistryName(dataBlock.getRegistryName()),
                new ItemBlock(energyTurret).setRegistryName(energyTurret.getRegistryName()),
                new ItemBlock(keypadBlock).setRegistryName(keypadBlock.getRegistryName()),
                new ItemBlock(magReader).setRegistryName(magReader.getRegistryName()),
                new ItemBlock(nanoFog).setRegistryName(nanoFog.getRegistryName()),
                new ItemBlock(nanoFogTerminal).setRegistryName(nanoFogTerminal.getRegistryName()),
                new ItemBlock(securityTerminal).setRegistryName(securityTerminal.getRegistryName()),
                new ItemBlock(rolldoor).setRegistryName(rolldoor.getRegistryName()),
                new ItemBlock(rolldoorController).setRegistryName(rolldoorController.getRegistryName()),
                new ItemBlock(rolldoorElement).setRegistryName(rolldoorElement.getRegistryName())
        );

        event.getRegistry().registerAll(
                secureDoorItem,
                securePrivateDoorItem,
                itemRFIDCard,
                itemMagCard,
                rfidReaderCardItem,
                damageUpgradeItem,
                cooldownUpgradeItem,
                energyUpgradeItem,
                movementUpgradeItem,
                nanoDNAItem
        );
    }


    private static CreativeTabs getCreativeTab() {
        return new CreativeTabs("tabOpenSecurity") {
            public ItemStack getTabIconItem() {
                return new ItemStack(Item.getItemFromBlock(dataBlock));
            }

            public String getTranslatedTabLabel() {
                return new TextComponentTranslation("itemGroup.OpenSecurity.tabOpenSecurity").getUnformattedText();
            }
        };
    }
}
