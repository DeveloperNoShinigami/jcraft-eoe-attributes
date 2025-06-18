package net.arna.jcraft.common.advancements;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.api.JRegistries;
import net.arna.jcraft.api.spec.SpecType;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ObtainedSpecTrigger extends SimpleCriterionTrigger<ObtainedSpecTrigger.TriggerInstance> {

    public final static ResourceLocation ID = JCraft.id("obtained_spec");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    protected TriggerInstance createInstance(final JsonObject json, final ContextAwarePredicate predicate, final DeserializationContext deserializationContext) {
        SpecType type = null;
        if (json.has("spec")) {
            final ResourceLocation resourceLocation = new ResourceLocation(GsonHelper.getAsString(json, "spec"));
            type = JRegistries.SPEC_TYPE_REGISTRY.get(resourceLocation);
            if (type == null) {
                throw new JsonSyntaxException("Unknown spec '" + resourceLocation + "'");
            }
        }
        return new TriggerInstance(predicate, type);
    }

    public void trigger(final ServerPlayer player, final @NotNull SpecType type) {
        this.trigger(player, trigger -> trigger.matches(type));
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        @Nullable
        private final SpecType type;

        public TriggerInstance(final ContextAwarePredicate player, final @Nullable SpecType type) {
            super(ObtainedSpecTrigger.ID, player);
            this.type = type;
        }

        public static TriggerInstance obtainedSpec(final @Nullable SpecType type) {
            return new TriggerInstance(ContextAwarePredicate.ANY, type);
        }

        public static TriggerInstance obtainedSpec() {
            return obtainedSpec(null);
        }

        public boolean matches(final @Nullable SpecType type) {
            return this.type == null || this.type == type;
        }

        @NotNull
        @Override
        public JsonObject serializeToJson(final @NotNull SerializationContext context) {
            final JsonObject jsonObject = super.serializeToJson(context);
            if (this.type != null) {
                jsonObject.addProperty("spec", JRegistries.SPEC_TYPE_REGISTRY.getId(type).toString());
            }
            return jsonObject;
        }
    }
}
