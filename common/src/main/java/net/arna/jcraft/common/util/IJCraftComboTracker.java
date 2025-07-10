package net.arna.jcraft.common.util;

import net.arna.jcraft.api.MoveUsage;
import net.minecraft.world.entity.LivingEntity;

public interface IJCraftComboTracker {
    float jcraft$getDamageScaling();

    int jcraft$getHitCount();

    boolean jcraft$addMoveToCombo(LivingEntity attacker, MoveUsage moveUsage);

    void jcraft$increaseHitCount();

    void jcraft$resetCombo();
}
