package net.arna.jcraft.common.minigame;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

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
    public Wager(@NotNull final AbstractWager wager) {
        for (final ItemStack stack : wager.items) {
            items.add(stack.copy());
        }
    }

    /**
     * Wagers another stack of items.
     */
    public void increaseWager(@NotNull final ItemStack increase) {
        items.add(Objects.requireNonNull(increase));
    }

    public void sort() {
        items.sort(ITEM_STACK_COMPARATOR);
    }

    public void readFromNbt(@NotNull CompoundTag tag) {
        for (final Tag itemNbt : tag.getList("wager_items", Tag.TAG_COMPOUND)) {
            items.add(ItemStack.of((CompoundTag)itemNbt));
        }
    }

    public void writeToNbt(@NotNull CompoundTag tag) {
        final ListTag itemsNbt = new ListTag();
        for (final ItemStack stack : items) {
            final CompoundTag itemNbt = new CompoundTag();
            stack.save(itemNbt);
            itemsNbt.add(itemNbt);
        }
        tag.put("wager_items", itemsNbt);
    }

    public static Wager sum(@NotNull final Collection<? extends AbstractWager> coll) {
        final Wager result = new Wager();
        for (final AbstractWager wager : coll) {
            for (final ItemStack stack : wager.items) {
                result.increaseWager(stack);
            }
        }
        return result;
    }

    public static Wager sum(@NotNull final AbstractWager wager1, @NotNull final  AbstractWager wager2) {
        return sum(List.of(wager1, wager2));
    }
}
