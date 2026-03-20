package com.bluelotuscoding.util;

import net.minecraft.world.entity.LivingEntity;

import java.util.function.Supplier;

/**
 * Utility to provide player context for move description calculations.
 */
public class MoveContext {
    private static final ThreadLocal<LivingEntity> CURRENT_PLAYER = new ThreadLocal<>();
    private static Supplier<LivingEntity> clientPlayerSupplier = () -> null;

    public static void setClientPlayerSupplier(Supplier<LivingEntity> supplier) {
        clientPlayerSupplier = supplier;
    }

    public static void setPlayer(LivingEntity player) {
        CURRENT_PLAYER.set(player);
    }

    public static void clear() {
        CURRENT_PLAYER.remove();
    }

    public static LivingEntity getPlayer() {
        LivingEntity player = CURRENT_PLAYER.get();
        if (player == null) {
            player = clientPlayerSupplier.get();
        }
        return player;
    }
}
