/**
 * 
 */
package pcl.opensecurity;

import java.util.concurrent.Callable;

import li.cil.oc.api.fs.FileSystem;
import pcl.opensecurity.blocks.BlockAlarm;
import pcl.opensecurity.blocks.BlockCardWriter;
import pcl.opensecurity.blocks.BlockData;
import pcl.opensecurity.blocks.BlockDisplayPanel;
import pcl.opensecurity.blocks.BlockDoorController;
import pcl.opensecurity.blocks.BlockEnergyTurret;
import pcl.opensecurity.blocks.BlockEntityDetector;
import pcl.opensecurity.blocks.BlockKVM;
import pcl.opensecurity.blocks.BlockMagReader;
import pcl.opensecurity.blocks.BlockRFIDReader;
import pcl.opensecurity.blocks.BlockSecurityDoor;
import pcl.opensecurity.blocks.BlockSecurityDoorPrivate;
import pcl.opensecurity.blocks.BlockSwitchableHub;
import pcl.opensecurity.client.CreativeTab;
import pcl.opensecurity.drivers.RFIDReaderCardDriver;
import pcl.opensecurity.entity.EntityEnergyBolt;
import pcl.opensecurity.items.ItemMagCard;
import pcl.opensecurity.items.ItemRFIDCard;
import pcl.opensecurity.items.ItemRFIDReaderCard;
import pcl.opensecurity.items.ItemSecurityDoor;
import pcl.opensecurity.items.ItemSecurityDoorPrivate;
import pcl.opensecurity.tileentity.TileEntityAlarm;
import pcl.opensecurity.tileentity.TileEntityCardWriter;
import pcl.opensecurity.tileentity.TileEntityDataBlock;
import pcl.opensecurity.tileentity.TileEntityDisplayPanel;
import pcl.opensecurity.tileentity.TileEntityDoorController;
import pcl.opensecurity.tileentity.TileEntityEnergyTurret;
import pcl.opensecurity.tileentity.TileEntityEntityDetector;
import pcl.opensecurity.tileentity.TileEntityKVM;
import pcl.opensecurity.tileentity.TileEntityMagReader;
import pcl.opensecurity.tileentity.TileEntityRFIDReader;
import pcl.opensecurity.tileentity.TileEntitySecureDoor;
import pcl.opensecurity.tileentity.TileEntitySwitchableHub;
import pcl.opensecurity.util.OSBreakEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

/**
 * @author Caitlyn
 *
 */
public class ContentRegistry {

	public static Block magCardReader;
	public static Block rfidCardReader;
	public static Block cardWriter;
	public static Block Alarm;
	public static Block EntityDetector;
	public static Block SecurityDoor;
	public static Block DoorController;
	public static Block DataBlock;
	public static Block SwitchableHub;
	public static Block BlockKVM;
	public static Block SecurityDoorPrivate;
	public static Block DisplayPanel;
	public static Block energyTurretBlock;
	public static Item magCard;
	public static Item rfidCard;
	public static Item securityDoor;
	public static Item securityDoorPrivate;
	public static Item rfidReaderCard;
	public static ItemBlock securityitemBlock;
	public static ItemStack secureOS_disk;
	public static CreativeTabs CreativeTab;
	
	
    // Called on mod init()
	public static void init() {
        registerTabs();
        registerBlocks();
        registerEntities();
        registerItems();
        registerEvents();
        registerRecipes();
	}

	private static void registerEntities() {
		EntityRegistry.registerModEntity(EntityEnergyBolt.class, "energybolt", 0, OpenSecurity.instance, 64, 20, true);
	}

	private static void registerEvents() {
		MinecraftForge.EVENT_BUS.register(new OSBreakEvent());
		OpenSecurity.logger.info("Registered Events");
	}

	private static void registerItems() {
		magCard = new ItemMagCard();
		GameRegistry.registerItem(magCard, "opensecurity.magCard");
		magCard.setCreativeTab(CreativeTab);

		rfidCard = new ItemRFIDCard();
		GameRegistry.registerItem(rfidCard, "opensecurity.rfidCard");
		rfidCard.setCreativeTab(CreativeTab);

		rfidReaderCard = new ItemRFIDReaderCard();
		GameRegistry.registerItem(rfidReaderCard, "opensecurity.rfidReaderCard");
		rfidReaderCard.setCreativeTab(CreativeTab);
		li.cil.oc.api.Driver.add(new RFIDReaderCardDriver());

		securityDoor = new ItemSecurityDoor(SecurityDoor);
		GameRegistry.registerItem(securityDoor, "opensecurity.securityDoor");
		securityDoor.setCreativeTab(CreativeTab);
		
		securityDoorPrivate = new ItemSecurityDoorPrivate(SecurityDoor);
		GameRegistry.registerItem(securityDoorPrivate, "opensecurity.securityDoorPrivate");
		securityDoor.setCreativeTab(CreativeTab);
		
		OpenSecurity.logger.info("Registered Items");
	}

	private static void registerBlocks() {
		magCardReader = new BlockMagReader();
		GameRegistry.registerBlock(magCardReader, "magreader");
		magCardReader.setCreativeTab(CreativeTab);
		GameRegistry.registerTileEntity(TileEntityMagReader.class, "MagCardTE");

		rfidCardReader = new BlockRFIDReader();
		GameRegistry.registerBlock(rfidCardReader, "rfidreader");
		rfidCardReader.setCreativeTab(CreativeTab);
		GameRegistry.registerTileEntity(TileEntityRFIDReader.class, "RFIDTE");

		cardWriter = new BlockCardWriter();
		GameRegistry.registerBlock(cardWriter, "rfidwriter");
		cardWriter.setCreativeTab(CreativeTab);
		GameRegistry.registerTileEntity(TileEntityCardWriter.class, "RFIDWriterTE");

		Alarm = new BlockAlarm();
		GameRegistry.registerBlock(Alarm, "alarm");
		Alarm.setCreativeTab(CreativeTab);
		GameRegistry.registerTileEntity(TileEntityAlarm.class, "AlarmTE");

		EntityDetector = new BlockEntityDetector();
		GameRegistry.registerBlock(EntityDetector, "entitydetector");
		EntityDetector.setCreativeTab(CreativeTab);
		GameRegistry.registerTileEntity(TileEntityEntityDetector.class, "EntityDetectorTE");

		DoorController = new BlockDoorController();
		GameRegistry.registerBlock(DoorController, "doorcontroller");
		DoorController.setCreativeTab(CreativeTab);
		GameRegistry.registerTileEntity(TileEntityDoorController.class, "DoorControllerTE");

		SecurityDoor = new BlockSecurityDoor();
		GameRegistry.registerBlock(SecurityDoor, "SecurityDoor");

		SecurityDoorPrivate = new BlockSecurityDoorPrivate();
		GameRegistry.registerBlock(SecurityDoorPrivate, "SecurityDoorPrivate");
		
		GameRegistry.registerTileEntity(TileEntitySecureDoor.class, "SecureDoorTE");
		
		DataBlock = new BlockData();
		GameRegistry.registerBlock(DataBlock, OpenSecurity.MODID + ".DataBlock");
		DataBlock.setCreativeTab(CreativeTab);
		GameRegistry.registerTileEntity(TileEntityDataBlock.class, OpenSecurity.MODID + ".DataBlockTE");

		SwitchableHub = new BlockSwitchableHub();
		GameRegistry.registerBlock(SwitchableHub, OpenSecurity.MODID + ".SwitchableHub");
		SwitchableHub.setCreativeTab(CreativeTab);
		GameRegistry.registerTileEntity(TileEntitySwitchableHub.class, OpenSecurity.MODID + ".SwitchableHubTE");
		
		BlockKVM = new BlockKVM();
		GameRegistry.registerBlock(BlockKVM, OpenSecurity.MODID + ".BlockKVM");
		BlockKVM.setCreativeTab(CreativeTab);
		GameRegistry.registerTileEntity(TileEntityKVM.class, OpenSecurity.MODID + ".KVMTE");

		//DisplayPanel = new BlockDisplayPanel();
		//GameRegistry.registerBlock(DisplayPanel, OpenSecurity.MODID + ".DisplayPanel");
		//DisplayPanel.setCreativeTab(CreativeTab);
		
		//GameRegistry.registerTileEntity(TileEntityDisplayPanel.class, OpenSecurity.MODID + ".DisplayPanelTE");
		
		energyTurretBlock = new BlockEnergyTurret();
		GameRegistry.registerBlock(energyTurretBlock, "energyTurretBlock");
		energyTurretBlock.setCreativeTab(CreativeTab);
		
		GameRegistry.registerTileEntity(TileEntityEnergyTurret.class, "EnergyTurret");

		
		OpenSecurity.logger.info("Registered Blocks");
	}

	private static void registerTabs() {
		 CreativeTab = new CreativeTab("OpenSecurity");
	}
	
	private static void registerRecipes() {
		Callable<FileSystem> factory = new Callable<FileSystem>() {
			@Override
			public FileSystem call() {
				return li.cil.oc.api.FileSystem.fromClass(OpenSecurity.class, OpenSecurity.MODID, "/lua/SecureOS/");
			}
		};
		secureOS_disk = li.cil.oc.api.Items.registerFloppy("SecureOS", 1, factory);

		ItemStack redstone = new ItemStack(Items.redstone);
		ItemStack paper = new ItemStack(Items.paper);
		ItemStack noteblock = new ItemStack(Blocks.noteblock);
		ItemStack door = new ItemStack(Items.iron_door);
		ItemStack obsidian = new ItemStack(Blocks.obsidian);
		ItemStack t2microchip = li.cil.oc.api.Items.get("chip2").createItemStack(1);
		ItemStack t1microchip = li.cil.oc.api.Items.get("chip1").createItemStack(1);
		ItemStack t1ram = li.cil.oc.api.Items.get("ram1").createItemStack(1);
		ItemStack pcb = li.cil.oc.api.Items.get("printedCircuitBoard").createItemStack(1);
		ItemStack controlunit = li.cil.oc.api.Items.get("cu").createItemStack(1);
		ItemStack wlancard = li.cil.oc.api.Items.get("wlanCard").createItemStack(1);
		ItemStack cardbase = li.cil.oc.api.Items.get("card").createItemStack(1);
		ItemStack cable = li.cil.oc.api.Items.get("cable").createItemStack(1);
		ItemStack transistor = li.cil.oc.api.Items.get("transistor").createItemStack(1);
		ItemStack floppy = li.cil.oc.api.Items.get("floppy").createItemStack(1);
		ItemStack datacard;
		if (li.cil.oc.api.Items.get("dataCard").createItemStack(1) != null) {
			datacard = li.cil.oc.api.Items.get("dataCard").createItemStack(1);
		} else {
			datacard = li.cil.oc.api.Items.get("dataCard1").createItemStack(1);
		}
		
		ItemStack oc_relay = li.cil.oc.api.Items.get("relay").createItemStack(1);

		GameRegistry.addRecipe(new ItemStack(rfidReaderCard, 1), "MRM", " N ", "BC ", 'M', t2microchip, 'R', t1ram, 'N', wlancard, 'B', cardbase, 'C', controlunit);

		GameRegistry.addRecipe(new ItemStack(EntityDetector, 1), "MRM", "   ", "BC ", 'M', t2microchip, 'R', t1ram, 'B', cardbase, 'C', controlunit);

		GameRegistry.addRecipe(new ItemStack(rfidCardReader, 1), " R ", "PFT", " C ", 'F', rfidReaderCard, 'P', pcb, 'R', redstone, 'C', cable, 'T', t2microchip);

		GameRegistry.addRecipe(new ItemStack(DataBlock, 1), " D ", "PFT", " C ", 'D', datacard, 'P', pcb, 'R', redstone, 'C', cable, 'T', t2microchip);

		GameRegistry.addRecipe(new ItemStack(Alarm, 1), " R ", "PNC", " T ", 'N', noteblock, 'P', pcb, 'R', redstone, 'C', cable, 'T', t2microchip);

		GameRegistry.addRecipe(new ItemStack(cardWriter, 1), "TRT", "SUS", "PC ", 'P', pcb, 'C', cable, 'T', t2microchip, 'S', transistor, 'U', controlunit, 'R', t1ram);

		GameRegistry.addRecipe(new ItemStack(magCardReader, 1), "T T", "S S", "PC ", 'P', pcb, 'C', cable, 'T', t2microchip, 'S', transistor);

		GameRegistry.addRecipe(new ItemStack(rfidCard, 6), "P P", " S ", "PTP", 'P', paper, 'S', transistor, 'T', t1microchip);

		GameRegistry.addRecipe(new ItemStack(magCard, 6), "P P", " S ", "P P", 'P', paper, 'S', transistor);

		GameRegistry.addRecipe(new ItemStack(securityDoor, 1), "TOT", "ODO", "SOS", 'D', door, 'S', transistor, 'T', t2microchip, 'O', obsidian);

		GameRegistry.addRecipe(new ItemStack(DoorController, 1), "TOT", "OCO", "SBS", 'B', cable, 'C', controlunit, 'S', transistor, 'T', t2microchip, 'O', obsidian);

		GameRegistry.addRecipe(new ItemStack(SwitchableHub, 1), "TBT", "BSB", "RBR", 'B', cable, 'S', oc_relay, 'R', transistor, 'T', t2microchip, 'O', obsidian);
		
		GameRegistry.addRecipe(new ItemStack(BlockKVM, 1), " B ", "BSB", "RBR", 'B', cable, 'S', oc_relay, 'R', transistor, 'T', t2microchip, 'O', obsidian);
		
		GameRegistry.addShapelessRecipe(secureOS_disk, new Object[] { floppy, magCard });
		OpenSecurity.logger.info("Registered Recipes");
	}
}
