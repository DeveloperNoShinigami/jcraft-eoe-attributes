package net.arna.jcraft.common.advancements;

import com.google.gson.JsonObject;
import net.arna.jcraft.JCraft;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class Hamon3Trigger extends SimpleCriterionTrigger<Hamon3Trigger.TriggerInstance> {

    public final static ResourceLocation ID = JCraft.id("hamon3");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    protected TriggerInstance createInstance(final JsonObject json, final ContextAwarePredicate predicate, final DeserializationContext deserializationContext) {
        return new TriggerInstance(predicate);
    }

    public void trigger(final ServerPlayer player) {
        this.trigger(player, obj -> true);
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {

        public TriggerInstance(final ContextAwarePredicate player) {
            super(Hamon3Trigger.ID, player);
        }

        public static TriggerInstance trigger() {
            return new TriggerInstance(ContextAwarePredicate.ANY);
        }

    }
}
