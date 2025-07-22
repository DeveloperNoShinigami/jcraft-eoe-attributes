package net.arna.jcraft.common.ai;

import lombok.Getter;
import lombok.Setter;

public class CombatInstantContext {
    @Getter @Setter
    private CombatEntityContext attackerCtx, targetCtx;

    public CombatInstantContext() {
    }
}
