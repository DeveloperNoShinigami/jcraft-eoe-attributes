package net.arna.jcraft.common.attack.moves.hierophantgreen;

import it.unimi.dsi.fastutil.ints.IntSet;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.IntMoveVariable;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractMultiHitAttack;
import net.arna.jcraft.common.entity.projectile.EmeraldProjectile;
import net.arna.jcraft.common.entity.stand.HGEntity;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import java.util.Set;

public class EmeraldSplashAttack extends AbstractMultiHitAttack<EmeraldSplashAttack, HGEntity> {
    public static final IntMoveVariable CHARGE_TIME = new IntMoveVariable();
    private final float speed;
    private boolean reflect = false;

    public EmeraldSplashAttack(int cooldown, int duration, float moveDistance, float damage, int stun, float knockback, float offset, IntSet hitMoments, float speed) {
        super(cooldown, duration, moveDistance, damage, stun, 0, knockback, offset, hitMoments);
        this.speed = speed;
        ranged = true;
    }

    public EmeraldSplashAttack withReflect() {
        this.reflect = true;
        return this;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(HGEntity attacker, LivingEntity user, MoveContext ctx) {
        int emeraldCount = 3 + ctx.getInt(CHARGE_TIME) / 10;

        for (int i = 0; i < emeraldCount; i++) {
            EmeraldProjectile emerald = new EmeraldProjectile(attacker.level(), user);
            emerald.shootFromRotation(user, user.getXRot(), user.getYRot(), 0.0F, speed, 5F);

            Vec3 upVec = GravityChangerAPI.getEyeOffset(attacker.getUserOrThrow());
            Vec3 heightOffset = upVec.scale(0.75);
            emerald.setPos(attacker.getBaseEntity().position().add(heightOffset));

            if (reflect) {
                emerald.withReflect();
            }

            attacker.level().addFreshEntity(emerald);
        }

        return Set.of();
    }

    @Override
    public void registerContextEntries(MoveContext ctx) {
        ctx.register(CHARGE_TIME);
    }

    @Override
    protected @NonNull EmeraldSplashAttack getThis() {
        return this;
    }

    @Override
    public @NonNull EmeraldSplashAttack copy() {
        EmeraldSplashAttack emeraldSplashAttack = copyExtras(new EmeraldSplashAttack(getCooldown(), getDuration(), getMoveDistance(), getDamage(), getStun(), getKnockback(), getOffset(), getHitMoments(), speed));
        if (reflect) {
            emeraldSplashAttack.withReflect();
        }
        return emeraldSplashAttack;
    }
}
