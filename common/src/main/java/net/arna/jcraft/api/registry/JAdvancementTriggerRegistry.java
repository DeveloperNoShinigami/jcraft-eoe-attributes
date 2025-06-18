package net.arna.jcraft.api.registry;

import net.arna.jcraft.common.advancements.ObtainedSpecTrigger;
import net.arna.jcraft.common.advancements.ObtainedStandTrigger;
import net.minecraft.advancements.CriteriaTriggers;

public interface JAdvancementTriggerRegistry {

    ObtainedStandTrigger OBTAINED_STAND = CriteriaTriggers.register(new ObtainedStandTrigger());
    ObtainedSpecTrigger OBTAINED_SPEC = CriteriaTriggers.register(new ObtainedSpecTrigger());

    static void init() {
        /* empty on purpose */
    }
}
