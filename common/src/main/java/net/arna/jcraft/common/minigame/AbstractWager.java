package net.arna.jcraft.common.minigame;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public abstract class AbstractWager {

    private final static Comparator<ItemStack> STACK_COMPARATOR = Comparator.comparing(ItemStack::getCount).reversed();
    private final static Comparator<ItemStack> ITEM_COMPARATOR = Comparator.comparing(stack -> stack.getDisplayName().getString());
    protected final static Comparator<ItemStack> ITEM_STACK_COMPARATOR = ITEM_COMPARATOR.thenComparing(STACK_COMPARATOR);


    protected final List<ItemStack> items = new LinkedList<>();

    protected AbstractWager() {
        // empty constructor
    }

    /**
     * Returns <code>true</code> if this wager is an expansion of the given wager. Being an expansion means that
     * this wager contains at least all item stacks that the other wager has, in the same order.
     */
    public boolean expands(@NotNull final AbstractWager wager) {
        if (wager.items.size() > items.size()) {
            return false;
        }
        final Iterator<ItemStack> smallerIt = wager.items.iterator();
        final Iterator<ItemStack> biggerIt = items.iterator();
        while (smallerIt.hasNext()) {
            if (!ItemStack.isSameItemSameTags(smallerIt.next(), biggerIt.next())) {
                return false;
            }
        }
        return true;
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
        final StringBuilder sb = new StringBuilder(getClass().getSimpleName());
        sb.append("{items=").append(items);
        sb.append('}');
        return sb.toString();
    }

}
