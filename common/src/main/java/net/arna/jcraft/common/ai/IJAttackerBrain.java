package net.arna.jcraft.common.ai;

import net.arna.jcraft.api.attack.enums.StunType;
import net.minecraft.world.effect.MobEffectInstance;

public interface IJAttackerBrain {
    // TODO: registry for mapping IJAttackerBrain implementations to Stand/Spec Types

    /**
     * State machine control for planning defense.
     * @return Whether the plan() should short-circuit
     */
    static boolean planDefense(final AttackerBrainInfo info) {
        final MobEffectInstance stun = info.getCombatCtx().getAttackerCtx().stun();
        if (stun == null) return false;

        if (stun.getAmplifier() == StunType.BLOCK.ordinal())
            info.setState(AttackerBrainInfo.State.DEFENSE);
            else info.setState(AttackerBrainInfo.State.COMBOED);

        return true;
    }
}
