package net.arna.jcraft.api.pose.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Builder;
import lombok.Singular;
import net.arna.jcraft.api.pose.ModifierCondition;
import net.arna.jcraft.api.pose.PoseModifiers;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

@Builder
public record PoseModifierGroup(@Singular List<ModifierCondition> conditions,
                                @Singular List<IPoseModifier> modifiers) implements IPoseModifier {
    public static final String ID = "group";
    public static final Codec<PoseModifierGroup> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ModifierCondition.CODEC.listOf().optionalFieldOf("conditions", List.of()).forGetter(PoseModifierGroup::conditions),
            PoseModifiers.CODEC.listOf().fieldOf("modifiers").forGetter(PoseModifierGroup::modifiers)
    ).apply(instance, PoseModifierGroup::new));

    @Override
    public String getId() {
        return "group";
    }

    @Override
    public void apply(HumanoidModel<?> model, LivingEntity user, float age) {
        if (ModifierCondition.anyFails(conditions, model, user)) {
            return; // Skip if any condition is not met
        }

        for (IPoseModifier modifier : modifiers) {
            modifier.apply(model, user, age);
        }
    }
}
