package net.arna.jcraft.common.attack.moves.dirtydeedsdonedirtcheap;

import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.BooleanMoveVariable;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.core.ctx.MoveVariable;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.entity.stand.D4CEntity;
import net.arna.jcraft.common.item.MockItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import java.util.List;
import java.util.Set;

public final class ItemPlaceMove extends AbstractMove<ItemPlaceMove, D4CEntity> {
    public static final BooleanMoveVariable PLACING_FIRST_STACK = new BooleanMoveVariable();
    public static final MoveVariable<ItemStack> PLACING = new MoveVariable<>(ItemStack.class);
    private static final List<ItemStack> placeableStacks = List.of(
            Items.STICK.getDefaultInstance(),
            Items.COBBLESTONE.getDefaultInstance(),
            Items.DEAD_BUSH.getDefaultInstance(),
            Items.APPLE.getDefaultInstance(),
            Items.OAK_SAPLING.getDefaultInstance()
    );

    public ItemPlaceMove(final int cooldown, final int windup, final int duration, final float moveDistance) {
        super(cooldown, windup, duration, moveDistance);
    }

    @Override
    public void onInitiate(final D4CEntity attacker) {
        super.onInitiate(attacker);

        final MoveContext ctx = attacker.getMoveContext();
        final boolean placingFirstStack = ctx.getBoolean(PLACING_FIRST_STACK);
        if (placingFirstStack) {
            ctx.set(PLACING, placeableStacks.get(attacker.getRandom().nextInt(placeableStacks.size())));
        }

        attacker.setItemSlot(EquipmentSlot.OFFHAND, ctx.get(PLACING).copy());
        ctx.setBoolean(PLACING_FIRST_STACK, !placingFirstStack);
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final D4CEntity attacker, final LivingEntity user, final MoveContext ctx) {
        final ItemStack offHandStack = attacker.getOffhandItem();

        final ItemEntity item = new ItemEntity(attacker.level(), attacker.getX(), attacker.getY() + 0.2, attacker.getZ(),
                MockItem.createMockStack(ctx.get(PLACING)), 0, 0, 0);
        item.setPickUpDelay(200);
        attacker.level().addFreshEntity(item);

        // Remove item from D4C's hand
        offHandStack.shrink(1);

        return Set.of();
    }

    @Override
    public void registerContextEntries(final MoveContext ctx) {
        ctx.register(PLACING_FIRST_STACK, true);
        ctx.register(PLACING);
    }

    @Override
    protected @NonNull ItemPlaceMove getThis() {
        return this;
    }

    @Override
    public @NonNull ItemPlaceMove copy() {
        return copyExtras(new ItemPlaceMove(getCooldown(), getWindup(), getDuration(), getMoveDistance()));
    }
}
