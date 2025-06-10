package net.arna.jcraft.common.advancements;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.api.JRegistries;
import net.arna.jcraft.api.stand.StandType;
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

public class ObtainedStandTrigger extends SimpleCriterionTrigger<ObtainedStandTrigger.TriggerInstance> {

    public final static ResourceLocation ID = JCraft.id("obtained_stand");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    protected TriggerInstance createInstance(final JsonObject json, final ContextAwarePredicate predicate, final DeserializationContext deserializationContext) {
        StandType type = null;
        if (json.has("stand")) {
            final ResourceLocation resourceLocation = new ResourceLocation(GsonHelper.getAsString(json, "stand"));
            type = JRegistries.STAND_TYPE_REGISTRY.get(resourceLocation);
            if (type == null) {
                throw new JsonSyntaxException("Unknown stand '" + resourceLocation + "'");
            }
        }
        return new TriggerInstance(predicate, type);
    }

    public void trigger(final ServerPlayer player, final @NotNull StandType type) {
        this.trigger(player, trigger -> trigger.matches(type));
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        @Nullable
        private final StandType type;

        public TriggerInstance(final ContextAwarePredicate player, final @Nullable StandType type) {
            super(ObtainedStandTrigger.ID, player);
            this.type = type;
        }

        public static TriggerInstance obtainedStand(final @Nullable StandType type) {
            return new TriggerInstance(ContextAwarePredicate.ANY, type);
        }

        public static TriggerInstance obtainedStand() {
            return obtainedStand(null);
        }

        public boolean matches(final @Nullable StandType type) {
            return this.type == null || this.type == type;
        }

        @NotNull
        @Override
        public JsonObject serializeToJson(final @NotNull SerializationContext context) {
            final JsonObject jsonObject = super.serializeToJson(context);
            if (this.type != null) {
                jsonObject.addProperty("stand", JRegistries.STAND_TYPE_REGISTRY.getId(type).toString());
            }
            return jsonObject;
        }
    }
}
