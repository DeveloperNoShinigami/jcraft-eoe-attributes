package net.arna.jcraft.common.attack.moves.killerqueen.bitesthedust;

import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractSimpleAttack;
import net.arna.jcraft.common.entity.stand.KQBTDEntity;
import net.arna.jcraft.common.util.JParticleType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import java.util.Set;

public final class ElbowAttack extends AbstractSimpleAttack<ElbowAttack, KQBTDEntity> {
    public ElbowAttack(final int cooldown, final int windup, final int duration, final float attackDistance, final float damage, final int stun,
                       final float hitboxSize, final float knockback, final float offset) {
        super(cooldown, windup, duration, attackDistance, damage, stun, hitboxSize, knockback, offset);
        withLaunch();
        hitSpark = JParticleType.HIT_SPARK_2;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final KQBTDEntity attacker, final LivingEntity user, final MoveContext ctx) {
        final Set<LivingEntity> targets = super.perform(attacker, user, ctx);
        for (LivingEntity target : targets) {
            target.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 5, 4, true, false));
        }

        return targets;
    }

    @Override
    protected @NonNull ElbowAttack getThis() {
        return this;
    }

    @Override
    public @NonNull ElbowAttack copy() {
        return copyExtras(new ElbowAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(), getStun(),
                getHitboxSize(), getKnockback(), getOffset()));
    }
}
