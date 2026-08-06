package pcl.opensecurity.item;

import net.minecraft.world.item.Item;

public final class TurretUpgradeItem extends Item {
    public enum Kind { DAMAGE, COOLDOWN, ENERGY, MOVEMENT }
    private final Kind kind;

    public TurretUpgradeItem(Kind kind, Properties properties) {
        super(properties);
        this.kind = kind;
    }

    public Kind kind() {
        return kind;
    }
}
