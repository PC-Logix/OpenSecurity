package pcl.opensecurity.util;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockLocation {
	
	/** The IBlockAccess for this block location. */
	public final IBlockAccess blockAccess;
	/** The World for this block location. <br>
	 *  May be null if the IBlockAccess getter was used. */
	public final World world;
	/** The location for this block location. */
	public final int x, y, z;
	
	private final boolean isWorld;
	
	private BlockLocation(IBlockAccess blockAccess, World world,
	                      int x, int y, int z, boolean isWorld) {
		this.blockAccess = blockAccess;
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.isWorld = isWorld;
	}
	
	// Static instantiation methods

	/** Returns a block location from this world and location. */
	public static BlockLocation get(IBlockAccess blockAccess, int x, int y, int z) {
		return new BlockLocation(blockAccess, null, x, y, z, false);
	}
	/** Returns a block location from this world and location. */
	public static BlockLocation get(World world, int x, int y, int z) {
		return new BlockLocation(world, world, x, y, z, true);
	}
	/** Returns a block location from the tile entity's location. */
	public static BlockLocation get(TileEntity tileEntity) {
		return get(tileEntity.getWorldObj(), tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
	}
	
	// Relative instantiation methods
	
	/** Returns a block location with its location set relative to this one. */
	public BlockLocation relative(int x, int y, int z) {
		return new BlockLocation(blockAccess, world, this.x + x, this.y + y, this.z + z, isWorld);
	}
	
	/** Returns a block location for the block in this direction, this many blocks away. */
	public BlockLocation offset(ForgeDirection direction, int distance) {
		return relative(direction.offsetX * distance,
		                direction.offsetY * distance,
		                direction.offsetZ * distance);
	}
	
	/** Returns a block location for the neighbor block in this direction. */
	public BlockLocation neighbor(ForgeDirection direction) {
		return relative(direction.offsetX, direction.offsetY, direction.offsetZ);
	}
	
	/** Returns a block location for the block to the west (-X). */
	public BlockLocation west() { return neighbor(ForgeDirection.WEST); }
	/** Returns a block location for the block to the east (+X). */
	public BlockLocation east() { return neighbor(ForgeDirection.EAST); }
	/** Returns a block location for the block below (-Y). */
	public BlockLocation below() { return neighbor(ForgeDirection.DOWN); }
	/** Returns a block location for the block above (+Y). */
	public BlockLocation above() { return neighbor(ForgeDirection.UP); }
	/** Returns a block location for the block to the north (-Z). */
	public BlockLocation north() { return neighbor(ForgeDirection.NORTH); }
	/** Returns a block location for the block to the south (+Z). */
	public BlockLocation south() { return neighbor(ForgeDirection.SOUTH); }
	
	// Getting and setting
	
	/** Gets the block at this location. */
	public Block getBlock() { return blockAccess.getBlock(x, y, z); }
	/** Gets the metadata for the block at this location. */
	public int getMetadata() { return blockAccess.getBlockMetadata(x, y, z); }
	/** Gets the tile entity of the block at this location. */
	public TileEntity getTileEntity() { return blockAccess.getTileEntity(x, y, z); }
	
	/** Gets the tile entity of the block at this location.
	 *  Returns null if the tile entity is not the correct type. */
	public <T extends TileEntity> T getTileEntity(Class<T> tileEntityClass) {
		TileEntity tileEntity = getTileEntity();
		return (tileEntityClass.isInstance(tileEntity) ? (T)tileEntity : null);
	}
	
	/** Gets the tile entity of the block at this location.
	 *  Throws an error if there is no tile entity or it's not the correct type. */
	public <T extends TileEntity> T getTileEntityStrict(Class<T> tileEntityClass) {
		TileEntity tileEntity = getTileEntity();
		if (tileEntity == null)
			throw new Error(String.format("Expected tile entity at %s, but none found.", this));
		if (!tileEntityClass.isInstance(tileEntity))
			throw new Error(String.format("Expected tile entity at %s to be '%s', but found '%s' instead.",
			                              this, tileEntityClass.getName(), tileEntity.getClass().getName()));
		return (T)tileEntity;
	}
	
	/** Sets the block at this location. */
	public void setBlock(Block block) {
		if (isWorld) world.setBlock(x, y, z, block);
	}
	/** Sets the metadata for the block at this location. */
	public void setMetadata(int metadata) {
		if (isWorld) world.setBlockMetadataWithNotify(x, y, z, metadata, SetBlockFlag.DEFAULT);
	}
	/** Sets the block and its metadata at this location. */
	public void setBlockAndMetadata(Block block, int metadata) {
		if (isWorld) world.setBlock(x, y, z, block, metadata, SetBlockFlag.DEFAULT);
	}
	
	/** Sets the tile entity for the block at this location. <br>
	 *  <b>Warning:</b> This is usually done automatically.
	 *                  Only use this when you know what you're doing! */
	public void setTileEntity(TileEntity tileEntity) {
		if (isWorld) world.setTileEntity(x, y, z, tileEntity);
	}
	
	// Additional block related methods
	
	/** Returns if the block is air. */
	public boolean isAir() {
		return blockAccess.isAirBlock(x, y, z);
	}
	/** Returns if the block is replaceable, like tall grass or fluids. */
	public boolean isReplaceable() {
		return getBlock().isReplaceable(blockAccess, x, y, z);
	}
	/** Returns if the block is solid on that side. */
	public boolean isSideSolid(ForgeDirection side) {
		return blockAccess.isSideSolid(x, y, z, side, false);
	}
	
	// Additional tile entity related methods
	
	
	
	// Equals, hashCode and toString
	
	@Override
	public boolean equals(Object obj) {
		BlockLocation loc;
		return ((obj instanceof BlockLocation) &&
		        (blockAccess == (loc = (BlockLocation)obj).blockAccess) &&
		        (x == loc.x) && (y == loc.y) && (z == loc.z));
	}
	
	@Override
	public int hashCode() {
		return (blockAccess.hashCode() ^ x ^ (z << 4) ^ (y << 8)); 
	}
	
	@Override
	public String toString() {
		return String.format("[%s; Coords=%s,%s,%s]", getWorldName(), x, y, z);
	}
	
	// Helper functions
	
	private String getWorldName() {
		return (isWorld ? ("DIM=" + Integer.toString(world.provider.dimensionId))
		                : blockAccess.toString());
	}
	
}