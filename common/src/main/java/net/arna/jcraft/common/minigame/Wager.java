package net.arna.jcraft.common.minigame;

import lombok.NonNull;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Wrapper for things that can be used as wagers in bets, games etc.
 */
public class Wager extends AbstractWager {

    public Wager() {
        // empty constructor
    }

    /**
     * (Deep) copy constructor.
     */
    public Wager(@NonNull final AbstractWager wager) {
        for (final ItemStack stack : wager.items) {
            items.add(stack.copy());
        }
    }

    /**
     * Wagers another stack of items.
     */
    public void increaseWager(@NonNull final ItemStack increase) {
        items.add(Objects.requireNonNull(increase));
    }

    public void sort() {
        items.sort(ITEM_STACK_COMPARATOR);
    }

    public void readFromNbt(@NonNull CompoundTag tag) {
        items.clear();
        for (final Tag itemTag : tag.getList("wager_items", Tag.TAG_COMPOUND)) {
            items.add(ItemStack.of((CompoundTag)itemTag));
        }
    }

    public static Wager sum(@NonNull final Collection<? extends AbstractWager> coll) {
        final Wager result = new Wager();
        for (final AbstractWager wager : coll) {
            for (final ItemStack stack : wager.items) {
                result.increaseWager(stack);
            }
        }
        return result;
    }

    public static Wager sum(@NonNull final AbstractWager wager1, @NonNull final  AbstractWager wager2) {
        return sum(List.of(wager1, wager2));
    }

    /**
     * Splits the specified wager into equal parts. Some of the initial wager may get lost due to rounding down.
     */
    public static ImmutableWager split(@NonNull final AbstractWager wager, final int divisor) {
        if (divisor <= 0) {
            throw new IllegalArgumentException("Divisor must be positive!");
        }
        if (divisor == 1) {
            return new ImmutableWager(wager);
        }
        final Wager split = new Wager();
        for (final ItemStack stack : wager.items) {
            split.increaseWager(stack.copyWithCount(stack.getCount() / divisor));
        }
        return new ImmutableWager(split);
    }
}
