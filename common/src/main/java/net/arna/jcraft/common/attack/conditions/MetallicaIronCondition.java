package net.arna.jcraft.common.attack.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import net.arna.jcraft.common.attack.core.MoveCondition;
import net.arna.jcraft.api.attack.MoveConditionType;
import net.arna.jcraft.common.entity.stand.MetallicaEntity;
import org.jetbrains.annotations.NotNull;

@Getter
public class MetallicaIronCondition extends MoveCondition<MetallicaIronCondition, MetallicaEntity> {
    private final float requiredIron;

    private MetallicaIronCondition(float minIron) {
        this.requiredIron = minIron;
    }

    public static MetallicaIronCondition atLeast(float minIron) {
        return new MetallicaIronCondition(minIron);
    }

    @Override
    public boolean test(final MetallicaEntity attacker) {
        return attacker.getIron() >= requiredIron;
    }

    @Override
    public @NotNull MoveConditionType<MetallicaIronCondition> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements MoveConditionType<MetallicaIronCondition> {
        public static final Type INSTANCE = new Type();

        @Override
        public Codec<MetallicaIronCondition> getCodec() {
            return RecordCodecBuilder.create(instance -> instance.group(
                Codec.FLOAT.fieldOf("min_iron").forGetter(MetallicaIronCondition::getRequiredIron)
            ).apply(instance, MetallicaIronCondition::new));
        }
    }
}
