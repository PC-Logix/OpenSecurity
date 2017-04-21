package pcl.opensecurity;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.registry.GameRegistry;
import pcl.opensecurity.common.blocks.BlockAlarm;

public class ContentRegistry {
	public static CreativeTabs creativeTab;
	public static Block alarmBlock;
	
	private ContentRegistry() {}
	
	// Called on mod preInit()
	public static void preInit() {
        registerBlocks();
        registerItems();
	}

	//Called on mod init()
	public static void init() {
		registerRecipes();
	}
	
	private static void registerItems() {
		
	}
	
	private static void registerBlocks() {
		alarmBlock = new BlockAlarm(Material.IRON);
		GameRegistry.registerBlock(alarmBlock, "alarm");
	}
	
	private static void registerRecipes() {
		
	}
}
