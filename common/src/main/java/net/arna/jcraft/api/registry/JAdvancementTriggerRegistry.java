package net.arna.jcraft.api.registry;

import net.arna.jcraft.common.advancements.Hamon1Trigger;
import net.arna.jcraft.common.advancements.ObtainedSpecTrigger;
import net.arna.jcraft.common.advancements.ObtainedStandTrigger;
import net.minecraft.advancements.CriteriaTriggers;

public interface JAdvancementTriggerRegistry {

    ObtainedStandTrigger OBTAINED_STAND = CriteriaTriggers.register(new ObtainedStandTrigger());
    ObtainedSpecTrigger OBTAINED_SPEC = CriteriaTriggers.register(new ObtainedSpecTrigger());
    Hamon1Trigger HAMON1 = CriteriaTriggers.register(new Hamon1Trigger());

    static void init() {
        /* empty on purpose */
    }
}
