package pcl.opensecurity.common.tileentity.logic;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import pcl.opensecurity.common.items.ItemCooldownUpgrade;
import pcl.opensecurity.common.items.ItemDamageUpgrade;
import pcl.opensecurity.common.items.ItemEnergyUpgrade;
import pcl.opensecurity.common.items.ItemMovementUpgrade;

public class EnergyTurretStats {
    private float damage, energyFactor, movePerTick;
    private int cooldownTicks;

    public EnergyTurretStats(){
        loadDefaults();
    }

    private void loadDefaults(){
        damage = 5F;
        cooldownTicks = 1;
        energyFactor = 1;
        movePerTick = 0.005F;
    }

    public void loadFromInventory(ItemStackHandler inventory){
        loadDefaults();
        for(int slot = 0; slot < inventory.getSlots(); slot++){
            ItemStack stack = inventory.getStackInSlot(slot);
            if(!stack.isEmpty())
                checkItem(stack.getItem());
        }
    }

    private void checkItem(Item item){
        if(item instanceof ItemDamageUpgrade)
            damage*= 3F;

        if(item instanceof ItemEnergyUpgrade)
            energyFactor*= 0.7;

        if(item instanceof ItemMovementUpgrade)
            movePerTick+= 2.5F;

        if(item instanceof ItemCooldownUpgrade)
            cooldownTicks+= 3;
    }

    public float getDamage(){
        return damage;
    }

    public float getEnergyUsage(){
        return 2 * getDamage() * energyFactor;
    }

    public int getCooldown(){
        return cooldownTicks;
    }

    public float getMoveSpeed(){
        return movePerTick;
    }
}