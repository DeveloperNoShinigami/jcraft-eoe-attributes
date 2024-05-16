package net.arna.jcraft.common.attack.moves.madeinheaven;

import lombok.NonNull;
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

public class FuryChopAttack extends AbstractSimpleAttack<FuryChopAttack, MadeInHeavenEntity> {
    public FuryChopAttack(int cooldown, int windup, int duration, float moveDistance, float damage, int stun,
                          float hitboxSize, float knockback, float offset) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset);
        hitSpark = JParticleType.HIT_SPARK_2;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(MadeInHeavenEntity attacker, LivingEntity user, MoveContext ctx) {
        Set<LivingEntity> targets = super.perform(attacker, user, ctx);

        // Punish user with mining fatigue on miss, reward with haste on hit.
        user.addEffect(new MobEffectInstance(targets.isEmpty() ? MobEffects.DIG_SLOWDOWN : MobEffects.DIG_SPEED,
                160, 0));

        return targets;
    }

    @Override
    protected void processTarget(MadeInHeavenEntity attacker, LivingEntity target, Vec3 kbVec, DamageSource damageSource) {
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
}
