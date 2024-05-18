package net.arna.jcraft.common.minigame;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Wrapper for things that can be used as wagers in bets, games etc.
 */
public class Wager {

    private final List<ItemStack> items = new LinkedList<>();

    public Wager() {
        // empty constructor
    }

    /**
     * Wagers another stack of items.
     */
    public void increaseWager(@NotNull final ItemStack increase) {
        items.add(Objects.requireNonNull(increase));
    }

    /**
     * Returns an immutable view of the item stacks wagered.
     */
    public @NotNull List<ItemStack> getItemWager() {
        return Collections.unmodifiableList(items);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Wager wager)) return false;
        if (items.size() != wager.items.size()) return false;
        final Iterator<ItemStack> it1 = items.iterator();
        final Iterator<ItemStack> it2 = wager.items.iterator();
        while (it1.hasNext()) {
            if (!ItemStack.isSameItemSameTags(it1.next(), it2.next())) {
                return false;
            }
        }
        return true;
    }

    // this hash ignores the item tags, but that is fine
    @Override
    public int hashCode() {
        int hash = 43 + items.size();
        for (final ItemStack stack : items) {
            hash = 43 * hash + stack.getCount() * stack.getItem().hashCode();
        }
        return Objects.hash(items);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Wager{");
        sb.append("items=").append(items);
        sb.append('}');
        return sb.toString();
    }
}
