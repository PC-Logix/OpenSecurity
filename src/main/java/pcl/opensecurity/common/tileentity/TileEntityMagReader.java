package pcl.opensecurity.common.tileentity;

import javax.annotation.Nullable;

import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Visibility;
import net.minecraft.block.state.IBlockState;
//import net.minecraft.client.audio.SoundCategory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.common.SoundHandler;
import pcl.opensecurity.common.items.ItemMagCard;

public class TileEntityMagReader extends TileEntityOSBase {

	public String data;
	public String eventName = "magData";
	
	public TileEntityMagReader() {
		node = Network.newNode(this, Visibility.Network).withComponent(getComponentName()).withConnector(32).create();
	}

	private static String getComponentName() {
		return "os_magreader";
	}
	
	public boolean doRead(ItemStack itemStack, EntityPlayer em, int side) {
		if (itemStack != null && itemStack.getItem() instanceof ItemMagCard /*&& this.blockMetadata == 0*/) {
			if(!world.isRemote){
				//worldObj.playSoundEffect(this.xCoord + 0.5D, this.yCoord + 0.5D,  this.zCoord + 0.5D, "opensecurity:card_swipe", 0.5F, worldObj.rand.nextFloat() * 0.1F + 0.9F);
			}
        	world.playSound(null, this.pos.getX() + 0.5F, this.pos.getY() + 0.5F, this.pos.getZ() + 0.5F, SoundHandler.card_swipe, SoundCategory.BLOCKS, 15 / 15 + 0.5F, 1.0F);
		}
		if (itemStack != null && itemStack.getItem() instanceof ItemMagCard && itemStack.getTagCompound() != null && itemStack.getTagCompound().hasKey("data")) {
			data = itemStack.getTagCompound().getString("data");
			String uuid = itemStack.getTagCompound().getString("uuid");
			String user;
			boolean locked = itemStack.getTagCompound().getBoolean("locked");
			if (node.changeBuffer(-5) == 0) {
				String localUUID;
				if (!OpenSecurity.ignoreUUIDs) {
					localUUID = uuid;
				} else {
					localUUID = "-1";
				}
				if (OpenSecurity.cfg.magCardDisplayName) {
					user = em.getDisplayNameString();
				} else {
					user = "player";
				}
				node.sendToReachable("computer.signal", eventName, user, data, localUUID, locked, side);
			}
			getUpdateTag();
			world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 2);
			world.markBlockRangeForRenderUpdate(getPos(), getPos());
			markDirty();
			return true;
		} else {
			getUpdateTag();
			world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 2);
			world.markBlockRangeForRenderUpdate(getPos(), getPos());
			markDirty();
			return false;
		}
	}

	@Callback(doc = "function(String:name):boolean; Sets the name of the event that gets sent when a card is swipped", direct = true)
	public Object[] setEventName(Context context, Arguments args) throws Exception {
		eventName = args.checkString(0);
		return new Object[]{ true };
	}
	
	@Override
	@Nullable
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound tagCom = super.getUpdateTag();
		this.writeToNBT(tagCom);
		return tagCom;
	}

	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		this.readFromNBT(tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
			readFromNBT(packet.getNbtCompound());
			IBlockState state = this.world.getBlockState(this.pos);
			this.world.notifyBlockUpdate(pos, state, state, 3);
	}
	
	public boolean writeNBTToDescriptionPacket()
	{
		return true;
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState)
	{
		return (oldState.getBlock() != newState.getBlock());
	}
}
