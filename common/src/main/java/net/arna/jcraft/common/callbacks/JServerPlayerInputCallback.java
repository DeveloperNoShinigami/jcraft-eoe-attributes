package net.arna.jcraft.common.callbacks;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.event.EventResult;
import net.arna.jcraft.common.attack.core.MoveInputType;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Callback called when a player inputs a move.
 */
public interface JServerPlayerInputCallback {
    Event<JServerPlayerInputCallback> EVENT = EventFactory.createEventResult();

    /**
     * Called when a player inputs a move.
     * @param player The player that input the move
     * @param moveInput The move the player input
     * @param pressed Whether the move was pressed or released
     * @param moveSuccess Whether the move was successful
     */
    EventResult onPlayerInput(ServerPlayerEntity player, MoveInputType moveInput, boolean pressed, boolean moveSuccess);
}
