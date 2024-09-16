package net.arna.jcraft.common.attack.moves.madeinheaven;

import lombok.Getter;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.component.living.CommonHitPropertyComponent;
import net.arna.jcraft.common.entity.stand.MadeInHeavenEntity;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.common.attack.MobilityType;
import net.arna.jcraft.registry.JSoundRegistry;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public final class SpeedSliceAttack extends AbstractMove<SpeedSliceAttack, MadeInHeavenEntity> {
    private final float damage, hitboxSize, knockback;

    public SpeedSliceAttack(int cooldown, int windup, int duration, float moveDistance, float damage, float hitboxSize,
                            float knockback) {
        super(cooldown, windup, duration, moveDistance);
        this.damage = damage;
        this.hitboxSize = hitboxSize;
        this.knockback = knockback;

        ranged = true;
        mobilityType = MobilityType.TELEPORT;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(MadeInHeavenEntity attacker, LivingEntity user, MoveContext ctx) {
        return doSpeedSlice(attacker, user.getEyePosition(), user.getEyePosition().add(user.getLookAngle().scale(8)),
                getDamage(), getKnockback(), getHitboxSize(), 20, 1);
    }

    public static Set<LivingEntity> doSpeedSlice(MadeInHeavenEntity attacker, Vec3 start, Vec3 end, float damage, float knockback, float size, int stunTicks, int stunType) {
        Level world = attacker.level();
        LivingEntity user = attacker.getUserOrThrow();
        HitResult hitResult = world.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, user));
        Vec3 pos1 = user.position();
        Vec3 pos2 = hitResult.getLocation();
        Vec3 towardsVec = pos2.subtract(pos1);

        Vec3 kbVec = towardsVec.normalize();

        DamageSource playerSource = world.damageSources().mobAttack(user);

        user.teleportToWithTicket(pos2.x, pos2.y, pos2.z);

        Set<LivingEntity> targets = new HashSet<>();
        double count = Math.round(pos1.distanceTo(pos2));

        for (int i = 0; i < count; i++) {
            Vec3 curPos = pos1.add(towardsVec.scale(i / count));

            Vec3 vec1 = curPos.add(-size, -size, -size);
            Vec3 vec2 = curPos.add(size, size, size);

            JUtils.displayHitbox(world, vec1, vec2);

            List<LivingEntity> hurt = world.getEntitiesOfClass(LivingEntity.class, new AABB(vec1, vec2),
                    EntitySelector.NO_CREATIVE_OR_SPECTATOR.and(e -> e != attacker && e != user));
            hurt.removeIf(targets::contains);
            targets.addAll(hurt);
        }

        for (LivingEntity ent : targets) {
            LivingEntity target = JUtils.getUserIfStand(ent);
            StandEntity.damageLogic(world, target, kbVec.scale(knockback).add(0, knockback / 4, 0),
                    stunTicks, stunType, false, damage, true, (int) (4 + damage), playerSource, user, CommonHitPropertyComponent.HitAnimation.MID);
        }

        if (attacker.getAccelTime() > 0 && !targets.isEmpty()) {
            attacker.incrementSpeedometer();
        }

        attacker.playSound(JSoundRegistry.MIH_ZOOM.get(), 1f, 1f);

        return targets;
    }

    @Override
    protected @NonNull SpeedSliceAttack getThis() {
        return this;
    }

    @Override
    public @NonNull SpeedSliceAttack copy() {
        return copyExtras(new SpeedSliceAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(),
                getDamage(), getHitboxSize(), getKnockback()));
    }
}
