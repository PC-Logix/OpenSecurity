package pcl.opensecurity;

/**
 * @author Caitlyn
 *
 */
import java.net.URL;
import java.util.logging.Logger;








import pcl.opensecurity.BuildInfo;
import pcl.opensecurity.blocks.BaseMagReaderBlock;
import pcl.opensecurity.gui.SecurityGUIHandler;
import pcl.opensecurity.items.MagCardComponentItemBlock;
import pcl.opensecurity.items.RFIDCardComponentItemBlock;
import pcl.opensecurity.items.MagCard;
import pcl.opensecurity.items.RFIDCard;
import pcl.opensecurity.tileentity.MagComponent;
import pcl.opensecurity.tileentity.RFIDComponent;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import li.cil.oc.api.Blocks;
import li.cil.oc.api.CreativeTab;
import li.cil.oc.api.Items;

@Mod(modid=OpenSecurity.MODID, name="OpenSecurity", version=BuildInfo.versionNumber + "." + BuildInfo.buildNumber, dependencies = "after:OpenComputers")
@NetworkMod(clientSideRequired=true)
public class OpenSecurity {
	
	public static final String MODID = "opensecurity";
	
		public static Block magCardComponent;
		public static Block rfidCardComponent;
		public static Item  magCard;
		public static Item  rfidCard;
		public static ItemBlock  securityitemBlock;
		
        @Instance(value = MODID)
        public static OpenSecurity instance;
        
        @SidedProxy(clientSide="pcl.opensecurity.ClientProxy", serverSide="pcl.opensecurity.CommonProxy")
        public static CommonProxy proxy;
        public static Config cfg = null;
        public static boolean render3D = true;
        
        private static boolean debug = true;
        public static Logger logger;
        
        @EventHandler
        public void preInit(FMLPreInitializationEvent event) {
        	
        	
        	cfg = new Config(new Configuration(event.getSuggestedConfigurationFile()));
        	render3D = cfg.render3D;
        	
            if((event.getSourceFile().getName().endsWith(".jar") || debug) && event.getSide().isClient() && cfg.enableMUD){
                try {
                    Class.forName("pcl.openprinter.mud.ModUpdateDetector").getDeclaredMethod("registerMod", ModContainer.class, URL.class, URL.class).invoke(null,
                            FMLCommonHandler.instance().findContainerFor(this),
                            new URL("http://PC-Logix.com/OpenPrinter/get_latest_build.php"),
                            new URL("http://PC-Logix.com/OpenPrinter/changelog.txt")
                    );
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
            logger = event.getModLog();
        	
        	
        	NetworkRegistry.instance().registerGuiHandler(this, new SecurityGUIHandler());
        	GameRegistry.registerTileEntity(MagComponent.class, "MagCardTE");
        	GameRegistry.registerTileEntity(RFIDComponent.class, "RFIDTE");
        	
        	//Register Blocks
        	magCardComponent = new BaseMagReaderBlock(cfg.securityBlockID, Material.iron);
        	GameRegistry.registerBlock(magCardComponent, MagCardComponentItemBlock.class, "opensecurity.magCardComponent");
        	magCardComponent.setCreativeTab(li.cil.oc.api.CreativeTab.Instance);
        	
        	rfidCardComponent = new BaseMagReaderBlock(cfg.securityBlockID, Material.iron);
        	GameRegistry.registerBlock(rfidCardComponent, RFIDCardComponentItemBlock.class, "opensecurity.rfidCardComponent");
        	rfidCardComponent.setCreativeTab(li.cil.oc.api.CreativeTab.Instance);
        	
        	//Register Items
        	magCard = new MagCard(cfg.magCardID);
    		GameRegistry.registerItem(magCard, "opensecurity.magCard");
    		magCard.setUnlocalizedName("magCard");
    		magCard.setTextureName("minecraft:paper");
    		magCard.setCreativeTab(li.cil.oc.api.CreativeTab.Instance);
    		
        	rfidCard = new RFIDCard(cfg.rfidCardID);
    		GameRegistry.registerItem(rfidCard, "opensecurity.rfidCard");
    		rfidCard.setUnlocalizedName("printerPaperRoll");
    		rfidCard.setTextureName("opensecurity:rfidCard");
    		rfidCard.setCreativeTab(li.cil.oc.api.CreativeTab.Instance);
        	
        }
        
        @EventHandler
    	public void load(FMLInitializationEvent event)
    	{
        	ItemStack nuggetIron	= Items.IronNugget;
        	ItemStack redstone		= new ItemStack(Item.redstone);
        	ItemStack microchip		= Items.MicrochipTier1;
        	ItemStack pcb			= Items.PrintedCircuitBoard;
        	ItemStack paper			= new ItemStack(Item.paper);

        	//magCardComponent
        	GameRegistry.addRecipe( new ItemStack(magCardComponent, 1), 
        			"IRI",
        			"MPM",
        			"IRI",
        			'I', nuggetIron, 'R', redstone, 'M', microchip, 'P', pcb);
        	
        	//rfidCardComponent
        	GameRegistry.addRecipe( new ItemStack(rfidCardComponent, 1), 
        			"IRI",
        			"MPM",
        			"IRI",
        			'I', nuggetIron, 'R', redstone, 'M', microchip, 'P', pcb);
        	

        			
        	
        	
    		proxy.registerRenderers();
    	}
}