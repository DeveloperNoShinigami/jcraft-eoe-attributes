package net.arna.jcraft.common.attack.moves.cream;

import lombok.NonNull;
import net.arna.jcraft.common.attack.moves.base.AbstractEffectInflictingAttack;
import net.arna.jcraft.common.entity.damage.JDamageSources;
import net.arna.jcraft.common.entity.stand.CreamEntity;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.util.JParticleType;
import net.arna.jcraft.registry.JStatusRegistry;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import java.util.List;

public class DestroyAttack extends AbstractEffectInflictingAttack<DestroyAttack, CreamEntity> {
    public DestroyAttack(int cooldown, int windup, int duration, float moveDistance, float damage, int stun,
                         float hitboxSize, float knockback, float offset) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset,
                List.of(new MobEffectInstance(JStatusRegistry.KNOCKDOWN.get(), 35, 0, true, false)));
        hitSpark = JParticleType.HIT_SPARK_3;
    }

    @Override
    protected void processTarget(CreamEntity attacker, LivingEntity target, Vec3 kbVec, DamageSource damageSource) {
        super.processTarget(attacker, target, kbVec, damageSource);

        StandEntity.trueDamage(8, JDamageSources.stand(attacker), target);
    }

    @Override
    protected @NonNull DestroyAttack getThis() {
        return this;
    }

    @Override
    public @NonNull DestroyAttack copy() {
        return copyExtras(new DestroyAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(),
                getStun(), getHitboxSize(), getKnockback(), getOffset()));
    }
}
