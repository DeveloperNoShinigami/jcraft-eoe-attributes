package net.arna.jcraft.common.attack.moves.horus;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.api.attack.MoveType;
import net.arna.jcraft.api.attack.moves.AbstractSimpleAttack;
import net.arna.jcraft.common.entity.projectile.LargeIcicleProjectile;
import net.arna.jcraft.common.entity.stand.HorusEntity;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.gravity.util.RotationUtil;
import net.arna.jcraft.registry.JSoundRegistry;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

public class StompAttack extends AbstractSimpleAttack<StompAttack, HorusEntity> {
    public StompAttack(int cooldown, int windup, int duration, float moveDistance, float damage, int stun,
                       float hitboxSize, float knockback, float offset) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset);
    }

    @Override
    public @NonNull MoveType<StompAttack> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(HorusEntity attacker, LivingEntity user) {
        Set<LivingEntity> targets = super.perform(attacker, user);

        LargeIcicleProjectile icicle = new LargeIcicleProjectile(attacker.level(), user);
        attacker.setLastLargeIcicle(icicle);

        // Shoot slightly upwards
        final Direction gravity = GravityChangerAPI.getGravityDirection(user);
        final Vec3 velocity = attacker.isFree() || !user.onGround() ?
                attacker.getLookAngle()
                        .add(RotationUtil.vecPlayerToWorld(new Vec3(0, -1, 0), gravity))
                        .scale(0.01)
                :
                user.getLookAngle()
                        .add(RotationUtil.vecPlayerToWorld(new Vec3(0, 1, 0), gravity))
                        .scale(0.01);
        final double e = velocity.x, f = velocity.y, g = velocity.z;
        final double l = velocity.horizontalDistance();
        icicle.moveTo(attacker.getX(), attacker.getY(), attacker.getZ(),
                (float) (Mth.atan2(-e, -g) * 57.2957763671875),
                (float) (Mth.atan2(f, l) * 57.2957763671875)
        );
        icicle.setDeltaMovement(velocity);
        icicle.lock();

        attacker.level().addFreshEntity(icicle);
        attacker.playSound(JSoundRegistry.HORUS_STOMP_SLAM.get());

        return targets;
    }

    @Override
    protected @NonNull StompAttack getThis() {
        return this;
    }

    @Override
    public @NonNull StompAttack copy() {
        return copyExtras(new StompAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(),
                getStun(), getHitboxSize(), getKnockback(), getOffset()));
    }

    public static class Type extends AbstractSimpleAttack.Type<StompAttack> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<StompAttack>, StompAttack> buildCodec(RecordCodecBuilder.Instance<StompAttack> instance) {
            return attackDefault(instance, StompAttack::new);
        }
    }
}
