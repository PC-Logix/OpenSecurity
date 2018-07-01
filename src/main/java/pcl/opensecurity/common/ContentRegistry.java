package pcl.opensecurity.common;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.common.blocks.*;
import pcl.opensecurity.common.drivers.RFIDReaderCardDriver;
import pcl.opensecurity.common.items.*;
import pcl.opensecurity.common.tileentity.*;

import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber
public class ContentRegistry {
    public static CreativeTabs creativeTab = getCreativeTab();

    public static Block alarmBlock = new BlockAlarm();
    public static Block doorController = new BlockDoorController();
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

    // TODO: block and item names normalization
    public static ItemCard itemRFIDCard = new ItemRFIDCard();
    public static ItemCard itemMagCard = new ItemMagCard();

    public static Item rfidReaderCardItem = new ItemRFIDReaderCard();
    public static Item damageUpgradeItem = new ItemDamageUpgrade();
    public static Item cooldownUpgradeItem = new ItemCooldownUpgrade();
    public static Item energyUpgradeItem = new ItemEnergyUpgrade();
    public static Item movementUpgradeItem = new ItemMovementUpgrade();

    public static ItemBlock itemBlockDoorController;  // this holds the unique instance of the ItemBlock corresponding to your block

    private ContentRegistry() {
    }

    public static final Set<Block> blocks = new HashSet<>();

    // Called on mod preInit()
    public static void preInit() {
        registerEvents();
        registerEntities();
    }

    private static void registerEvents() {
        MinecraftForge.EVENT_BUS.register(new OSBreakEvent());
        OpenSecurity.logger.info("Registered Events");
    }

    //Called on mod init()
    public static void init() {
        li.cil.oc.api.Driver.add(new RFIDReaderCardDriver());
        registerRecipes();
    }

    private static void registerEntities() {
        //EntityRegistry.registerModEntity(EntityEnergyBolt.class, "energybolt", 1, OpenSecurity.instance, 128, 1, true);
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
                privateSecureDoor
        );

        GameRegistry.registerTileEntity(TileEntityAlarm.class, Reference.Names.BLOCK_ALARM);
        GameRegistry.registerTileEntity(TileEntityDoorController.class, Reference.Names.BLOCK_DOOR_CONTROLLER);
        GameRegistry.registerTileEntity(TileEntitySecurityTerminal.class, Reference.Names.BLOCK_SECURITY_TERMINAL);
        GameRegistry.registerTileEntity(TileEntityBiometricReader.class, Reference.Names.BLOCK_BIOMETRIC_READER);
        GameRegistry.registerTileEntity(TileEntityDataBlock.class, Reference.Names.BLOCK_DATA);
        GameRegistry.registerTileEntity(TileEntityCardWriter.class, Reference.Names.BLOCK_CARD_WRITER);
        GameRegistry.registerTileEntity(TileEntityMagReader.class, Reference.Names.BLOCK_MAG_READER);
        GameRegistry.registerTileEntity(TileEntityKeypad.class, Reference.Names.BLOCK_KEYPAD);
        GameRegistry.registerTileEntity(TileEntityEntityDetector.class, Reference.Names.BLOCK_ENTITY_DETECTOR);
        GameRegistry.registerTileEntity(TileEntityEnergyTurret.class, Reference.Names.BLOCK_ENERGY_TURRET);
        GameRegistry.registerTileEntity(TileEntityRFIDReader.class, Reference.Names.BLOCK_RFID_READER);
        GameRegistry.registerTileEntity(TileEntitySecureDoor.class, Reference.Names.BLOCK_SECURE_DOOR);
    }

    @SuppressWarnings("ConstantConditions")
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                new ItemBlock(alarmBlock).setRegistryName(alarmBlock.getRegistryName()),
                new ItemBlock(doorController).setRegistryName(doorController.getRegistryName()),
                new ItemBlock(securityTerminal).setRegistryName(securityTerminal.getRegistryName()),
                new ItemBlock(biometricReaderBlock).setRegistryName(biometricReaderBlock.getRegistryName()),
                new ItemBlock(dataBlock).setRegistryName(dataBlock.getRegistryName()),
                new ItemBlock(cardWriter).setRegistryName(cardWriter.getRegistryName()),
                new ItemBlock(magReader).setRegistryName(magReader.getRegistryName()),
                new ItemBlock(keypadBlock).setRegistryName(keypadBlock.getRegistryName()),
                new ItemBlock(entityDetector).setRegistryName(entityDetector.getRegistryName()),
                new ItemBlock(energyTurret).setRegistryName(energyTurret.getRegistryName()),
                new ItemBlock(rfidReader).setRegistryName(rfidReader.getRegistryName()),
                new ItemSecureDoor(secureDoor).setRegistryName(secureDoor.getRegistryName()),
                new ItemSecurePrivateDoor(privateSecureDoor).setRegistryName(privateSecureDoor.getRegistryName())
        );

        event.getRegistry().registerAll(
                itemRFIDCard,
                itemMagCard,
                rfidReaderCardItem,
                damageUpgradeItem,
                cooldownUpgradeItem,
                energyUpgradeItem,
                movementUpgradeItem
        );
    }

    private static void registerRecipes() {
//        // Vanilla Minecraft blocks/items
//        String iron = "ingotIron";
//        String diamond = "gemDiamond";
//        String redstone = "dustRedstone";
//        String obsidian = "obsidian";
//        String glass = "blockGlassColorless";
//        String stone = "stone";
//        ItemStack stone_button = new ItemStack(Blocks.STONE_BUTTON);
//        ItemStack paper = new ItemStack(Items.PAPER);
//        ItemStack noteblock = new ItemStack(Blocks.NOTEBLOCK);
//        ItemStack door = new ItemStack(Items.IRON_DOOR);
//        ItemStack gunpowder = new ItemStack(Items.GUNPOWDER);
//        ItemStack arrow = new ItemStack(Items.ARROW);
//        ItemStack piston = new ItemStack(Item.getItemFromBlock(Blocks.PISTON));
//        ItemStack water = new ItemStack(Items.WATER_BUCKET);
//
//        // Opencomputers blocks/items
//        String t2microchip = "oc:circuitChip2";
//        String t1microchip = "oc:circuitChip1";
//        String t1ram = "oc:ram1";
//        String pcb = "oc:materialCircuitBoardPrinted";
//        String controlunit = "oc:materialCU";
//        String wlancard = "oc:wlanCard";
//        String cardbase = "oc:materialCard";
//        String cable = "oc:cable";
//        String transistor = "oc:materialTransistor";
//        String numpad = "oc:materialNumPad";
//        String batteryUpgrade = "oc:batteryUpgrade1";
//        String oc_relay = "oc:relay";
//        ItemStack floppy = li.cil.oc.api.Items.get("floppy").createItemStack(1);
//        ItemStack datacard;
//        if (li.cil.oc.api.Items.get("dataCard").createItemStack(1) != null) {
//            datacard = li.cil.oc.api.Items.get("dataCard").createItemStack(1);
//        } else {
//            datacard = li.cil.oc.api.Items.get("dataCard1").createItemStack(1);
//        }
//
//
//        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(rfidReaderCardItem, 1),
//                "MRM",
//                " N ",
//                "BC ",
//                'M', t2microchip, 'R', t1ram, 'N', wlancard, 'B', cardbase, 'C', controlunit));
//
//
//        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(entityDetector, 1),
//                "MRM",
//                "   ",
//                "BC ",
//                'M', t2microchip, 'R', t1ram, 'B', cardbase, 'C', controlunit));
//
//
//        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(rfidReader, 1),
//                " R ",
//                "PFT",
//                " C ",
//                'F', rfidReaderCardItem, 'P', pcb, 'R', redstone, 'C', cable, 'T', t2microchip));
//
//
//        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(dataBlock, 1),
//                " D ",
//                "PFT",
//                " C ",
//                'D', datacard, 'P', pcb, 'R', redstone, 'C', cable, 'T', t2microchip));
//
//        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(alarmBlock, 1),
//                " R ",
//                "PNC",
//                " T ",
//                'N', noteblock, 'P', pcb, 'R', redstone, 'C', cable, 'T', t2microchip));
//
//        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(cardWriter, 1),
//                "TRT",
//                "SUS",
//                "PC ",
//                'P', pcb, 'C', cable, 'T', t2microchip, 'S', transistor, 'U', controlunit, 'R', t1ram));
//
//        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(magReader, 1),
//                "T T",
//                "S S",
//                "PC ",
//                'P', pcb, 'C', cable, 'T', t2microchip, 'S', transistor));
//
//        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemRFIDCard, 6),
//                "P P",
//                " S ",
//                "PTP",
//                'P', paper, 'S', transistor, 'T', t1microchip));
//
//        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemMagCard, 6),
//                "P P",
//                " S ",
//                "P P",
//                'P', paper, 'S', transistor));
//
//        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(secureDoor, 1),
//                "TGT",
//                "ODO",
//                "SOS",
//                'G', glass, 'D', door, 'S', transistor, 'T', t2microchip, 'O', obsidian));
//
//        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(privateSecureDoor, 1),
//                "TOT",
//                "ODO",
//                "SOS",
//                'D', door, 'S', transistor, 'T', t2microchip, 'O', obsidian));
//
//        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(doorController, 1),
//                "TOT",
//                "OCO",
//                "SBS",
//                'B', cable, 'C', controlunit, 'S', transistor, 'T', t2microchip, 'O', obsidian));
//
///*		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(SwitchableHubBlock, 1),
//				"TBT", 
//				"BSB", 
//				"RBR", 
//				'B', cable, 'S', oc_relay, 'R', transistor, 'T', t2microchip, 'O', obsidian));
//		
//		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(KVMBlock, 1),
//				" B ", 
//				"BSB", 
//				"RBR", 
//				'B', cable,  'S', oc_relay, 'R', transistor, 'T', t2microchip, 'O', obsidian));*/
//
//        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(energyTurret, 1),
//                "ABA",
//                "BCB",
//                "ABA",
//                'A', iron, 'B', t2microchip, 'C', diamond));
//
//        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(damageUpgradeItem, 1),
//                "A A",
//                " G ",
//                "A A",
//                'A', arrow, 'G', gunpowder));
//
//        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(movementUpgradeItem, 1),
//                "R R",
//                " P ",
//                "R R",
//                'P', piston, 'R', redstone));
//
//        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(cooldownUpgradeItem, 1),
//                "R R",
//                " W ",
//                "R R",
//                'W', water, 'R', redstone));
//
//        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(energyUpgradeItem, 1),
//                "R R",
//                " B ",
//                "R R",
//                'B', batteryUpgrade, 'R', redstone));
//
//        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(keypadBlock, 1),
//                "TIT",
//                "INI",
//                "ICI",
//                'T', transistor, 'N', numpad, 'C', t1microchip, 'I', iron));
//
//        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(biometricReaderBlock, 1),
//                "SIS",
//                "STS",
//                "SCS",
//                'T', transistor, 'C', t1microchip, 'I', iron, 'S', stone));
//
//        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(securityTerminal, 1),
//                "SIS",
//                "STS",
//                "SCS",
//                'T', controlunit, 'C', t2microchip, 'I', iron, 'S', stone));

        OpenSecurity.logger.info("Registered Recipes");
    }

    private static CreativeTabs getCreativeTab() {
        return new CreativeTabs("tabOpenSecurity") {
            @SideOnly(Side.CLIENT)
            public ItemStack getTabIconItem() {
                return new ItemStack(Item.getItemFromBlock(dataBlock));
            }

            @SideOnly(Side.CLIENT)
            public String getTranslatedTabLabel() {
                return I18n.translateToLocal("itemGroup.OpenSecurity.tabOpenSecurity");
            }
        };
    }
}
