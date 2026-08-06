package pcl.opensecurity.blockentity;

import pcl.opensecurity.OpenSecurity;

import li.cil.oc.api.Network;
import li.cil.oc.api.UnrecoverablePersistanceException;
import li.cil.oc.api.network.Connector;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.BlockEntityEnvironment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class SecurityBlockEntity extends BlockEntityEnvironment {
    private static final String OC_NODE_TAG = "oc:node";

    protected SecurityBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, String componentName, double buffer) {
        super(type, pos, state);
        node = Network.newNode(this, Visibility.Network)
                .withComponent(componentName, Visibility.Network)
                .withConnector(buffer).create();
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        if (node != null && node.host() == this && tag.contains(OC_NODE_TAG, Tag.TAG_COMPOUND)) {
            try {
                node.loadData(tag.getCompound(OC_NODE_TAG), provider);
            } catch (UnrecoverablePersistanceException exception) {
                OpenSecurity.LOGGER.warn("Could not restore the OpenComputers component address at {}", worldPosition, exception);
            }
        }
    }

    protected boolean consumeEnergy(double amount) {
        return node instanceof Connector connector && connector.tryChangeBuffer(-amount);
    }
}
