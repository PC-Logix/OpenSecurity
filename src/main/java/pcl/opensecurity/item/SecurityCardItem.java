package pcl.opensecurity.item;

import pcl.opensecurity.data.CardData;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public final class SecurityCardItem extends Item {
    public enum Kind { RFID, MAG }
    private final Kind kind;

    public SecurityCardItem(Kind kind, Properties properties) {
        super(properties);
        this.kind = kind;
    }

    public Kind kind() {
        return kind;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (kind != Kind.RFID) return InteractionResult.PASS;
        CardData card = CardData.read(stack);
        if (!card.valid()) return InteractionResult.PASS;
        if (!player.level().isClientSide) {
            target.getPersistentData().put("rfidData", card.write(new net.minecraft.nbt.CompoundTag()));
            if (!player.getAbilities().instabuild) stack.shrink(1);
        }
        return InteractionResult.sidedSuccess(player.level().isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        CardData card = CardData.read(stack);
        if (card.valid()) tooltip.add(Component.literal(card.data()).withStyle(ChatFormatting.GRAY));
        if (card.locked()) tooltip.add(Component.translatable("tooltip.opensecurity.card.locked").withStyle(ChatFormatting.RED));
        super.appendHoverText(stack, context, tooltip, flag);
    }
}
