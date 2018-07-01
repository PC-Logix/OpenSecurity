package pcl.opensecurity.client;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.client.models.CamouflageBakedModel;
import pcl.opensecurity.client.models.ModColourManager;
import pcl.opensecurity.client.models.ModelBakeEventHandler;
import pcl.opensecurity.client.renderer.*;
import pcl.opensecurity.client.sounds.AlarmResource;
import pcl.opensecurity.common.CommonProxy;
import pcl.opensecurity.common.ContentRegistry;
import pcl.opensecurity.common.Reference;
import pcl.opensecurity.common.entity.EntityEnergyBolt;
import pcl.opensecurity.common.items.ItemCard;
import pcl.opensecurity.common.tileentity.TileEntityEnergyTurret;
import pcl.opensecurity.common.tileentity.TileEntityKeypad;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ClientProxy extends CommonProxy {
    public static List<String> alarmList = new ArrayList<String>();

    @Override
    public World getWorld(int dimId) {
        World world = Minecraft.getMinecraft().world;
        if (world.provider.getDimension() == dimId) {
            return world;
        }
        return null;
    }

    @Override
    public void preinit() {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(ModelBakeEventHandler.instance);
    }

    @SubscribeEvent
    public void renderWorldLastEvent(RenderWorldLastEvent evt) {
        SecurityTerminalRender.showFoundTerminals(evt);
    }

    @Override
    public void init() {
        super.init();
        Minecraft mc = Minecraft.getMinecraft();
        mc.getItemColors().registerItemColorHandler(new CardColorHandler(ContentRegistry.itemRFIDCard), ContentRegistry.itemRFIDCard);
        mc.getItemColors().registerItemColorHandler(new CardColorHandler(ContentRegistry.itemMagCard), ContentRegistry.itemMagCard);
        ModColourManager.registerColourHandlers();

        StateMapperBase ignoreState = new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState iBlockState) {
                return CamouflageBakedModel.variantTag;
            }
        };
        ModelLoader.setCustomStateMapper(ContentRegistry.doorController, ignoreState);
    }

    public void registerRenderers() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityKeypad.class, new RenderKeypad());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEnergyTurret.class, new RenderEnergyTurret());
        TileEntityItemStackRenderer.instance = new EnergyTurretRenderHelper();
        RenderingRegistry.registerEntityRenderingHandler(EntityEnergyBolt.class, RenderEntityEnergyBolt::new);
        OpenSecurity.logger.info("Registering TESR");
    }

    @Override
    public void registerModels() {
        registerBlockItem(ContentRegistry.alarmBlock, 0, Reference.Names.BLOCK_ALARM);
        registerBlockItem(ContentRegistry.doorController, 0, Reference.Names.BLOCK_DOOR_CONTROLLER);
        registerBlockItem(ContentRegistry.securityTerminal, 0, Reference.Names.BLOCK_SECURITY_TERMINAL);
        registerBlockItem(ContentRegistry.biometricReaderBlock, 0, Reference.Names.BLOCK_BIOMETRIC_READER);
        registerBlockItem(ContentRegistry.dataBlock, 0, Reference.Names.BLOCK_DATA);
        registerBlockItem(ContentRegistry.cardWriter, 0, Reference.Names.BLOCK_CARD_WRITER);
        registerBlockItem(ContentRegistry.magReader, 0, Reference.Names.BLOCK_MAG_READER);
        registerBlockItem(ContentRegistry.keypadBlock, 0, Reference.Names.BLOCK_KEYPAD);
        registerBlockItem(ContentRegistry.entityDetector, 0, Reference.Names.BLOCK_ENTITY_DETECTOR);
        registerBlockItem(ContentRegistry.energyTurret, 0, Reference.Names.BLOCK_ENERGY_TURRET);
        registerBlockItem(ContentRegistry.rfidReader, 0, Reference.Names.BLOCK_RFID_READER);
        registerBlockItem(ContentRegistry.secureDoor, 0, Reference.Names.BLOCK_SECURE_DOOR);
        registerBlockItem(ContentRegistry.privateSecureDoor, 0, Reference.Names.BLOCK_PRIVATE_SECURE_DOOR);

        registerItem(ContentRegistry.itemRFIDCard, Reference.Names.ITEM_RFID_CARD);
        registerItem(ContentRegistry.rfidReaderCardItem, Reference.Names.ITEM_RFID_READER_CARD);
        registerItem(ContentRegistry.itemMagCard, Reference.Names.ITEM_MAG_CARD);
        registerItem(ContentRegistry.damageUpgradeItem, Reference.Names.ITEM_DAMAGE_UPGRADE);
        registerItem(ContentRegistry.movementUpgradeItem, Reference.Names.ITEM_MOVEMENT_UPGRADE);
        registerItem(ContentRegistry.energyUpgradeItem, Reference.Names.ITEM_ENERGY_UPGRADE);
        registerItem(ContentRegistry.cooldownUpgradeItem, Reference.Names.ITEM_COOLDOWN_UPGRADE);
    }

    private void registerBlockItem(final Block block, int meta, final String blockName) {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), meta, new ModelResourceLocation(block.getRegistryName().toString()));
        OpenSecurity.logger.info("Registering " + blockName + " Item Renderer");
    }

    private void registerItem(final Item item, final String itemName) {
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(OpenSecurity.MODID + ":" + itemName));
        OpenSecurity.logger.info("Registering " + itemName + " Item Renderer");
    }

    public void listFilesForFolder(final File folder) {
        AlarmResource r = new AlarmResource();
        int i = 1;
        if (folder.listFiles() != null) {
            for (final File fileEntry : folder.listFiles()) {
                if (fileEntry.isDirectory()) {
                    listFilesForFolder(fileEntry);
                } else {
                    r.addSoundReferenceMapping(i, fileEntry.getName()); //add map soundlocation -> recordX
                    i++;
                }
            }
        }
        r.registerAsResourceLocation(); //finalise IResourcePack
    }

    @Override
    public void registerSounds() {
        File[] listOfFiles;
        File alarmSounds = new File("./mods/OpenSecurity/assets/opensecurity/sounds/alarms");
        if (alarmSounds.exists()) {
            listOfFiles = alarmSounds.listFiles();

            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    alarmList.add(listOfFiles[i].getName());
                }
            }
        }
        listFilesForFolder(alarmSounds);
    }


    @SideOnly(Side.CLIENT)
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