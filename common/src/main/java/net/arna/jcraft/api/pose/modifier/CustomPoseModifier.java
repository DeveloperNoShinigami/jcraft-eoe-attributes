package net.arna.jcraft.api.pose.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Builder;
import lombok.Singular;
import net.arna.jcraft.api.pose.*;
import net.minecraft.client.model.Model;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

@Builder
public record CustomPoseModifier(@Singular List<ModifierCondition> conditions, ModifierOperation operation,
                                 ModelPartGetter part, ModelPartProperty property, float value) implements IPoseModifier {
    public static final String ID = "custom";
    public static final Codec<CustomPoseModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ModifierCondition.CODEC.listOf().optionalFieldOf("conditions", List.of()).forGetter(CustomPoseModifier::conditions),
            ModifierOperation.CODEC.fieldOf("operation").forGetter(CustomPoseModifier::operation),
            ModelPartGetter.CODEC.fieldOf("part").forGetter(CustomPoseModifier::part),
            ModelPartProperty.CODEC.fieldOf("property").forGetter(CustomPoseModifier::property),
            Codec.FLOAT.fieldOf("value").forGetter(CustomPoseModifier::value)
    ).apply(instance, CustomPoseModifier::new));

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public boolean isModelSupported(final ModelType<?> modelType) {
        return modelType.hasPart(part.partName());
    }

    public void apply(final Model model, final LivingEntity user, final float age) {
        if (ModifierCondition.anyFails(conditions, model, user)) {
            return; // Skip if any condition is not met
        }

        operation.apply(part.getPart(model), property, value);
    }
}
