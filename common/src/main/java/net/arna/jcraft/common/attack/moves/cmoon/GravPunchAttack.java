package net.arna.jcraft.common.attack.moves.cmoon;

import lombok.NonNull;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractSimpleAttack;
import net.arna.jcraft.common.entity.stand.CMoonEntity;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.gravity.util.Gravity;
import net.arna.jcraft.common.util.JParticleType;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.arna.jcraft.registry.JStatusRegistry;
import net.minecraft.core.Direction;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import java.util.Set;

public class GravPunchAttack extends AbstractSimpleAttack<GravPunchAttack, CMoonEntity> {
    public static final String GRAVITY_SOURCE = JCraft.MOD_ID + "$" + GravPunchAttack.class.getSimpleName();

    public GravPunchAttack(int cooldown, int windup, int duration, float moveDistance, float damage, int stun,
                           float hitboxSize, float knockback, float offset) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset);
        hitSpark = JParticleType.HIT_SPARK_2; //todo: gravpunch inversion hitspark
    }

    @Override
    protected void processTarget(CMoonEntity attacker, LivingEntity target, Vec3 kbVec, DamageSource damageSource) {
        super.processTarget(attacker, target, kbVec, damageSource);

        Direction oppositeGravity = GravityChangerAPI.getGravityDirection(target).getOpposite();
        GravityChangerAPI.addGravity(target, new Gravity(oppositeGravity, 2, 60, GRAVITY_SOURCE));
        target.addEffect(new MobEffectInstance(JStatusRegistry.WEIGHTLESS.get(), 60, 0, true, false));
        // Launches them up relative to their original gravity, to prevent ground clipping
        JUtils.setVelocity(target, oppositeGravity.getStepX() * 0.2, oppositeGravity.getStepY() * 0.2, oppositeGravity.getStepZ() * 0.2);
    }

    @Override
    public void performHook(CMoonEntity attacker, Set<LivingEntity> targets, Set<AABB> boxes, DamageSource damageSource, Vec3 forwardPos, Vec3 rotationVector, MoveContext ctx) {
        if (targets.isEmpty()) {
            return;
        }
        JComponentPlatformUtils.getShockwaveHandler(attacker.level()).addShockwave(forwardPos, new Vec3(GravityChangerAPI.getGravityDirection(attacker).step()), 3.0f);
    }

    @Override
    protected @NonNull GravPunchAttack getThis() {
        return this;
    }

    @Override
    public @NonNull GravPunchAttack copy() {
        return copyExtras(new GravPunchAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(),
                getStun(), getHitboxSize(), getKnockback(), getOffset()));
    }
}
