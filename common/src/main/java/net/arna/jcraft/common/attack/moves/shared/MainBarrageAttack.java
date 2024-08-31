package net.arna.jcraft.common.attack.moves.shared;

import lombok.NonNull;
import net.arna.jcraft.common.attack.core.IAttacker;
import net.arna.jcraft.common.attack.core.MoveInputType;
import net.arna.jcraft.common.attack.core.ctx.BooleanMoveVariable;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractBarrageAttack;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.util.JUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import java.util.Set;

/**
 * A simple attack that performs at a set interval.
 * The user may crouch to break blocks.
 */
public final class MainBarrageAttack<A extends IAttacker<? extends A, ?>> extends AbstractBarrageAttack<MainBarrageAttack<A>, A> {
    public static final BooleanMoveVariable BREAK_BLOCKS = new BooleanMoveVariable();
    private final float mineableHardness;
    private final int baseStun, baseDuration;
    private static final int MINING_BARRAGE_TIME = 200;

    public MainBarrageAttack(int cooldown, int windup, int duration, float attackDistance, float damage, int stun,
                             float hitboxSize, float knockback, float offset, int interval, float mineableHardness) {
        super(cooldown, windup, duration, attackDistance, damage, stun, hitboxSize, knockback, offset, interval);
        this.mineableHardness = mineableHardness;
        this.baseDuration = duration;
        this.baseStun = stun;
        withBarrageShockwaves();
        withHoldable();
    }

    @Override
    public void onInitiate(A attacker) {
        boolean breakBlocks = attacker.getUserOrThrow().isShiftKeyDown();
        withDuration(breakBlocks ? MINING_BARRAGE_TIME : baseDuration);
        super.onInitiate(attacker);
        attacker.getMoveContext().setBoolean(BREAK_BLOCKS, breakBlocks);
        withStun(breakBlocks ? 1 : baseStun);
    }

    @Override
    public boolean canFinish(A attacker) {
        if (attacker.getMoveContext().getBoolean(BREAK_BLOCKS)) {
            return false;
        }
        return super.canFinish(attacker);
    }

    @Override
    public void onUserMoveInput(A attacker, MoveInputType type, boolean pressed, boolean moveInitiated) {
        super.onUserMoveInput(attacker, type, pressed, moveInitiated);
        // Mining barrages may be held
        if (attacker.getMoveContext().getBoolean(BREAK_BLOCKS) && type.getMoveType() == getMoveType() && !pressed) {
            attacker.cancelMove();
        }
    }

    @Override
    protected @NonNull MainBarrageAttack<A> getThis() {
        return this;
    }

    @Override
    public void registerContextEntries(MoveContext ctx) {
        ctx.register(BREAK_BLOCKS, false);
    }

    @Override
    public @NonNull Set<LivingEntity> perform(A attacker, LivingEntity user, MoveContext ctx) {
        Set<LivingEntity> targets = super.perform(attacker, user, ctx);

        if (ctx.getBoolean(BREAK_BLOCKS)) {
            ServerLevel serverWorld = (ServerLevel) user.level();
            LivingEntity attackerEntity = attacker.getBaseEntity();
            Vec3i lookDirection = JUtils.getLookDirection(user).getNormal();
            Vec3i localUp = GravityChangerAPI.getGravityDirection(user).getOpposite().getNormal();

            BlockPos userPos = lookDirection.getY() != 0 ? attackerEntity.blockPosition() : user.blockPosition();

            breakIfPossible(serverWorld, userPos.offset(lookDirection), user);
            breakIfPossible(serverWorld, userPos.offset(lookDirection.offset(localUp)), user);
        }

        return targets;
    }

    private void breakIfPossible(ServerLevel world, BlockPos pos, LivingEntity user) {
        Block block = world.getBlockState(pos).getBlock();
        if (block.defaultDestroyTime() < 0) {
            return;
        }
        if (block.defaultDestroyTime() <= mineableHardness) {
            world.destroyBlock(pos, true, user);
        }
    }

    @Override
    public @NonNull MainBarrageAttack<A> copy() {
        return copyExtras(new MainBarrageAttack<>(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(), getStun(),
                getHitboxSize(), getKnockback(), getOffset(), getInterval(), mineableHardness));
    }
}
