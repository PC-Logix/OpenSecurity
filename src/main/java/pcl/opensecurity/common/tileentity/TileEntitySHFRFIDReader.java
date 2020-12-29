package pcl.opensecurity.common.tileentity;

import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.EnvironmentHost;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.common.entity.Drone;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.common.items.ItemCard;
import pcl.opensecurity.common.items.ItemRFIDCard;

import java.util.ArrayList;
import java.util.HashMap;

//import net.minecraft.client.audio.SoundCategory;

public class TileEntitySHFRFIDReader extends TileEntityOSBase {
	public String data;
	public String eventName = "magData";

	public TileEntitySHFRFIDReader() {
		super("os_rfidreader");
		node = Network.newNode(this, Visibility.Network).withComponent(getComponentName()).withConnector(32).create();
	}

	public TileEntitySHFRFIDReader(EnvironmentHost host){
		super("os_rfidreader", host);
	}
	
	// Thanks gamax92 from #oc for the following 2 methods...
	private HashMap<String, Object> info(Entity entity, String data, boolean locked) {
		HashMap<String, Object> value = new HashMap<String, Object>();

		double rangeToEntity = entity.getDistance(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ());

		String name;
		if (entity instanceof EntityPlayerMP)
			name = ((EntityPlayer) entity).getDisplayNameString();
		else
			name = entity.getName();
		node.sendToReachable("computer.signal", "rfidData", name, rangeToEntity, data);
		value.put("name", name);
		value.put("range", rangeToEntity);
		value.put("data", data);
		value.put("locked", locked);

		return value;
	}

	static class RFIDCard{
		ItemCard.CardTag tag;

		public RFIDCard(ItemStack st){
			if (!(st.getItem() instanceof ItemRFIDCard))
				return;

			tag = new ItemCard.CardTag(st);
		}
	}

	ArrayList<RFIDCard> scanInventory(IInventory inv){
		ArrayList<RFIDCard> cards = new ArrayList<>();
		for (int k = 0; k < inv.getSizeInventory(); k++) {
			ItemStack st = inv.getStackInSlot(k);

			if (st.getItem() instanceof ItemRFIDCard)
				cards.add(new RFIDCard(st));
		}

		return cards;
	}

	@SuppressWarnings({ "rawtypes" })
	private HashMap<Integer, HashMap<String, Object>> scan(int range) {
		//getWorld().setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, 1, 3);
		//Block block = getWorld().getBlock(this.xCoord, this.yCoord, this.zCoord);
		//getWorld().scheduleBlockUpdate(this.xCoord, this.yCoord, this.zCoord, block, 20);
		HashMap<Integer, HashMap<String, Object>> output = new HashMap<Integer, HashMap<String, Object>>();
		int index = 1;
		for(Entity entity : this.getWorld().getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(this.getPos().add(-range, -range, -range), this.getPos().add(range+1, range+1, range+1)))){
			if (entity instanceof EntityPlayer) {
				for(RFIDCard card : scanInventory(((EntityPlayer) entity).inventory))
					if(card.tag.isValid)
						output.put(index++, info(entity, card.tag.dataTag, card.tag.locked));
			}
			else if (entity instanceof Drone) {
				for(RFIDCard card : scanInventory(((Drone) entity).mainInventory()))
					if(card.tag.isValid)
						output.put(index++, info(entity, card.tag.dataTag, card.tag.locked));
			}

			ItemCard.CardTag entityTag = new ItemCard.CardTag(entity.getEntityData().getCompoundTag("rfidData"));
			if(entityTag.isValid)
				output.put(index++, info(entity, entityTag.dataTag, entityTag.locked));

		}

		//if (found) {
		//	getWorld().setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, 2, 3);
		//} else {
		//	getWorld().setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, 3, 3);
		//}

		return output;
	}

	@Callback(doc = "function(String:name):boolean; Sets the name of the event that gets sent when a card is swipped", direct = true)
	public Object[] setEventName(Context context, Arguments args) {
		eventName = args.checkString(0);
		return new Object[]{ true };
	}
	
	@Callback(doc = "function(optional:int:range):string; pushes a signal \"rfidData\" for each found rfid on all players in range, optional set range.")
	public Object[] scan(Context context, Arguments args) {
		int range = Math.min(OpenSecurity.rfidRange, args.optInteger(0, OpenSecurity.rfidRange));

		if (node.changeBuffer(-5 * range) == 0) {
        	return new Object[]{ scan(range) };
		} else {
			return new Object[] { false, "Not enough power in OC Network." };
		}
	}

    public boolean canInteractWith(EntityPlayer playerIn) {
        // If we are too far away from this tile entity you cannot use it
        return !isInvalid() && playerIn.getDistanceSq(pos.add(0.5D, 0.5D, 0.5D)) <= 64D;
    }
}
