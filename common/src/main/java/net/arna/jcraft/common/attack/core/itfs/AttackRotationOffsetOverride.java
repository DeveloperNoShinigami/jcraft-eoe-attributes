package net.arna.jcraft.common.attack.core.itfs;

import net.arna.jcraft.common.entity.stand.StandEntity;

/**
 * Moves implementing this will be able to override the attack rotation offset
 * of the stand using this move.
 * More or less specific to Feign Barrage.
 */
public interface AttackRotationOffsetOverride {
    /**
     * @return the attack rotation offset to use for the stand when this move is used.
     */
    float getAttackRotationOffset(StandEntity<?, ?> attacker);
}
