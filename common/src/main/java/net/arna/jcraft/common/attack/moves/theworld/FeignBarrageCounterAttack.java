package net.arna.jcraft.common.attack.moves.theworld;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import lombok.NonNull;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.attack.core.data.MoveType;
import net.arna.jcraft.common.attack.core.data.MoveSetLoader;
import net.arna.jcraft.common.attack.core.itfs.AttackRotationOffsetOverride;
import net.arna.jcraft.common.attack.moves.base.AbstractCounterAttack;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.attack.moves.shared.CounterMissMove;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.entity.stand.TheWorldEntity;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.registry.JSoundRegistry;
import net.arna.jcraft.registry.JStatusRegistry;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

@Getter
public final class FeignBarrageCounterAttack extends AbstractCounterAttack<FeignBarrageCounterAttack, TheWorldEntity>
        implements AttackRotationOffsetOverride {
    private static final CounterMissMove<TheWorldEntity> missAttack = new CounterMissMove<>(10);
    private final AbstractMove<?, ? super TheWorldEntity> hitMove;

    public FeignBarrageCounterAttack(final int cooldown, final int windup, final int duration, final float moveDistance,
                                     final AbstractMove<?, ? super TheWorldEntity> hitMove) {
        super(cooldown, windup, duration, moveDistance);
        this.hitMove = hitMove;
    }

    @Override
    public void whiff(final @NonNull TheWorldEntity attacker, final @NonNull LivingEntity user) {
        attacker.setMove(missAttack, TheWorldEntity.State.COUNTER_MISS);
        JCraft.stun(user, missAttack.getDuration(), 0);
    }

    @Override
    public void counter(final @NonNull TheWorldEntity attacker, final Entity countered, final DamageSource counteredDamageSource) {
        super.counter(attacker, countered, counteredDamageSource);

        if (countered == null || !attacker.hasUser()) {
            return;
        }
        // Teleports behind countered
        final LivingEntity user = attacker.getUserOrThrow();
        final Vec3 behind = countered.position().subtract(countered.getLookAngle());
        final BlockPos behindBlockPos = new BlockPos((int) behind.x, (int) behind.y, (int) behind.z);
        JUtils.setVelocity(user, 0, 0, 0);
        if (!user.level().getBlockState(behindBlockPos).canOcclude()) {
            user.teleportToWithTicket(behind.x, behind.y, behind.z);
        }
        user.lookAt(EntityAnchorArgument.Anchor.EYES, countered.getEyePosition());

        if (countered instanceof LivingEntity livingEntity) { // Override stun & cancel all moves
            livingEntity.removeEffect(JStatusRegistry.DAZED.get());
            JCraft.stun(livingEntity, 20, 0);

            JUtils.cancelMoves(livingEntity);
        }

        attacker.setMove(hitMove, TheWorldEntity.State.COUNTER_HIT);
        attacker.playSound(JSoundRegistry.TIME_SKIP.get(), 1, 1);
    }

    @Override
    public @NonNull MoveType<FeignBarrageCounterAttack> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    protected @NonNull FeignBarrageCounterAttack getThis() {
        return this;
    }

    @Override
    public @NonNull FeignBarrageCounterAttack copy() {
        return copyExtras(new FeignBarrageCounterAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(), hitMove));
    }

    @Override
    public float getAttackRotationOffset(StandEntity<?, ?> attacker) {
        return attacker.getMoveStun() > getWindupPoint() ? attacker.getIdleRotation() : StandEntity.ATTACK_ROTATION;
    }

    public static class Type extends AbstractCounterAttack.Type<FeignBarrageCounterAttack> {
        public static final Type INSTANCE = new Type();

        @SuppressWarnings("unchecked")
        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<FeignBarrageCounterAttack>, FeignBarrageCounterAttack> buildCodec(RecordCodecBuilder.Instance<FeignBarrageCounterAttack> instance) {
            return baseDefault(instance).and(MoveSetLoader.MOVE_CODEC.get()
                    .fieldOf("hit_move")
                    .<AbstractMove<?, ? super TheWorldEntity>>xmap(m -> (AbstractMove<?, ? super TheWorldEntity>) m, m -> m)
                    .forGetter(FeignBarrageCounterAttack::getHitMove))
                    .apply(instance, applyExtras(FeignBarrageCounterAttack::new));
        }
    }
}
