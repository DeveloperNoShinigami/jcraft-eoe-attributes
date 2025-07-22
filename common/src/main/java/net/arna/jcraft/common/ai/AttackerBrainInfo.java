package net.arna.jcraft.common.ai;

import lombok.Getter;
import lombok.NonNull;

public class AttackerBrainInfo {
    public enum State {
        IDLE,
        // Approaching target
        APPROACH,
        // Hitting blocking target
        PRESSURE,
        // Actively hitting target
        COMBOING,
        // Repositioning, soft defense
        DISENGAGE,
        // Zoning
        KEEPAWAY,
        // Blocking
        DEFENSE,
        // Hit
        COMBOED,
    }

    @Getter
    private final CombatInstantContext combatCtx;
    @Getter
    private final int aiLevel;
    @Getter
    private State state = State.IDLE;

    public void setState(@NonNull State state) {
        this.state = state;
    }

    public AttackerBrainInfo(int aiLevel) {
        this.aiLevel = aiLevel;
        this.combatCtx = new CombatInstantContext();
    }
}
