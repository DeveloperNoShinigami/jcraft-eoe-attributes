package net.arna.jcraft.common.attack.moves.madeinheaven;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.api.attack.MoveType;
import net.arna.jcraft.common.attack.moves.base.AbstractBarrageAttack;
import net.arna.jcraft.common.entity.stand.MadeInHeavenEntity;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

// This being a barrage is really just for the interval-based performing logic.
public final class JudgementAttack extends AbstractBarrageAttack<JudgementAttack, MadeInHeavenEntity> {
    private Vec3 initPos, initRot;

    public JudgementAttack(final int cooldown, final int windup, final int duration, final float moveDistance, final int interval) {
        super(cooldown, windup, duration, moveDistance, 0, 0, 0, 0, 0, interval);
    }

    @Override
    public @NonNull MoveType<JudgementAttack> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final MadeInHeavenEntity attacker, final LivingEntity user) {
        Set<LivingEntity> targets = Set.of();
        if (attacker.getMoveStun() > 1) {
            if (getBlow(attacker) > 0) {
                final RandomSource random = attacker.getRandom();
                targets = SpeedSliceAttack.doSpeedSlice(attacker,
                        initPos.add(initRot.scale(random.triangle(2, 2))),
                        initPos.add(random.triangle(0, 5), random.triangle(0, 5),
                                random.triangle(0, 5)),
                        1f, 0.1f, 1.75f, 20, 1);
            } else {
                initPos = user.position();
                initRot = Vec3.directionFromRotation(0, user.getYRot());
            }
        } else {
            targets = SpeedSliceAttack.doSpeedSlice(attacker,
                    initPos.subtract(user.getLookAngle().scale(3)),
                    initPos.add(initRot.scale(10)), 6, 3, 2f, 5, 3);
        }

        return targets;
    }

    @Override
    protected @NonNull JudgementAttack getThis() {
        return this;
    }

    @Override
    public @NonNull JudgementAttack copy() {
        return copyExtras(new JudgementAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getInterval()));
    }

    public static class Type extends AbstractBarrageAttack.Type<JudgementAttack> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<JudgementAttack>, JudgementAttack> buildCodec(RecordCodecBuilder.Instance<JudgementAttack> instance) {
            return instance.group(extras(), attackExtras(), cooldown(), windup(), duration(), moveDistance(), interval())
                    .apply(instance, applyAttackExtras(JudgementAttack::new));
        }
    }
}
