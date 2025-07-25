package net.arna.jcraft.common.ai.brain;

import net.arna.jcraft.api.spec.JSpec;
import net.arna.jcraft.common.ai.AttackerBrainInfo;
import net.arna.jcraft.common.ai.CombatInstantContext;
import net.arna.jcraft.common.ai.IJAttackerBrain;
import net.minecraft.world.entity.Mob;

public interface SpecAttackerBrain extends IJAttackerBrain {
    static void tick(Mob mob, AttackerBrainInfo info) {
//TODO: impl
    }

    static void plan(final int aiLevel, final AttackerBrainInfo info, final CombatInstantContext combatCtx) {
        IJAttackerBrain.planDefense(info);
        final JSpec<?, ?> spec = combatCtx.getAttackerCtx().spec();
        if (spec == null) return;
        spec.plan(aiLevel, info, combatCtx);
    }
}