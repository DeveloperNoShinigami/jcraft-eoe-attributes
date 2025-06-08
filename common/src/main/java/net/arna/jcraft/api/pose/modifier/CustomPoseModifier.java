package net.arna.jcraft.api.pose.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Builder;
import lombok.Singular;
import net.arna.jcraft.api.pose.HumanoidModelPart;
import net.arna.jcraft.api.pose.ModelPartProperty;
import net.arna.jcraft.api.pose.ModifierCondition;
import net.arna.jcraft.api.pose.ModifierOperation;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

@Builder
public record CustomPoseModifier(@Singular List<ModifierCondition> conditions, ModifierOperation type,
                                 HumanoidModelPart part, ModelPartProperty property, float value) implements IPoseModifier {
    public static final String ID = "custom";
    public static final Codec<CustomPoseModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ModifierCondition.CODEC.listOf().optionalFieldOf("conditions", List.of()).forGetter(CustomPoseModifier::conditions),
            ModifierOperation.CODEC.fieldOf("type").forGetter(CustomPoseModifier::type),
            HumanoidModelPart.CODEC.fieldOf("part").forGetter(CustomPoseModifier::part),
            ModelPartProperty.CODEC.fieldOf("property").forGetter(CustomPoseModifier::property),
            Codec.FLOAT.fieldOf("value").forGetter(CustomPoseModifier::value)
    ).apply(instance, CustomPoseModifier::new));

    @Override
    public String getId() {
        return ID;
    }

    public void apply(HumanoidModel<?> model, LivingEntity user, float age) {
        if (ModifierCondition.anyFails(conditions, model, user)) {
            return; // Skip if any condition is not met
        }

        type.apply(part.getPart(model), property, value);
    }
}
