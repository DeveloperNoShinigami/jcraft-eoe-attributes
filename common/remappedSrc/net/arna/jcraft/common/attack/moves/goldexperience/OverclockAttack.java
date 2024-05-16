package net.arna.jcraft.common.attack.moves.goldexperience;

import lombok.NonNull;
import net.arna.jcraft.common.attack.core.StunType;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractSimpleAttack;
import net.arna.jcraft.common.entity.stand.GoldExperienceEntity;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.util.JParticleType;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.registry.JStatusRegistry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import java.util.Set;

public class OverclockAttack extends AbstractSimpleAttack<OverclockAttack, GoldExperienceEntity> {
    public OverclockAttack(int cooldown, int windup, int duration, float attackDistance, float damage, int stun,
                           float hitboxSize, float knockback, float offset) {
        super(cooldown, windup, duration, attackDistance, damage, stun, hitboxSize, knockback, offset);
        withStunType(StunType.LAUNCH);
        hitSpark = JParticleType.HIT_SPARK_3;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(GoldExperienceEntity attacker, LivingEntity user, MoveContext ctx) {
        Set<LivingEntity> targets = super.perform(attacker, user, ctx);

        for (LivingEntity target : targets) {
            target.addEffect(new MobEffectInstance(JStatusRegistry.DAZED.get(), 60, 3, true, false));
            target.addEffect(new MobEffectInstance(JStatusRegistry.OUTOFBODY.get(), 60, 0, false, true));

            Vec3 upDir = new Vec3(GravityChangerAPI.getGravityDirection(user).step());
            JUtils.setVelocity(target, upDir.scale(-0.8));
        }

        return targets;
    }

    @Override
    protected @NonNull OverclockAttack getThis() {
        return this;
    }

    @Override
    public @NonNull OverclockAttack copy() {
        return copyExtras(new OverclockAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(), getStun(),
                getHitboxSize(), getKnockback(), getOffset()));
    }
}
