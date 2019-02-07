package pcl.opensecurity.util;

import java.util.UUID;

public interface IOwner {
    void setOwner(UUID uuid);
    UUID getOwner();
}
