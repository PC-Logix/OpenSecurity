package pcl.opensecurity.common;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.ShapedOreRecipe;
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

    public static Item secureDoorItem = new ItemSecureDoor();
    public static Item securePrivateDoorItem = new ItemSecurePrivateDoor();
    public static Item rfidReaderCardItem = new ItemRFIDReaderCard();
    public static Item damageUpgradeItem = new ItemDamageUpgrade();
    public static Item cooldownUpgradeItem = new ItemCooldownUpgrade();
    public static Item energyUpgradeItem = new ItemEnergyUpgrade();
    public static Item movementUpgradeItem = new ItemMovementUpgrade();

    public ContentRegistry() {
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

        registerTileEntity(TileEntityAlarm.class, Reference.Names.BLOCK_ALARM);
        registerTileEntity(TileEntityDoorController.class, Reference.Names.BLOCK_DOOR_CONTROLLER);
        registerTileEntity(TileEntitySecurityTerminal.class, Reference.Names.BLOCK_SECURITY_TERMINAL);
        registerTileEntity(TileEntityBiometricReader.class, Reference.Names.BLOCK_BIOMETRIC_READER);
        registerTileEntity(TileEntityDataBlock.class, Reference.Names.BLOCK_DATA);
        registerTileEntity(TileEntityCardWriter.class, Reference.Names.BLOCK_CARD_WRITER);
        registerTileEntity(TileEntityMagReader.class, Reference.Names.BLOCK_MAG_READER);
        registerTileEntity(TileEntityKeypad.class, Reference.Names.BLOCK_KEYPAD);
        registerTileEntity(TileEntityEntityDetector.class, Reference.Names.BLOCK_ENTITY_DETECTOR);
        registerTileEntity(TileEntityEnergyTurret.class, Reference.Names.BLOCK_ENERGY_TURRET);
        registerTileEntity(TileEntityRFIDReader.class, Reference.Names.BLOCK_RFID_READER);
        registerTileEntity(TileEntitySecureDoor.class, Reference.Names.BLOCK_SECURE_DOOR);
    }

    private static void registerTileEntity(Class<? extends TileEntity> tileEntityClass, String key) {
        // For better readability
        GameRegistry.registerTileEntity(tileEntityClass, new ResourceLocation(OpenSecurity.MODID, key));
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
                new ItemBlock(rfidReader).setRegistryName(rfidReader.getRegistryName())
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
                movementUpgradeItem
        );
    }

	@SubscribeEvent
	public void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        // Vanilla Minecraft blocks/items
        String iron = "ingotIron";
        String diamond = "gemDiamond";
        String redstone = "dustRedstone";
        String obsidian = "obsidian";
        String glass = "blockGlassColorless";
        String stone = "stone";
        ItemStack stone_button = new ItemStack(Blocks.STONE_BUTTON);
        ItemStack paper = new ItemStack(Items.PAPER);
        ItemStack noteblock = new ItemStack(Blocks.NOTEBLOCK);
        ItemStack door = new ItemStack(Items.IRON_DOOR);
        ItemStack gunpowder = new ItemStack(Items.GUNPOWDER);
        ItemStack arrow = new ItemStack(Items.ARROW);
        ItemStack piston = new ItemStack(Item.getItemFromBlock(Blocks.PISTON));
        ItemStack water = new ItemStack(Items.WATER_BUCKET);

        // Opencomputers blocks/items
        ItemStack t2microchip = li.cil.oc.api.Items.get("chip2").createItemStack(1);
        ItemStack t1microchip = li.cil.oc.api.Items.get("chip1").createItemStack(1);
        ItemStack t1ram = li.cil.oc.api.Items.get("ram1").createItemStack(1);
        ItemStack pcb = li.cil.oc.api.Items.get("circuitboard").createItemStack(1);
        ItemStack controlunit = li.cil.oc.api.Items.get("cu").createItemStack(1);
        ItemStack wlancard = li.cil.oc.api.Items.get("wlancard1").createItemStack(1);
        ItemStack cardbase = li.cil.oc.api.Items.get("card").createItemStack(1);
        ItemStack cable = li.cil.oc.api.Items.get("cable").createItemStack(1);
        ItemStack transistor = li.cil.oc.api.Items.get("transistor").createItemStack(1);
        ItemStack numpad = li.cil.oc.api.Items.get("numpad").createItemStack(1);
        ItemStack batteryUpgrade = li.cil.oc.api.Items.get("batteryupgrade1").createItemStack(1);
        ItemStack oc_relay = li.cil.oc.api.Items.get("relay").createItemStack(1);
        ItemStack floppy = li.cil.oc.api.Items.get("floppy").createItemStack(1);
        ItemStack datacard = li.cil.oc.api.Items.get("datacard1").createItemStack(1);


        event.getRegistry().register(new ShapedOreRecipe(rfidReaderCardItem.getRegistryName(), new ItemStack(rfidReaderCardItem, 1),
                "MRM",
                " N ",
                "BC ",
                'M', t2microchip, 'R', t1ram, 'N', wlancard, 'B', cardbase, 'C', controlunit).setRegistryName(OpenSecurity.MODID,rfidReaderCardItem.getUnlocalizedName()));

        event.getRegistry().register(new ShapedOreRecipe(entityDetector.getRegistryName(), new ItemStack(entityDetector, 1),
                "MRM",
                "   ",
                "BC ",
                'M', t2microchip, 'R', t1ram, 'B', cardbase, 'C', controlunit).setRegistryName(OpenSecurity.MODID,entityDetector.getUnlocalizedName()));


        event.getRegistry().register(new ShapedOreRecipe(rfidReader.getRegistryName(), new ItemStack(rfidReader, 1),
                " R ",
                "PFT",
                " C ",
                'F', rfidReaderCardItem, 'P', pcb, 'R', redstone, 'C', cable, 'T', t2microchip).setRegistryName(OpenSecurity.MODID,rfidReader.getUnlocalizedName()));


        event.getRegistry().register(new ShapedOreRecipe(dataBlock.getRegistryName(), new ItemStack(dataBlock, 1),
                " D ",
                "PRT",
                " C ",
                'D', datacard, 'P', pcb, 'R', redstone, 'C', cable, 'T', t2microchip).setRegistryName(OpenSecurity.MODID,dataBlock.getUnlocalizedName()));

        event.getRegistry().register(new ShapedOreRecipe(alarmBlock.getRegistryName(), new ItemStack(alarmBlock, 1),
                " R ",
                "PNC",
                " T ",
                'N', noteblock, 'P', pcb, 'R', redstone, 'C', cable, 'T', t2microchip).setRegistryName(OpenSecurity.MODID,alarmBlock.getUnlocalizedName()));

        event.getRegistry().register(new ShapedOreRecipe(cardWriter.getRegistryName(), new ItemStack(cardWriter, 1),
                "TRT",
                "SUS",
                "PC ",
                'P', pcb, 'C', cable, 'T', t2microchip, 'S', transistor, 'U', controlunit, 'R', t1ram).setRegistryName(OpenSecurity.MODID,cardWriter.getUnlocalizedName()));

        event.getRegistry().register(new ShapedOreRecipe(magReader.getRegistryName(), new ItemStack(magReader, 1),
                "T T",
                "S S",
                "PC ",
                'P', pcb, 'C', cable, 'T', t2microchip, 'S', transistor).setRegistryName(OpenSecurity.MODID,magReader.getUnlocalizedName()));

        event.getRegistry().register(new ShapedOreRecipe(itemRFIDCard.getRegistryName(), new ItemStack(itemRFIDCard, 6),
                "P P",
                " S ",
                "PTP",
                'P', paper, 'S', transistor, 'T', t1microchip).setRegistryName(OpenSecurity.MODID,itemRFIDCard.getUnlocalizedName()));

        event.getRegistry().register(new ShapedOreRecipe(itemMagCard.getRegistryName(), new ItemStack(itemMagCard, 6),
                "P P",
                " S ",
                "P P",
                'P', paper, 'S', transistor).setRegistryName(OpenSecurity.MODID,itemMagCard.getUnlocalizedName()));

        event.getRegistry().register(new ShapedOreRecipe(secureDoor.getRegistryName(), new ItemStack(secureDoor, 1),
                "TGT",
                "ODO",
                "SOS",
                'G', glass, 'D', door, 'S', transistor, 'T', t2microchip, 'O', obsidian).setRegistryName(OpenSecurity.MODID,secureDoor.getUnlocalizedName()));

        event.getRegistry().register(new ShapedOreRecipe(privateSecureDoor.getRegistryName(), new ItemStack(privateSecureDoor, 1),
                "TOT",
                "ODO",
                "SOS",
                'D', door, 'S', transistor, 'T', t2microchip, 'O', obsidian).setRegistryName(OpenSecurity.MODID,privateSecureDoor.getUnlocalizedName()));

        event.getRegistry().register(new ShapedOreRecipe(doorController.getRegistryName(), new ItemStack(doorController, 1),
                "TOT",
                "OCO",
                "SBS",
                'B', cable, 'C', controlunit, 'S', transistor, 'T', t2microchip, 'O', obsidian).setRegistryName(OpenSecurity.MODID,doorController.getUnlocalizedName()));

/*		event.getRegistry().register(new ShapedOreRecipe(new ItemStack(SwitchableHubBlock, 1),
				"TBT", 
				"BSB", 
				"RBR", 
				'B', cable, 'S', oc_relay, 'R', transistor, 'T', t2microchip, 'O', obsidian));
		
		event.getRegistry().register(new ShapedOreRecipe(new ItemStack(KVMBlock, 1),
				" B ", 
				"BSB", 
				"RBR", 
				'B', cable,  'S', oc_relay, 'R', transistor, 'T', t2microchip, 'O', obsidian));*/

        event.getRegistry().register(new ShapedOreRecipe(energyTurret.getRegistryName(), new ItemStack(energyTurret, 1),
                "ABA",
                "BCB",
                "ABA",
                'A', iron, 'B', t2microchip, 'C', diamond).setRegistryName(OpenSecurity.MODID,energyTurret.getUnlocalizedName()));

        event.getRegistry().register(new ShapedOreRecipe(damageUpgradeItem.getRegistryName(), new ItemStack(damageUpgradeItem, 1),
                "A A",
                " G ",
                "A A",
                'A', arrow, 'G', gunpowder).setRegistryName(OpenSecurity.MODID,damageUpgradeItem.getUnlocalizedName()));

        event.getRegistry().register(new ShapedOreRecipe(movementUpgradeItem.getRegistryName(), new ItemStack(movementUpgradeItem, 1),
                "R R",
                " P ",
                "R R",
                'P', piston, 'R', redstone).setRegistryName(OpenSecurity.MODID,movementUpgradeItem.getUnlocalizedName()));

        event.getRegistry().register(new ShapedOreRecipe(cooldownUpgradeItem.getRegistryName(), new ItemStack(cooldownUpgradeItem, 1),
                "R R",
                " W ",
                "R R",
                'W', water, 'R', redstone).setRegistryName(OpenSecurity.MODID,cooldownUpgradeItem.getUnlocalizedName()));

        event.getRegistry().register(new ShapedOreRecipe(energyUpgradeItem.getRegistryName(), new ItemStack(energyUpgradeItem, 1),
                "R R",
                " B ",
                "R R",
                'B', batteryUpgrade, 'R', redstone).setRegistryName(OpenSecurity.MODID,energyUpgradeItem.getUnlocalizedName()));

        event.getRegistry().register(new ShapedOreRecipe(keypadBlock.getRegistryName(), new ItemStack(keypadBlock, 1),
                "TIT",
                "INI",
                "ICI",
                'T', transistor, 'N', numpad, 'C', t1microchip, 'I', iron).setRegistryName(OpenSecurity.MODID,keypadBlock.getUnlocalizedName()));

        event.getRegistry().register(new ShapedOreRecipe(biometricReaderBlock.getRegistryName(), new ItemStack(biometricReaderBlock, 1),
                "SIS",
                "STS",
                "SCS",
                'T', transistor, 'C', t1microchip, 'I', iron, 'S', stone).setRegistryName(OpenSecurity.MODID,biometricReaderBlock.getUnlocalizedName()));

        event.getRegistry().register(new ShapedOreRecipe(securityTerminal.getRegistryName(), new ItemStack(securityTerminal, 1),
                "SIS",
                "STS",
                "SCS",
                'T', controlunit, 'C', t2microchip, 'I', iron, 'S', stone).setRegistryName(OpenSecurity.MODID,securityTerminal.getUnlocalizedName()));

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
