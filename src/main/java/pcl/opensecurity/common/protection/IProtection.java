package pcl.opensecurity.common.protection;
/**
 * @author ben_mkiv
 */
import net.minecraft.entity.Entity;

public interface IProtection {
    boolean isProtected(Entity entityIn, Protection.UserAction action);
}
