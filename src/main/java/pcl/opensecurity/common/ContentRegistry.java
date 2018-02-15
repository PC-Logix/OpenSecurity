package pcl.opensecurity.common;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.ShapedOreRecipe;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.common.blocks.*;
import pcl.opensecurity.common.drivers.RFIDReaderCardDriver;
import pcl.opensecurity.common.entity.EntityEnergyBolt;
import pcl.opensecurity.common.items.ItemCard;
import pcl.opensecurity.common.items.ItemCooldownUpgrade;
import pcl.opensecurity.common.items.ItemDamageUpgrade;
import pcl.opensecurity.common.items.ItemEnergyUpgrade;
import pcl.opensecurity.common.items.ItemMagCard;
import pcl.opensecurity.common.items.ItemMovementUpgrade;
import pcl.opensecurity.common.items.ItemRFIDCard;
import pcl.opensecurity.common.items.ItemRFIDReaderCard;
import pcl.opensecurity.common.items.ItemSecureDoor;
import pcl.opensecurity.common.items.ItemSecurePrivateDoor;
import pcl.opensecurity.common.tileentity.*;

public class ContentRegistry {
    public static CreativeTabs creativeTab;
    public static Block alarmBlock;
    public static Block biometricReaderBlock;
    public static Block dataBlock;
    public static Block cardWriter;
    public static Block magReader;
    public static Block secureDoor;
    public static Block privateSecureDoor;
    public static Block keypadBlock;
    public static Block energyTurret;
    public static Block rfidReader;
    public static Block entityDetector;
    public static Block securityTerminal;

    public static ItemCard itemRFIDCard;
    public static ItemCard itemMagCard;

    public static Item damageUpgradeItem;
    public static Item movementUpgradeItem;
    public static Item cooldownUpgradeItem;
    public static Item energyUpgradeItem;
    public static Item rfidReaderCardItem;

    public static BlockDoorController doorController;  // this holds the unique instance of your block
    public static ItemBlock itemBlockDoorController;  // this holds the unique instance of the ItemBlock corresponding to your block

    private ContentRegistry() {}

    public static final Set<Block> blocks = new HashSet<>();

    // Called on mod preInit()
    public static void preInit() {
    	registerEvents();
        registerTabs();
        registerBlocks();
        registerItems();
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
        EntityRegistry.registerModEntity(EntityEnergyBolt.class, "energybolt", 1, OpenSecurity.instance, 128, 1, true);
    }

    private static void registerItems() {
        itemRFIDCard = new ItemRFIDCard();
        GameRegistry.register( itemRFIDCard.setRegistryName( new ResourceLocation( OpenSecurity.MODID, "rfidcard" ) ) );
        itemRFIDCard.setCreativeTab(creativeTab);

        itemMagCard = new ItemMagCard();
        GameRegistry.register( itemMagCard.setRegistryName( new ResourceLocation( OpenSecurity.MODID, "magcard" ) ) );
        itemMagCard.setCreativeTab(creativeTab);

        damageUpgradeItem = new ItemDamageUpgrade();
        GameRegistry.register(damageUpgradeItem.setRegistryName( new ResourceLocation( OpenSecurity.MODID, "damageUpgrade" ) ) );
        damageUpgradeItem.setCreativeTab(creativeTab);

        cooldownUpgradeItem = new ItemCooldownUpgrade();
        GameRegistry.register(cooldownUpgradeItem.setRegistryName( new ResourceLocation( OpenSecurity.MODID, "cooldownUpgrade" ) ) );
        cooldownUpgradeItem.setCreativeTab(creativeTab);

        energyUpgradeItem = new ItemEnergyUpgrade();
        GameRegistry.register(energyUpgradeItem.setRegistryName( new ResourceLocation( OpenSecurity.MODID, "energyUpgrade" ) ) );
        energyUpgradeItem.setCreativeTab(creativeTab);

        movementUpgradeItem = new ItemMovementUpgrade();
        GameRegistry.register(movementUpgradeItem.setRegistryName( new ResourceLocation( OpenSecurity.MODID, "movementUpgrade" ) ) );
        movementUpgradeItem.setCreativeTab(creativeTab);

        rfidReaderCardItem = new ItemRFIDReaderCard();
        GameRegistry.register(rfidReaderCardItem.setRegistryName( new ResourceLocation( OpenSecurity.MODID, "rfidReaderCard" ) ) );
        rfidReaderCardItem.setCreativeTab(creativeTab);
    }

    @SuppressWarnings("deprecation")
    private static void registerBlocks() {
        alarmBlock = new BlockAlarm(Material.IRON);
        registerBlock(alarmBlock);
        alarmBlock.setCreativeTab(creativeTab);
        GameRegistry.registerTileEntity(TileEntityAlarm.class, "os_alarm");

        biometricReaderBlock = new BlockBiometricReader(Material.IRON);
        registerBlock(biometricReaderBlock);
        biometricReaderBlock.setCreativeTab(creativeTab);
        GameRegistry.registerTileEntity(TileEntityBiometricReader.class, "biometric_reader");

        dataBlock = new BlockData(Material.IRON);
        registerBlock(dataBlock);
        dataBlock.setCreativeTab(creativeTab);
        GameRegistry.registerTileEntity(TileEntityDataBlock.class, "data_block");

        cardWriter = new BlockCardWriter(Material.IRON);
        registerBlock(cardWriter);
        cardWriter.setCreativeTab(creativeTab);
        GameRegistry.registerTileEntity(TileEntityCardWriter.class, "card_writer");

        magReader = new BlockMagReader(Material.IRON);
        registerBlock(magReader);
        magReader.setCreativeTab(creativeTab);
        GameRegistry.registerTileEntity(TileEntityMagReader.class, "mag_reader");

        keypadBlock = new BlockKeypad(Material.IRON);
        registerBlock(keypadBlock);
        keypadBlock.setCreativeTab(creativeTab);
        GameRegistry.registerTileEntity(TileEntityKeypad.class, "keypad");

        entityDetector = new BlockEntityDetector(Material.IRON);
        registerBlock(entityDetector);
        entityDetector.setCreativeTab(creativeTab);
        GameRegistry.registerTileEntity(TileEntityEntityDetector.class, "entity_detector");

        energyTurret = new BlockEnergyTurret(Material.IRON);
        registerBlock(energyTurret);
        energyTurret.setCreativeTab(creativeTab);
        GameRegistry.registerTileEntity(TileEntityEnergyTurret.class, "energyTurret");

        rfidReader = new BlockRFIDReader(Material.IRON);
        registerBlock(rfidReader);
        rfidReader.setCreativeTab(creativeTab);
        GameRegistry.registerTileEntity(TileEntityRFIDReader.class, "rfidReader");

        secureDoor = new BlockSecureDoor(Material.IRON);
        GameRegistry.registerBlock(secureDoor, ItemSecureDoor.class, "secure_door");
        secureDoor.setCreativeTab(creativeTab);
        privateSecureDoor = new BlockSecurePrivateDoor(Material.IRON);
        GameRegistry.registerBlock(privateSecureDoor, ItemSecurePrivateDoor.class, "secure_private_door");
        privateSecureDoor.setCreativeTab(creativeTab);

        GameRegistry.registerTileEntity(TileEntitySecureDoor.class, "secure_door");

        doorController = (BlockDoorController)(new BlockDoorController(Material.IRON).setUnlocalizedName("door_controller"));
        doorController.setRegistryName("door_controller");
        GameRegistry.register(doorController);

        doorController.setCreativeTab(creativeTab);

        securityTerminal = new BlockSecurityTerminal(Material.IRON);
        registerBlock(securityTerminal);
        securityTerminal.setCreativeTab(creativeTab);
        GameRegistry.registerTileEntity(TileEntitySecurityTerminal.class, "security_terminal");

        // We also need to create and register an ItemBlock for this block otherwise it won't appear in the inventory
        itemBlockDoorController = new ItemBlock(doorController);
        itemBlockDoorController.setRegistryName(doorController.getRegistryName());
        GameRegistry.register(itemBlockDoorController);
        itemBlockDoorController.setCreativeTab(creativeTab);

        GameRegistry.registerTileEntity(TileEntityDoorController.class, "door_controller");
    }

    private static void registerRecipes() {
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
        String t2microchip = "oc:circuitChip2";
        String t1microchip = "oc:circuitChip1";
        String t1ram = "oc:ram1";
        String pcb = "oc:materialCircuitBoardPrinted";
        String controlunit = "oc:materialCU";
        String wlancard = "oc:wlanCard";
        String cardbase = "oc:materialCard";
        String cable = "oc:cable";
        String transistor = "oc:materialTransistor";
        String numpad = "oc:materialNumPad";
        String batteryUpgrade = "oc:batteryUpgrade1";
        String oc_relay = "oc:relay";
        ItemStack floppy = li.cil.oc.api.Items.get("floppy").createItemStack(1);
        ItemStack datacard;
        if (li.cil.oc.api.Items.get("dataCard").createItemStack(1) != null) {
            datacard = li.cil.oc.api.Items.get("dataCard").createItemStack(1);
        } else {
            datacard = li.cil.oc.api.Items.get("dataCard1").createItemStack(1);
        }


        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(rfidReaderCardItem, 1),
                "MRM",
                " N ",
                "BC ",
                'M', t2microchip, 'R', t1ram, 'N', wlancard, 'B', cardbase, 'C', controlunit));


        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(entityDetector, 1),
                "MRM",
                "   ",
                "BC ",
                'M', t2microchip, 'R', t1ram, 'B', cardbase, 'C', controlunit));


        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(rfidReader, 1),
                " R ",
                "PFT",
                " C ",
                'F', rfidReaderCardItem, 'P', pcb, 'R', redstone, 'C', cable, 'T', t2microchip));


        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(dataBlock, 1),
                " D ",
                "PFT",
                " C ",
                'D', datacard, 'P', pcb, 'R', redstone, 'C', cable, 'T', t2microchip));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(alarmBlock, 1),
                " R ",
                "PNC",
                " T ",
                'N', noteblock, 'P', pcb, 'R', redstone, 'C', cable, 'T', t2microchip));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(cardWriter, 1),
                "TRT",
                "SUS",
                "PC ",
                'P', pcb, 'C', cable, 'T', t2microchip, 'S', transistor, 'U', controlunit, 'R', t1ram));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(magReader, 1),
                "T T",
                "S S",
                "PC ",
                'P', pcb, 'C', cable, 'T', t2microchip, 'S', transistor));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemRFIDCard, 6),
                "P P",
                " S ",
                "PTP",
                'P', paper, 'S', transistor, 'T', t1microchip));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemMagCard, 6),
                "P P",
                " S ",
                "P P",
                'P', paper, 'S', transistor));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(secureDoor, 1),
                "TGT",
                "ODO",
                "SOS",
                'G', glass, 'D', door, 'S', transistor, 'T', t2microchip, 'O', obsidian));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(privateSecureDoor, 1),
                "TOT",
                "ODO",
                "SOS",
                'D', door, 'S', transistor, 'T', t2microchip, 'O', obsidian));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(doorController, 1),
                "TOT",
                "OCO",
                "SBS",
                'B', cable, 'C', controlunit, 'S', transistor, 'T', t2microchip, 'O', obsidian));

/*		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(SwitchableHubBlock, 1),
				"TBT", 
				"BSB", 
				"RBR", 
				'B', cable, 'S', oc_relay, 'R', transistor, 'T', t2microchip, 'O', obsidian));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(KVMBlock, 1),
				" B ", 
				"BSB", 
				"RBR", 
				'B', cable,  'S', oc_relay, 'R', transistor, 'T', t2microchip, 'O', obsidian));*/

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(energyTurret, 1),
                "ABA",
                "BCB",
                "ABA",
                'A', iron, 'B', t2microchip, 'C', diamond));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(damageUpgradeItem, 1),
                "A A",
                " G ",
                "A A",
                'A', arrow, 'G', gunpowder));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(movementUpgradeItem, 1),
                "R R",
                " P ",
                "R R",
                'P', piston, 'R', redstone));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(cooldownUpgradeItem, 1),
                "R R",
                " W ",
                "R R",
                'W', water, 'R', redstone));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(energyUpgradeItem, 1),
                "R R",
                " B ",
                "R R",
                'B', batteryUpgrade, 'R', redstone));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(keypadBlock, 1),
                "TIT",
                "INI",
                "ICI",
                'T', transistor, 'N', numpad, 'C', t1microchip, 'I', iron));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(biometricReaderBlock, 1),
                "SIS",
                "STS",
                "SCS",
                'T', transistor, 'C', t1microchip, 'I', iron, 'S', stone));

        OpenSecurity.logger.info("Registered Recipes");
    }

    /**
     * Register a Block with the default ItemBlock class.
     *
     * @param block The Block instance
     * @param <BLOCK>   The Block type
     * @return The Block instance
     */
    protected static <BLOCK extends Block> BLOCK registerBlock(BLOCK block) {
        return registerBlock(block, ItemBlock::new);
    }

    /**
     * Register a Block with a custom ItemBlock class.
     *
     * @param <BLOCK>     The Block type
     * @param block       The Block instance
     * @param itemFactory A function that creates the ItemBlock instance, or null if no ItemBlock should be created
     * @return The Block instance
     */
    protected static <BLOCK extends Block> BLOCK registerBlock(BLOCK block, @Nullable Function<BLOCK, ItemBlock> itemFactory) {
        GameRegistry.register(block);

        if (itemFactory != null) {
            final ItemBlock itemBlock = itemFactory.apply(block);

            GameRegistry.register(itemBlock.setRegistryName(block.getRegistryName()));
        }

        blocks.add(block);
        return block;
    }

    public static void registerTabs() {

        creativeTab = new CreativeTabs("tabOpenSecurity") {
            @SideOnly(Side.CLIENT)
            public Item getTabIconItem() {
                return Item.getItemFromBlock(dataBlock);
            }

            @SideOnly(Side.CLIENT)
            public String getTranslatedTabLabel() {
                return I18n.translateToLocal("itemGroup.OpenSecurity.tabOpenSecurity");
            }
        };
    }
}
