package pcl.opensecurity.common.blocks;

import net.minecraft.block.material.Material;

public class BlockSecurePrivateDoor extends BlockSecureDoor {

	public BlockSecurePrivateDoor(Material materialIn) {
		super(materialIn);
		setUnlocalizedName("private_secure_door");
		setHardness(.5f);
	}

}
