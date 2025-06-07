package net.arna.jcraft.common.item;

import dev.architectury.registry.registries.RegistrySupplier;
import net.arna.jcraft.api.spec.SpecType2;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BoxingGlovesItem extends SpecObtainmentItem {
    public BoxingGlovesItem(Properties settings, RegistrySupplier<SpecType2> spec) {
        super(settings, spec);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level world, List<Component> tooltip, @NotNull TooltipFlag context) {
        tooltip.add(Component.translatable("jcraft.boxinggloves.desc"));
        super.appendHoverText(stack, world, tooltip, context);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level world, Player user, @NotNull InteractionHand hand) {
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
