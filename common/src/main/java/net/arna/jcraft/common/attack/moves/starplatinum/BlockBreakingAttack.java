package net.arna.jcraft.common.attack.moves.starplatinum;

import lombok.NonNull;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractSimpleAttack;
import net.arna.jcraft.common.entity.stand.AbstractStarPlatinumEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import java.util.Set;

import static net.minecraft.world.level.block.Block.getId;

public class BlockBreakingAttack extends AbstractSimpleAttack<BlockBreakingAttack, AbstractStarPlatinumEntity<?, ?>> {
    public BlockBreakingAttack(int cooldown, int windup, int duration, float moveDistance, float damage, int stun, float hitboxSize, float knockback, float offset) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset);
    }

    @Override
    public void performHook(AbstractStarPlatinumEntity<?, ?> attacker, Set<LivingEntity> targets, Set<AABB> boxes, DamageSource damageSource, Vec3 forwardPos, Vec3 rotationVector, MoveContext ctx) {
        Level world = attacker.level();
        if (world.getGameRules().getBoolean(JCraft.STAND_GRIEFING)) {
            BlockPos bPos = attacker.blockPosition().offset((int) rotationVector.x * 1, (int) rotationVector.y * 1, (int) rotationVector.z * 1);
            for (int x = -1; x < 2; x++) {
                for (int y = -1; y < 2; y++) {
                    for (int z = -1; z < 2; z++) {
                        BlockPos curPos = bPos.offset(x, y, z);
                        BlockState curState = world.getBlockState(curPos);

                        if (curState.getBlock().getExplosionResistance() > 10f || curState.isAir()) {
                            continue;
                        }

                        world.levelEvent(null, 2001, curPos, getId(curState)); // Particles

                        FallingBlockEntity fallingBlock = FallingBlockEntity.fall(world, curPos, curState);
                        fallingBlock.setDeltaMovement(rotationVector.add(x * 0.5, 0.5, z * 0.5));
                        fallingBlock.time = -120;
                        fallingBlock.hurtMarked = true;
                        fallingBlock.hasImpulse = true;
                    }
                }
            }
        }
    }

    @Override
    protected @NonNull BlockBreakingAttack getThis() {
        return this;
    }

    @Override
    public @NonNull BlockBreakingAttack copy() {
        return copyExtras(new BlockBreakingAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(), getStun(),
                getHitboxSize(), getKnockback(), getOffset()));
    }
}
