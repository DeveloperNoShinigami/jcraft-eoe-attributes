package net.arna.jcraft.common.ai.brain;

import net.arna.jcraft.common.ai.AttackerBrainInfo;
import net.arna.jcraft.common.ai.CombatInstantContext;
import net.arna.jcraft.common.ai.IJAttackerBrain;
import net.minecraft.world.entity.Mob;

public interface StandSpecAttackerBrain extends IJAttackerBrain {
    static void tick(Mob mob, AttackerBrainInfo info) {

    }

    static void plan(final int aiLevel, final AttackerBrainInfo info, final CombatInstantContext combatCtx) {
        if (IJAttackerBrain.planDefense(info)) return;

    }
}