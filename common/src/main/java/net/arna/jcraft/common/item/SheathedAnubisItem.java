package net.arna.jcraft.common.item;

import dev.architectury.registry.registries.RegistrySupplier;
import net.arna.jcraft.api.spec.SpecType2;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.registry.JItemRegistry;
import net.arna.jcraft.registry.JSoundRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SheathedAnubisItem extends SpecObtainmentItem {
    public SheathedAnubisItem(Properties settings, RegistrySupplier<SpecType2> spec) {
        super(settings, spec);
    }

    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.BLOCK;
    }

    public int getUseDuration(@NotNull ItemStack stack) {
        return 72000;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level world, List<Component> tooltip, @NotNull TooltipFlag context) {
        tooltip.add(Component.translatable("jcraft.anubis.namedesc"));
        tooltip.add(Component.translatable("jcraft.anubis.bloodthirstdesc"));
        tooltip.add(Component.translatable("jcraft.anubis.removaldesc"));
        tooltip.add(Component.translatable("jcraft.sheathedanubis.blockdesc"));
        tooltip.add(Component.translatable("jcraft.sheathedanubis.desc"));
        super.appendHoverText(stack, world, tooltip, context); // Doubles the previous Anubis description
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level world, Player user, @NotNull InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);

        if (user.isShiftKeyDown()) { // Block
            user.startUsingItem(hand);
        } else { // Unsheathe
            if (world.isClientSide) {
                return InteractionResultHolder.fail(itemStack);
            }
            StandEntity<?, ?> stand = JUtils.getStand(user);
            if (stand != null && stand.blocking) {
                return InteractionResultHolder.fail(itemStack);
            }
            ServerLevel serverWorld = (ServerLevel) world;

            boolean specChanged = tryGetSpec(user);
            if (specChanged) {
                JUtils.serverPlaySound(JSoundRegistry.ANUBIS_UNSHEATHE.get(), serverWorld, user.position());
                JUtils.serverPlaySound(JSoundRegistry.ANUBIS_SPECCHANGE.get(), serverWorld, user.position());
                user.setItemInHand(hand, new ItemStack(JItemRegistry.ANUBIS.get()));
            } else if (!warned) {
                JUtils.serverPlaySound(JSoundRegistry.ANUBIS_UNSHEATHE.get(), serverWorld, user.position());
                user.setItemInHand(hand, new ItemStack(JItemRegistry.ANUBIS.get()));
            }
        }

        return InteractionResultHolder.consume(itemStack);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level world, @NotNull Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if (world.isClientSide) {
            return;
        }
        if (entity instanceof Player player) // Bloodlust
        {
            AnubisItem.handleAnubisEffects(player.getLastHurtMobTimestamp() - player.tickCount, player);
        }
    }
}
