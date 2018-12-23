package pcl.opensecurity.common.protection;

import net.minecraft.entity.Entity;

public interface IProtection {
    boolean isProtected(Entity entityIn, Protection.UserAction action);
}
