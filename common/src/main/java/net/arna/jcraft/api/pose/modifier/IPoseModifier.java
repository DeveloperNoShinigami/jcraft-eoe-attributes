package net.arna.jcraft.api.pose.modifier;

import net.arna.jcraft.api.pose.ModelType;
import net.minecraft.client.model.Model;
import net.minecraft.world.entity.LivingEntity;

public interface IPoseModifier {
    IPoseModifier EMPTY = new IPoseModifier() {
        @Override
        public String getId() {
            return "empty";
        }

        @Override
        public void apply(Model model, LivingEntity user, float age) {
            // no op
        }
    };

    /**
     * Returns a unique identifier for this modifier.
     * This is used for serialization and identification purposes.
     *
     * @return A unique identifier string
     */
    String getId();

    /**
     * Checks whether this modifier can modify the given model.
     * @param modelType The type of the model to check compatibility with
     * @return true if the model is supported, false otherwise
     */
    default boolean isModelSupported(ModelType<?> modelType) {
        return true;
    }

    /**
     * Applies this modifier to the given model part.
     *
     * @param model The model to modify
     * @param user The entity using the model. Can be used for context
     * @param age The age of the entity in ticks. Can be used for animations or time-based effects
     */
    void apply(Model model, LivingEntity user, float age);
}
