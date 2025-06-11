package net.arna.jcraft.api.registry;

import net.arna.jcraft.common.advancements.ObtainedStandTrigger;
import net.minecraft.advancements.CriteriaTriggers;

public interface JAdvancementTriggerRegistry {

    ObtainedStandTrigger OBTAINED_STAND = CriteriaTriggers.register(new ObtainedStandTrigger());

    static void init() {
        /* empty on purpose */
    }
}
