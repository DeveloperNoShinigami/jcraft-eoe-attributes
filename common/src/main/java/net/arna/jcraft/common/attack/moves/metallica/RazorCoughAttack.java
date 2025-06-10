package net.arna.jcraft.common.attack.moves.metallica;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.api.attack.MoveType;
import net.arna.jcraft.api.attack.moves.AbstractMove;
import net.arna.jcraft.common.entity.stand.MetallicaEntity;
import net.arna.jcraft.common.tickable.MagneticFields;
import net.arna.jcraft.common.tickable.RazorCoughs;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.registry.JSoundRegistry;
import net.arna.jcraft.registry.JStatusRegistry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashSet;
import java.util.Set;

public class RazorCoughAttack extends AbstractMove<RazorCoughAttack, MetallicaEntity> {
    public RazorCoughAttack(int cooldown, int windup, int duration) {
        super(cooldown, windup, duration, 0);
    }

    @Override
    public @NonNull MoveType<RazorCoughAttack> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(MetallicaEntity attacker, LivingEntity user) {
        final Set<Entity> filter = new HashSet<>(2);
        filter.add(user);
        filter.add(attacker);
        if (user.isVehicle()) {
            filter.addAll(user.getPassengers());
        }

        MagneticFields.forAllOfOwner(user, (field) -> {
            Set<LivingEntity> hit = JUtils.generateHitbox(attacker.level(), field.pos, field.getStrength(), filter);
            for (LivingEntity target : hit) {
                RazorCoughs.add(user, target);
                target.playSound(JSoundRegistry.METALLICA_RAZOR_VOMIT_PREPARE.get());

                int amplifier = 0;
                MobEffectInstance effect = target.getEffect(JStatusRegistry.HYPOXIA.get());
                if (effect != null) {
                    amplifier = effect.getAmplifier() + 1;
                }

                target.addEffect(new MobEffectInstance(
                        JStatusRegistry.HYPOXIA.get(),
                        20 * 20,
                        amplifier
                ));
            }
        });

        return Set.of();
    }

    @Override
    protected @NonNull RazorCoughAttack getThis() {
        return this;
    }

    @Override
    public @NonNull RazorCoughAttack copy() {
        return copyExtras(new RazorCoughAttack(getCooldown(), getWindup(), getDuration()));
    }

    public static class Type extends AbstractMove.Type<RazorCoughAttack> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<RazorCoughAttack>, RazorCoughAttack> buildCodec(RecordCodecBuilder.Instance<RazorCoughAttack> instance) {
            return instance.group(extras(), cooldown(), windup(), duration()).apply(instance, applyExtras(RazorCoughAttack::new));
        }
    }
}
