package net.arna.jcraft.common.attack.moves.cmoon;

import lombok.NonNull;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractSimpleAttack;
import net.arna.jcraft.common.entity.stand.CMoonEntity;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.arna.jcraft.registry.JStatusRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import java.util.Set;

public class GroundSlamAttack extends AbstractSimpleAttack<GroundSlamAttack, CMoonEntity> {
    public GroundSlamAttack(int cooldown, int windup, int duration, float moveDistance, float damage, int stun,
                            float hitboxSize, float knockback, float offset) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset);
    }

    @Override
    protected void processTarget(CMoonEntity attacker, LivingEntity target, Vec3 kbVec, DamageSource damageSource) {
        super.processTarget(attacker, target, kbVec, damageSource);

        LivingEntity user = attacker.getUserOrThrow();
        GravityChangerAPI.setWorldVelocity(target, GravityChangerAPI.getGravityDirection(user).step());
        target.hurtMarked = true;
        if (user.isShiftKeyDown()) {
            target.addEffect(new MobEffectInstance(JStatusRegistry.KNOCKDOWN.get(), 30, 0, true, false));
        }
    }

    @Override
    public void performHook(CMoonEntity attacker, Set<LivingEntity> targets, Set<AABB> boxes, DamageSource damageSource, Vec3 forwardPos, Vec3 rotationVector, MoveContext ctx) {
        Level world = attacker.level();
        Vec3i gravityVector = GravityChangerAPI.getGravityDirection(attacker).getNormal();

        if (world.getGameRules().getBoolean(JCraft.STAND_GRIEFING)) {
            BlockPos bPos = attacker.blockPosition();

            // Adjust pancake shape for gravity
            Vec3i min = new Vec3i(-2, -2, -2);
            Vec3i max = new Vec3i(3, 3, 3);
            min = min.subtract(gravityVector);
            max = max.offset(gravityVector);

            for (int x = min.getX(); x < max.getX(); x++) {
                for (int y = min.getY(); y < max.getY(); y++) {
                    for (int z = min.getZ(); z < max.getZ(); z++) {
                        BlockPos curPos = bPos.offset(x, y, z);
                        BlockState curState = world.getBlockState(curPos);

                        if (curState.getBlock().getExplosionResistance() > 10f || curState.isAir()) {
                            continue;
                        }

                        FallingBlockEntity fallingBlock = FallingBlockEntity.fall(world, curPos, curState);
                        fallingBlock.setDeltaMovement(-gravityVector.getX() * 0.5, -gravityVector.getY() * 0.5, -gravityVector.getZ() * 0.5);
                        fallingBlock.time = -120;
                        fallingBlock.hurtMarked = true;
                        fallingBlock.hasImpulse = true;
                    }
                }
            }
        }

        JComponentPlatformUtils.getShockwaveHandler(attacker.level()).addShockwave(attacker.position().add(rotationVector), new Vec3(GravityChangerAPI.getGravityDirection(attacker).step()), 4.0f);
    }

    @Override
    protected @NonNull GroundSlamAttack getThis() {
        return this;
    }

    @Override
    public @NonNull GroundSlamAttack copy() {
        return copyExtras(new GroundSlamAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(),
                getStun(), getHitboxSize(), getKnockback(), getOffset()));
    }
}
