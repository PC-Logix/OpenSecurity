package pcl.opensecurity.client;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pcl.opensecurity.Config;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.client.models.ModColourManager;
import pcl.opensecurity.client.models.ModelNanoFogSwarm;
import pcl.opensecurity.client.renderer.*;
import pcl.opensecurity.client.sounds.AlarmResource;
import pcl.opensecurity.common.CommonProxy;
import pcl.opensecurity.common.ContentRegistry;
import pcl.opensecurity.common.blocks.*;
import pcl.opensecurity.common.entity.EntityEnergyBolt;
import pcl.opensecurity.common.entity.EntityNanoFogSwarm;
import pcl.opensecurity.common.items.*;
import pcl.opensecurity.common.tileentity.TileEntityEnergyTurret;
import pcl.opensecurity.common.tileentity.TileEntityKeypad;
import pcl.opensecurity.common.tileentity.TileEntityRolldoorController;
import pcl.opensecurity.manual.ManualPathProvider;
import pcl.opensecurity.util.FileUtils;

import java.io.File;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @SubscribeEvent
    public void renderWorldLastEvent(RenderWorldLastEvent evt) {
        SecurityTerminalRender.showFoundTerminals(evt);
    }

    @SubscribeEvent
    public void colorHandlerEventBlock(ColorHandlerEvent.Block event) {
        ContentRegistry.nanoFog.initColorHandler(event.getBlockColors());
        ContentRegistry.doorController.initColorHandler(event.getBlockColors());
        ContentRegistry.rolldoorController.initColorHandler(event.getBlockColors());
    }

    @Override
    public World getWorld(int dimId) {
        World world = Minecraft.getMinecraft().world;
        return world.provider.getDimension() == dimId ? world : null;
    }

    @Override
    public void preinit() {
        super.preinit();
        Config.clientPreInit();

        ModelNanoFogSwarm.setupResolution(Config.getConfig().getCategory("client").get("nanoFogSwarmResolution").getInt());

        MinecraftForge.EVENT_BUS.register(this);

        ModelLoaderRegistry.registerLoader(new CamouflageBlockModelLoader());


        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityKeypad.class, new RenderKeypad());

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRolldoorController.class, new RenderRolldoorController());


        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEnergyTurret.class, new RenderEnergyTurret());
        TileEntityItemStackRenderer.instance = new EnergyTurretRenderHelper();

        RenderingRegistry.registerEntityRenderingHandler(EntityEnergyBolt.class, RenderEntityEnergyBolt::new);

        RenderingRegistry.registerEntityRenderingHandler(EntityNanoFogSwarm.class, NanoFogSwarmRenderer.FACTORY);

        if(OpenSecurity.debug)
            OpenSecurity.logger.info("Registered renderers/models");
    }

    @Override
    public void init() {
        super.init();
        Minecraft mc = Minecraft.getMinecraft();
        mc.getItemColors().registerItemColorHandler(new CardColorHandler(ContentRegistry.itemRFIDCard), ContentRegistry.itemRFIDCard);
        mc.getItemColors().registerItemColorHandler(new CardColorHandler(ContentRegistry.itemMagCard), ContentRegistry.itemMagCard);
        ModColourManager.registerColourHandlers();
        ManualPathProvider.initialize();
    }

    @Override
    public void registerModels() {
        registerBlockItem(ContentRegistry.alarmBlock, 0, BlockAlarm.NAME);
        registerBlockItem(ContentRegistry.securityTerminal, 0, BlockSecurityTerminal.NAME);
        registerBlockItem(ContentRegistry.biometricReaderBlock, 0, BlockBiometricReader.NAME);
        registerBlockItem(ContentRegistry.dataBlock, 0, BlockData.NAME);
        registerBlockItem(ContentRegistry.cardWriter, 0, BlockCardWriter.NAME);
        registerBlockItem(ContentRegistry.magReader, 0, BlockMagReader.NAME);
        registerBlockItem(ContentRegistry.keypadBlock, 0, BlockKeypad.NAME);
        registerBlockItem(ContentRegistry.entityDetector, 0, BlockEntityDetector.NAME);
        registerBlockItem(ContentRegistry.energyTurret, 0, BlockEnergyTurret.NAME);
        registerBlockItem(ContentRegistry.rfidReader, 0, BlockRFIDReader.NAME);
        registerBlockItem(ContentRegistry.nanoFogTerminal, 0, BlockNanoFogTerminal.NAME);
        registerBlockItem(ContentRegistry.rolldoor, 0, BlockRolldoor.NAME);
        registerBlockItem(ContentRegistry.rolldoorElement, 0, BlockRolldoorElement.NAME);

        // BlockNanoFog uses custom texture/model loader for shield blocks
        CamouflageBlockModelLoader.registerBlock(ContentRegistry.nanoFog);
        CamouflageBlockModelLoader.registerBlock(ContentRegistry.doorController);
        CamouflageBlockModelLoader.registerBlock(ContentRegistry.rolldoorController);


        registerItem(ContentRegistry.secureDoorItem, BlockSecureDoor.NAME);
        registerItem(ContentRegistry.securePrivateDoorItem, BlockSecurePrivateDoor.NAME);
        registerItem(ContentRegistry.itemRFIDCard, ItemRFIDCard.NAME);
        registerItem(ContentRegistry.rfidReaderCardItem, ItemRFIDReaderCard.NAME);
        registerItem(ContentRegistry.itemMagCard, ItemMagCard.NAME);
        registerItem(ContentRegistry.damageUpgradeItem, ItemDamageUpgrade.NAME);
        registerItem(ContentRegistry.movementUpgradeItem, ItemMovementUpgrade.NAME);
        registerItem(ContentRegistry.energyUpgradeItem, ItemEnergyUpgrade.NAME);
        registerItem(ContentRegistry.cooldownUpgradeItem, ItemCooldownUpgrade.NAME);
        registerItem(ContentRegistry.nanoDNAItem, ItemNanoDNA.NAME);

        ModelLoader.setCustomStateMapper(ContentRegistry.secureDoor, new StateMap.Builder().ignore(BlockDoor.POWERED).build());
        ModelLoader.setCustomStateMapper(ContentRegistry.privateSecureDoor, new StateMap.Builder().ignore(BlockDoor.POWERED).build());
    }

    private void registerBlockItem(final Block block, int meta, final String blockName) {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), meta, new ModelResourceLocation(block.getRegistryName().toString(), "inventory"));
        if(OpenSecurity.debug)
            OpenSecurity.logger.info("Registering Renderer for block '" + blockName + "'");
    }

    private void registerItem(final Item item, final String itemName) {
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(OpenSecurity.MODID + ":" + itemName));
        if(OpenSecurity.debug)
            OpenSecurity.logger.info("Registering Renderer for Item '" + itemName + "'");
    }

    private void listFilesForPath(final File path) {
        AlarmResource r = new AlarmResource();
        int i = 1;

        for(File fileEntry : FileUtils.listFilesForPath(path.getPath()))
            r.addSoundReferenceMapping(i++, fileEntry.getName()); //add map soundlocation -> recordX

        r.registerAsResourceLocation(); //finalise IResourcePack
    }

    @Override
    public void registerSounds() {
        File alarmSounds = new File("./mods/OpenSecurity/assets/opensecurity/sounds/alarms");

        if (!alarmSounds.exists())
            return;

        for(File file : alarmSounds.listFiles())
            if (file.isFile())
                OpenSecurity.alarmList.add(file.getName());

        listFilesForPath(alarmSounds);
    }

    private static class CardColorHandler implements IItemColor {
        private final ItemCard card;

        private CardColorHandler(ItemCard card) {
            this.card = card;
        }

        @Override
        public int colorMultiplier(ItemStack stack, int tintIndex) {
            // TODO Auto-generated method stub
            return tintIndex == 0 ? 0xFFFFFF : card.getColor(stack);
        }
    }

}