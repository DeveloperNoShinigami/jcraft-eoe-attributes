package net.arna.jcraft.common.item;

import net.arna.jcraft.common.spec.SpecType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BoxingGlovesItem extends SpecObtainmentItem {
    public BoxingGlovesItem(Properties settings, SpecType spec) {
        super(settings, spec);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        tooltip.add(Component.translatable("jcraft.boxinggloves.desc"));
        super.appendHoverText(stack, world, tooltip, context);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);

        if (!world.isClientSide) {
            boolean specChanged = tryGetSpec(user);
            if (specChanged) {
                if (!user.isCreative()) {
                    itemStack.shrink(1);
                }
                user.getCooldowns().addCooldown(this, 20);
            }
        }

        return InteractionResultHolder.consume(itemStack);
    }
}
