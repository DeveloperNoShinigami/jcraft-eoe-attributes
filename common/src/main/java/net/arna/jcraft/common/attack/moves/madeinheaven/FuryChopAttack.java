package net.arna.jcraft.common.attack.moves.madeinheaven;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.data.MoveType;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractSimpleAttack;
import net.arna.jcraft.common.entity.stand.MadeInHeavenEntity;
import net.arna.jcraft.common.util.JParticleType;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import java.util.Set;

public final class FuryChopAttack extends AbstractSimpleAttack<FuryChopAttack, MadeInHeavenEntity> {
    public FuryChopAttack(final int cooldown, final int windup, final int duration, final float moveDistance, final float damage, final int stun,
                          final float hitboxSize, final float knockback, final float offset) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset);
        hitSpark = JParticleType.HIT_SPARK_2;
    }

    @Override
    public @NonNull MoveType<FuryChopAttack> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final MadeInHeavenEntity attacker, final LivingEntity user, final MoveContext ctx) {
        Set<LivingEntity> targets = super.perform(attacker, user, ctx);

        // Punish user with mining fatigue on miss, reward with haste on hit.
        user.addEffect(new MobEffectInstance(targets.isEmpty() ? MobEffects.DIG_SLOWDOWN : MobEffects.DIG_SPEED,
                160, 0));

        return targets;
    }

    @Override
    protected void processTarget(final MadeInHeavenEntity attacker, final LivingEntity target, final Vec3 kbVec, final DamageSource damageSource) {
        super.processTarget(attacker, target, kbVec, damageSource);

        target.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 160, 0));
    }

    @Override
    protected @NonNull FuryChopAttack getThis() {
        return this;
    }

    @Override
    public @NonNull FuryChopAttack copy() {
        return copyExtras(new FuryChopAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(),
                getStun(), getHitboxSize(), getKnockback(), getOffset()));
    }

    public static class Type extends AbstractSimpleAttack.Type<FuryChopAttack> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<FuryChopAttack>, FuryChopAttack> buildCodec(RecordCodecBuilder.Instance<FuryChopAttack> instance) {
            return attackDefault(instance, FuryChopAttack::new);
        }
    }
}
