package pcl.opensecurity.blocks;

import pcl.opensecurity.tileentity.TileEntityKeypadLock;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;


public class BlockKeypadLock extends BlockOSBase {

	static IIcon textureTop;
	static IIcon textureSide;
	static IIcon textureBottom;
	
	public BlockKeypadLock()
	{
		super();
		setBlockName("keypadlock");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister)
	{
		textureTop=iconRegister.registerIcon("opensecurity:machine_side");
		textureBottom=iconRegister.registerIcon("opensecurity:machine_side");
		textureSide=iconRegister.registerIcon("opensecurity:machine_side");
	}
	
	@Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int side)
    {
		if (side == 0)
            return textureBottom;

        if (side == 1)
    		return textureTop;
        
        return textureSide;
    }

    //called when rendering as block in inventory
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int par2)
    {
		return side == 1 ? textureTop : textureSide;
    }
    
	
	@Override
	public boolean shouldSideBeRendered(IBlockAccess blockAccess, int x, int y, int z, int side)
	{		
		if (side>1)
		{
			int mx=x, my=y, mz=z;
		    switch(side)
			{
  			case 2: mz++; break;
			case 3: mz--; break;
			case 4: mx++; break;
			case 5: mx--; break;
			}			

		    int facing=blockAccess.getBlockMetadata(mx, my, mz);
			if (facing==side)
				return false;			
		}
			
		return super.shouldSideBeRendered(blockAccess,x,y,z,side);
		/**/
	}
	
	
	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
	{
		//BLLog.debug("Activate with hit at %f, %f, %f", hitX, hitY, hitZ);
		if (player.isSneaking())
			return false;
		
		//if it wasn't the face, false
		int facing=world.getBlockMetadata(x, y, z);
		if (facing!=side)
		{
			//BLLog.debug("wrong side.");
			return false;
		}
		//BLLog.debug("side = %d", side);
		float relX=0f,relY=hitY*16f;
		//normalize face-relative "x" pixel position
		switch(facing)
		{
		case 2: relX=hitX*16f; break;
		case 3: relX=(1f-hitX)*16f; break;
		case 4: relX=(1f-hitZ)*16f; break;
		case 5: relX=hitZ*16f; break;		
		}
		
		//figure out what, if any, button was hit?
		if (relX<4f || relX>12 || relY<2f || relY>11.5f)
		{
			//BLLog.debug("outside button area.");			
			//completely outside area of buttons, return
			return true;
		}
		int col=(int)((relX-4f)/3f);
		float colOff=(relX-4f)%3f;
		int row=(int)((relY-2f)/2.5f);
		float rowOff=(relY-2f)%2.5f;
		//check and return if between buttons
		if (colOff>2f || rowOff>2f)
		{
			//BLLog.debug("between buttons.");
			return true;
		}		
		
		//ok! hit a button!
		//BLLog.debug("Hit button on row %d in col %d", row, col);
		int idx = (2-col)+3*(3-row);
		TileEntityKeypadLock te=(TileEntityKeypadLock)world.getTileEntity(x,y,z);
		te.pressedButton(player,idx);
		return true;
	
	}
	
	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityKeypadLock();
	}
}