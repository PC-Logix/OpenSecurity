package pcl.opensecurity;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.registry.GameRegistry;
import pcl.opensecurity.common.blocks.BlockAlarm;
import pcl.opensecurity.common.blocks.BlockBiometricReader;
import pcl.opensecurity.common.tileentity.TileEntityAlarm;
import pcl.opensecurity.common.tileentity.TileEntityBiometricReader;

public class ContentRegistry {
	public static CreativeTabs creativeTab;
	public static Block alarmBlock;
	public static Block biometricReaderBlock;
	
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
	
	@SuppressWarnings("deprecation")
	private static void registerBlocks() {
		alarmBlock = new BlockAlarm(Material.IRON);
		GameRegistry.registerBlock(alarmBlock, "alarm");
		GameRegistry.registerTileEntity(TileEntityAlarm.class, "alarm");
		
		biometricReaderBlock = new BlockBiometricReader(Material.IRON);
		GameRegistry.registerBlock(biometricReaderBlock, "biometric_reader");
		GameRegistry.registerTileEntity(TileEntityBiometricReader.class, "biometric_reader");
	}
	
	private static void registerRecipes() {
		
	}
}
