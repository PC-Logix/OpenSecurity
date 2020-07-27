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
import pcl.opensecurity.manual.Manual;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Mod.EventBusSubscriber
public class ContentRegistry {
    public static CreativeTabs creativeTab = new CreativeTabs("tabOpenSecurity") {
        public @Nonnull ItemStack getTabIconItem() {
            return new ItemStack(Item.getItemFromBlock(BlockSecurityTerminal.DEFAULTITEM));
        }

        public @Nonnull String getTranslatedTabLabel() {
            return new TextComponentTranslation("itemGroup.OpenSecurity.tabOpenSecurity").getUnformattedText();
        }
    };

    // holds a list of normal mod blocks
    public static final HashSet<Block> modBlocks = new HashSet<>();

    // holds a list of mod blocks that should register as blocks that can be camouflaged
    public static final HashSet<Block> modCamoBlocks = new HashSet<>();

    // holds a list of mod blocks that have a specific custom Item like the doors
    public static final HashMap<Block, ItemStack> modBlocksWithItem = new HashMap<>();

    // holds a list of normal mod items
    public static final HashSet<ItemStack> modItems = new HashSet<>();

    static {
        modBlocks.add(BlockAlarm.DEFAULTITEM = new BlockAlarm());
        modBlocks.add(BlockSecurityTerminal.DEFAULTITEM = new BlockSecurityTerminal());
        modBlocks.add(BlockBiometricReader.DEFAULTITEM = new BlockBiometricReader());
        modBlocks.add(BlockData.DEFAULTITEM = new BlockData());
        modBlocks.add(BlockCardWriter.DEFAULTITEM = new BlockCardWriter());
        modBlocks.add(BlockMagReader.DEFAULTITEM = new BlockMagReader());
        modBlocks.add(BlockKeypad.DEFAULTITEM = new BlockKeypad());
        modBlocks.add(BlockEntityDetector.DEFAULTITEM = new BlockEntityDetector());
        modBlocks.add(BlockEnergyTurret.DEFAULTITEM = new BlockEnergyTurret());
        modBlocks.add(BlockRFIDReader.DEFAULTITEM = new BlockRFIDReader());
        modBlocks.add(BlockNanoFogTerminal.DEFAULTITEM = new BlockNanoFogTerminal());
        modBlocks.add(BlockRolldoorElement.DEFAULTITEM = new BlockRolldoorElement());

        modBlocksWithItem.put(BlockSecureDoor.DEFAULTITEM = new BlockSecureDoor(), ItemSecureDoor.DEFAULTSTACK = new ItemStack(new ItemSecureDoor()));
        modBlocksWithItem.put(BlockSecurePrivateDoor.DEFAULTITEM = new BlockSecurePrivateDoor(), ItemSecurePrivateDoor.DEFAULTSTACK = new ItemStack(new ItemSecurePrivateDoor()));

        modCamoBlocks.add(BlockRolldoor.DEFAULTITEM = new BlockRolldoor());
        modCamoBlocks.add(BlockDoorController.DEFAULTITEM = new BlockDoorController());
        modCamoBlocks.add(BlockRolldoorController.DEFAULTITEM = new BlockRolldoorController());
        modCamoBlocks.add(BlockNanoFog.DEFAULTITEM = new BlockNanoFog());

        modItems.add(ItemRFIDCard.DEFAULTSTACK = new ItemStack(new ItemRFIDCard()));
        modItems.add(ItemMagCard.DEFAULTSTACK = new ItemStack(new ItemMagCard()));
        modItems.add(ItemRFIDReaderCard.DEFAULTSTACK = new ItemStack(new ItemRFIDReaderCard()));
        modItems.add(ItemDamageUpgrade.DEFAULTSTACK = new ItemStack(new ItemDamageUpgrade()));
        modItems.add(ItemCooldownUpgrade.DEFAULTSTACK = new ItemStack(new ItemCooldownUpgrade()));
        modItems.add(ItemEnergyUpgrade.DEFAULTSTACK = new ItemStack(new ItemEnergyUpgrade()));
        modItems.add(ItemMovementUpgrade.DEFAULTSTACK = new ItemStack(new ItemMovementUpgrade()));
        modItems.add(ItemNanoDNA.DEFAULTSTACK = new ItemStack(new ItemNanoDNA()));
    }


    // Called on mod preInit()
    public static void preInit() {
        for(Item manualItem : Manual.items)
            modItems.add(new ItemStack(manualItem));

        registerEvents();
    }

    private static void registerEvents() {
        if(OpenSecurity.registerBlockBreakEvent)
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
    public static void addBlocks(RegistryEvent.Register<Block> event) {
        for(Block block : modBlocks)
            event.getRegistry().register(block);

        for(Block block : modBlocksWithItem.keySet())
            event.getRegistry().register(block);

        for(Block block : modCamoBlocks)
            event.getRegistry().register(block);

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
    public static void addItems(RegistryEvent.Register<Item> event) {

        for(Block block : modBlocks)
            event.getRegistry().register(new ItemBlock(block).setRegistryName(block.getRegistryName()));

        for(Map.Entry<Block, ItemStack> entry : modBlocksWithItem.entrySet())
            event.getRegistry().register(entry.getValue().getItem());

        for(Block block : modCamoBlocks)
            event.getRegistry().register(new ItemBlock(block).setRegistryName(block.getRegistryName()));

        for(ItemStack itemStack : modItems)
            event.getRegistry().register(itemStack.getItem());
    }
}
