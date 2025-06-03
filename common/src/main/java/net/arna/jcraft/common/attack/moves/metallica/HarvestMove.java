package net.arna.jcraft.common.attack.moves.metallica;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.MoveInputType;
import net.arna.jcraft.api.attack.MoveType;
import net.arna.jcraft.common.attack.moves.base.AbstractBarrageAttack;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.entity.stand.MetallicaEntity;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.registry.JTagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.Optional;
import java.util.Set;

import static net.arna.jcraft.common.entity.stand.MetallicaEntity.SIPHON_POS;

public class HarvestMove extends AbstractBarrageAttack<HarvestMove, MetallicaEntity> {
    public HarvestMove(int duration, float moveDistance, int interval) {
        super(0, 0, duration, moveDistance, 0, 0, 0, 0, 0, interval);
        withHoldable();
    }

    @Override
    public @NonNull MoveType<HarvestMove> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public void onInitiate(MetallicaEntity attacker) {
        super.onInitiate(attacker);
        attacker.getEntityData().set(SIPHON_POS, Optional.empty());
    }

    @Override
    public void onUserMoveInput(final MetallicaEntity attacker, final MoveInputType type, final boolean pressed,final  boolean moveInitiated) {
        super.onUserMoveInput(attacker, type, pressed, moveInitiated);
        // Must be held
        if (type.getMoveClass() == getMoveClass() && !pressed) attacker.cancelMove();
    }

    @Override
    public void activeTick(MetallicaEntity attacker, int moveStun) {
        super.activeTick(attacker, moveStun);

        LivingEntity user = attacker.getUser();
        if (user instanceof Player) return;

        // AI iron harvest. Cancel automatically if iron is full
        if (attacker.getIron() >= MetallicaEntity.IRON_MAX)
            attacker.cancelMove();
    }

    @Override
    public @NonNull Set<LivingEntity> perform(MetallicaEntity attacker, LivingEntity user) {
        Set<LivingEntity> targets = super.perform(attacker, user);
        final BlockHitResult hitResult = JUtils.genericBlockRaycast(user.level(), user, 5, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE);
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            final BlockPos hitPos = hitResult.getBlockPos();
            attacker.getEntityData().set(SIPHON_POS, Optional.of(hitPos));

            float gain = user.level().getBlockState(hitPos).is(JTagRegistry.IRON_BLOCKS) ? 3f : 1.5f;
            if (attacker.getEntityData().get(MetallicaEntity.INVISIBLE)) gain /= 2.0f;

            attacker.addIron(gain);
        } else {
            attacker.getEntityData().set(SIPHON_POS, Optional.empty());
        }

        return targets;
    }

    @Override
    public StandEntity.MoveSelectionResult specificMoveSelectionCriterion(MetallicaEntity attacker, LivingEntity mob,
                                                                          LivingEntity target, int stunTicks,
                                                                          int enemyMoveStun, double distance, StandEntity<?, ?> enemyStand,
                                                                          AbstractMove<?, ?> enemyAttack) {
        return attacker.getIron() >= MetallicaEntity.IRON_MAX ? StandEntity.MoveSelectionResult.STOP : StandEntity.MoveSelectionResult.PASS;
    }

    @Override
    protected @NonNull HarvestMove getThis() {
        return this;
    }

    @Override
    public @NonNull HarvestMove copy() {
        return copyExtras(new HarvestMove(getDuration(), getMoveDistance(), getInterval()));
    }

    public static class Type extends AbstractBarrageAttack.Type<HarvestMove> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<HarvestMove>, HarvestMove> buildCodec(RecordCodecBuilder.Instance<HarvestMove> instance) {
            return instance.group(extras(), attackExtras(), duration(), moveDistance(), interval())
                    .apply(instance, applyAttackExtras(HarvestMove::new));
        }
    }
}
