package net.arna.jcraft.common.item;

import lombok.NonNull;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.api.component.player.CommonSpecComponent;
import net.arna.jcraft.api.registry.JItemRegistry;
import net.arna.jcraft.api.spec.SpecData;
import net.arna.jcraft.api.spec.SpecType;
import net.arna.jcraft.api.spec.SpecTypeUtil;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SpecDiscItem extends Item {

    private static final String SPEC_ID = "SpecID";

    public SpecDiscItem(Properties settings) {
        super(settings);
    }

    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.EAT;
    }

    @Override
    public @NonNull InteractionResultHolder<ItemStack> use(Level world, Player user, @NonNull InteractionHand hand) {
        final ItemStack itemStack = user.getItemInHand(hand);
        if (world.isClientSide) {
            return InteractionResultHolder.pass(itemStack);
        }

        if (JCraft.wasRecentlyAttacked(user.getCombatTracker())) {
            user.displayClientMessage(Component.translatable("jcraft.disc.combat"), true);
            return InteractionResultHolder.fail(itemStack);
        }

        // Get NBT
        SpecType itemSpec;

        final CompoundTag data = itemStack.getOrCreateTag();
        itemSpec = SpecTypeUtil.readFromNBT(data, SPEC_ID);

        final CommonSpecComponent specData = JComponentPlatformUtils.getSpecData(user);
        final SpecType userSpec = specData.getType();

        if (itemSpec == userSpec && itemSpec != null) {
            user.displayClientMessage(Component.translatable("jcraft.disc.same_spec"), true);
            return InteractionResultHolder.fail(itemStack);
        }

        specData.setType(itemSpec);
        if (userSpec == null) {
            data.remove(SPEC_ID);
        } else {
            data.putString(SPEC_ID, userSpec.getId().toString());
        }

        // 1s usage cooldown to prevent overuse
        user.getCooldowns().addCooldown(this, 20);

        return InteractionResultHolder.success(itemStack);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag context) {
        SpecType type = getSpecType(stack);
        if (type == null) {
            tooltip.add(Component.literal("Empty").withStyle(s -> s.applyFormat(ChatFormatting.GRAY)));
            return;
        }
        SpecData data = type.getData();
        tooltip.add(data.getName().copy().withStyle(s -> s.withColor(ChatFormatting.GRAY)));
    }

    public static ItemStack createDiscStack(SpecType type) {
        ItemStack stack = new ItemStack(JItemRegistry.SPEC_DISC.get());
        CompoundTag nbt = stack.getOrCreateTag();
        nbt.putString(SPEC_ID, type.getId().toString());
        return stack;
    }

    public static boolean isEmptyDisc(ItemStack stack) {
        return stack.getTag() == null || !stack.getTag().contains(SPEC_ID, Tag.TAG_INT);
    }

    public static SpecType getSpecType(ItemStack stack) {
        if (!stack.is(JItemRegistry.SPEC_DISC.get())) {
            return null;
        }

        CompoundTag nbt = stack.getTag();
        if (nbt == null || !nbt.contains(SPEC_ID)) {
            return null;
        }

        return SpecTypeUtil.readFromNBT(nbt, SPEC_ID);
    }
}
