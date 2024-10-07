package net.arna.jcraft.common.attack.moves.shared;

import lombok.NonNull;
import net.arna.jcraft.JCraft;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
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

    public MainBarrageAttack(final int cooldown, final int windup, final int duration, final float attackDistance, final float damage, final int stun,
                             final float hitboxSize, final float knockback, final float offset, final int interval, final float mineableHardness) {
        super(cooldown, windup, duration, attackDistance, damage, stun, hitboxSize, knockback, offset, interval);
        this.mineableHardness = mineableHardness;
        this.baseDuration = duration;
        this.baseStun = stun;
        withBarrageShockwaves();
        withHoldable();
    }

    @Override
    public void onInitiate(final A attacker) {
        boolean breakBlocks = attacker.getUserOrThrow().isShiftKeyDown();
        if (breakBlocks && !attacker.getEntityWorld().getGameRules().getBoolean(JCraft.STAND_GRIEFING) && !(attacker.getUserOrThrow() instanceof Player)) {
            breakBlocks = false;
        }
        withDuration(breakBlocks ? MINING_BARRAGE_TIME : baseDuration);
        super.onInitiate(attacker);
        attacker.getMoveContext().setBoolean(BREAK_BLOCKS, breakBlocks);
        withStun(breakBlocks ? 1 : baseStun);
    }

    @Override
    public boolean canFinish(final A attacker) {
        if (attacker.getMoveContext().getBoolean(BREAK_BLOCKS)) {
            return false;
        }
        return super.canFinish(attacker);
    }

    @Override
    public void onUserMoveInput(final A attacker, final MoveInputType type, final boolean pressed, final boolean moveInitiated) {
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
    public void registerContextEntries(final MoveContext ctx) {
        ctx.register(BREAK_BLOCKS, false);
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final A attacker, final LivingEntity user, final MoveContext ctx) {
        final Set<LivingEntity> targets = super.perform(attacker, user, ctx);

        if (ctx.getBoolean(BREAK_BLOCKS)) {
            final ServerLevel serverWorld = (ServerLevel) user.level();
            final LivingEntity attackerEntity = attacker.getBaseEntity();
            final Vec3i lookDirection = JUtils.getLookDirection(user).getNormal();
            final Vec3i localUp = GravityChangerAPI.getGravityDirection(user).getOpposite().getNormal();

            final BlockPos userPos = lookDirection.getY() != 0 ? attackerEntity.blockPosition() : user.blockPosition();

            breakIfPossible(serverWorld, userPos.offset(lookDirection), user);
            breakIfPossible(serverWorld, userPos.offset(lookDirection.offset(localUp)), user);
        }

        return targets;
    }

    private void breakIfPossible(final ServerLevel world, final BlockPos pos, final LivingEntity user) {
        final Block block = world.getBlockState(pos).getBlock();
        if (block.defaultDestroyTime() < 0) {
            return;
        }
        if (block.defaultDestroyTime() <= mineableHardness &&
                (user instanceof Player || world.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING))) {
            world.destroyBlock(pos, true, user);
        }
    }

    @Override
    public @NonNull MainBarrageAttack<A> copy() {
        return copyExtras(new MainBarrageAttack<>(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(), getStun(),
                getHitboxSize(), getKnockback(), getOffset(), getInterval(), mineableHardness));
    }
}
