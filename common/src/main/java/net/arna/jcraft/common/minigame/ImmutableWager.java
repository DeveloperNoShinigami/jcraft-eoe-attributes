package net.arna.jcraft.common.minigame;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

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
    public ImmutableWager(@NotNull final AbstractWager wager) {
        for (final ItemStack stack : wager.items) {
            items.add(stack.copy());
        }
    }

    /**
     * Returns an immutable view of the item stacks wagered.
     */
    public @NotNull List<ItemStack> getItemWager() {
        return Collections.unmodifiableList(items);
    }

}
