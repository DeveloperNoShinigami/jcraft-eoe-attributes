package net.arna.jcraft.common.ai.brain;

import net.arna.jcraft.api.stand.StandEntity;
import net.arna.jcraft.common.ai.AttackerBrainInfo;
import net.arna.jcraft.common.ai.CombatEntityContext;
import net.arna.jcraft.common.ai.CombatInstantContext;
import net.arna.jcraft.common.ai.IJAttackerBrain;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

public interface StandAttackerBrain extends IJAttackerBrain {
    static void tick(Mob mob, AttackerBrainInfo info) {
        // TODO: generalize more
        final LivingEntity target = mob.getTarget();
        if (target == null) return;

        final CombatInstantContext combatCtx = info.getCombatCtx();
        combatCtx.setAttackerCtx(CombatEntityContext.from(mob));
        combatCtx.setTargetCtx(CombatEntityContext.from(target));

        final int aiLevel = info.getAiLevel();

        plan(aiLevel, info, combatCtx);
    }

    static void plan(final int aiLevel, final AttackerBrainInfo info, final CombatInstantContext combatCtx) {
        if (IJAttackerBrain.planDefense(info)) return;
        final StandEntity<?, ?> stand = combatCtx.getAttackerCtx().stand();
        if (stand == null) return;
        stand.plan(aiLevel, info, combatCtx);
    }
}