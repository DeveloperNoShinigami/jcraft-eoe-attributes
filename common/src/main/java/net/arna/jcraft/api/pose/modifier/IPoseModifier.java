package net.arna.jcraft.api.pose.modifier;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.LivingEntity;

public interface IPoseModifier {
    IPoseModifier EMPTY = new IPoseModifier() {
        @Override
        public String getId() {
            return "empty";
        }

        @Override
        public void apply(HumanoidModel<?> model, LivingEntity user, float age) {
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
     * Applies this modifier to the given model part.
     *
     * @param model The humanoid model to modify
     * @param user  The entity using the model. Can be used for context
     * @param age The age of the entity in ticks. Can be used for animations or time-based effects
     */
    void apply(HumanoidModel<?> model, LivingEntity user, float age);
}
