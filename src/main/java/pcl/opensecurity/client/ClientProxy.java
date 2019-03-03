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
        BlockNanoFog.DEFAULTITEM.initColorHandler(event.getBlockColors());
        BlockDoorController.DEFAULTITEM.initColorHandler(event.getBlockColors());
        BlockRolldoorController.DEFAULTITEM.initColorHandler(event.getBlockColors());
        BlockRolldoor.DEFAULTITEM.initColorHandler(event.getBlockColors());
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
        mc.getItemColors().registerItemColorHandler(new CardColorHandler(), ItemRFIDCard.DEFAULTSTACK.getItem());
        mc.getItemColors().registerItemColorHandler(new CardColorHandler(), ItemMagCard.DEFAULTSTACK.getItem());
        ManualPathProvider.initialize();
    }

    @Override
    public void registerModels() {
        for(Block block : ContentRegistry.modBlocks)
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(block.getRegistryName().toString(), "inventory"));

        // BlockNanoFog uses custom texture/model loader for shield blocks
        for(Block block : ContentRegistry.modCamoBlocks)
            CamouflageBlockModelLoader.registerBlock((BlockCamouflage) block);

        for(ItemStack itemStack : ContentRegistry.modBlocksWithItem.values())
            ModelLoader.setCustomModelResourceLocation(itemStack.getItem(), 0, new ModelResourceLocation(itemStack.getItem().getRegistryName().toString()));

        for(ItemStack itemStack : ContentRegistry.modItems)
            ModelLoader.setCustomModelResourceLocation(itemStack.getItem(), 0, new ModelResourceLocation(itemStack.getItem().getRegistryName().toString()));

        ModelLoader.setCustomStateMapper(BlockRolldoorElement.DEFAULTITEM, new StateMap.Builder().ignore(BlockRolldoorElement.PROPERTYOFFSET).build());
        ModelLoader.setCustomStateMapper(BlockSecureDoor.DEFAULTITEM, new StateMap.Builder().ignore(BlockDoor.POWERED).build());
        ModelLoader.setCustomStateMapper(BlockSecurePrivateDoor.DEFAULTITEM, new StateMap.Builder().ignore(BlockDoor.POWERED).build());
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
        private CardColorHandler() {}

        @Override
        public int colorMultiplier(ItemStack stack, int tintIndex) {
            // TODO Auto-generated method stub
            return tintIndex == 0 ? 0xFFFFFF : new ItemCard.CardTag(stack.getTagCompound()).color;
        }
    }
}