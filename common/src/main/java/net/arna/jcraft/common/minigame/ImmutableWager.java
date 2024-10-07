package net.arna.jcraft.common.minigame;

import lombok.NonNull;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.List;

public final class ImmutableWager extends AbstractWager {

    public static final ImmutableWager EMPTY = new ImmutableWager();

    private ImmutableWager() {
        /* Singleton constructor. */
    }

    /**
     * Creates an immutable copy of the given wager.
     */
    public ImmutableWager(@NonNull final AbstractWager wager) {
        for (final ItemStack stack : wager.items) {
            items.add(stack.copy());
        }
    }

    /**
     * Returns an immutable view of the item stacks wagered.
     */
    public @NonNull List<ItemStack> getItemWager() {
        return Collections.unmodifiableList(items);
    }

}
