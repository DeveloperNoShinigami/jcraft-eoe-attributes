package net.arna.jcraft.common.ai.brain;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.api.stand.StandEntity;
import net.arna.jcraft.common.ai.AttackerBrainInfo;
import net.arna.jcraft.common.ai.CombatInstantContext;
import net.arna.jcraft.common.ai.IJAttackerBrain;
import net.arna.jcraft.common.util.JUtils;
import net.minecraft.world.entity.Mob;

public interface StandAttackerBrain extends IJAttackerBrain {
    static void tick(Mob mob, AttackerBrainInfo info) {
        final CombatInstantContext combatCtx = IJAttackerBrain.target(mob, info);
        if (combatCtx == null) return;

        StandEntity<?, ?> stand = JUtils.getStand(mob);
        if (stand == null) stand = JCraft.summon(mob);

        final int aiLevel = info.getAiLevel();

        info.setReactionTime(IJAttackerBrain.reactionTimeFor(aiLevel, mob.getRandom()));

        plan(aiLevel, info, combatCtx);
        stand.executePlan(aiLevel, info, combatCtx);
    }

    static void plan(final int aiLevel, final AttackerBrainInfo info, final CombatInstantContext combatCtx) {
        IJAttackerBrain.planDefense(info);
        final StandEntity<?, ?> stand = combatCtx.getAttackerCtx().stand();
        if (stand == null) return;
        stand.plan(aiLevel, info, combatCtx);
    }
}