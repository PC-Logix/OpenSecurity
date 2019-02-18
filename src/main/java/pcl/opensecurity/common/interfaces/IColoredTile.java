package pcl.opensecurity.common.interfaces;

import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;

import java.awt.*;

public interface IColoredTile {
    int getColor();
    void setColor(int color);
    void onColorChanged();

    default boolean isColoringItem(ItemStack stack) {
        return stack.getItem() instanceof ItemDye;
    }

    default int getColorFromStack(ItemStack stack){
        float[] vals = li.cil.oc.util.Color.dyeColor(stack).getColorComponentValues();
        return new Color(vals[0], vals[1], vals[2]).getRGB();
    }

    default boolean setColor(ItemStack stack){
        if(!isColoringItem(stack))
            return false;

        setColor(getColorFromStack(stack));
        return true;
    }
}
