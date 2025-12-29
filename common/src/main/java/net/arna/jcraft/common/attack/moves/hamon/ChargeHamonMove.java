package net.arna.jcraft.common.attack.moves.hamon;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.api.MoveSelectionResult;
import net.arna.jcraft.api.attack.MoveType;
import net.arna.jcraft.api.attack.enums.MoveInputType;
import net.arna.jcraft.api.attack.moves.AbstractBarrageAttack;
import net.arna.jcraft.api.attack.moves.AbstractMove;
import net.arna.jcraft.api.stand.StandEntity;
import net.arna.jcraft.common.spec.HamonSpec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Set;

public class ChargeHamonMove extends AbstractBarrageAttack<ChargeHamonMove, HamonSpec> {
    public ChargeHamonMove(int duration, float moveDistance, int interval) {
        super(0, 0, duration, moveDistance, 0, 0, 0, 0, 0, interval);
        withHoldable();
    }

    @Override
    public @NonNull MoveType<ChargeHamonMove> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public void onInitiate(HamonSpec attacker) {
        super.onInitiate(attacker);
        attacker.flashClientHamonBar();
    }

    @Override
    public void onUserMoveInput(final HamonSpec attacker, final MoveInputType type, final boolean pressed, final  boolean moveInitiated) {
        super.onUserMoveInput(attacker, type, pressed, moveInitiated);
        // Must be held
        if (type.getMoveClass() == getMoveClass() && !pressed) {
            attacker.cancelMove();
            attacker.updateClientHamonBar();
        }
    }

    @Override
    public void activeTick(HamonSpec attacker, int moveStun) {
        super.activeTick(attacker, moveStun);

        LivingEntity user = attacker.getUser();
        if (user instanceof Player) return;

        // AI charge. Cancel automatically if charge is full.
        if (attacker.getCharge() >= HamonSpec.MAX_CHARGE)
            attacker.cancelMove();
    }

    @Override
    public @NonNull Set<LivingEntity> perform(HamonSpec attacker, LivingEntity user) {
        float add = 0.2f;

        final float healthRatio = user.getHealth() / user.getMaxHealth();
        add *= healthRatio;

        attacker.drainCharge(-0.2f - add);

        return Set.of();
    }

    @Override
    public MoveSelectionResult specificMoveSelectionCriterion(HamonSpec attacker, LivingEntity mob,
                                                              LivingEntity target, int stunTicks,
                                                              int enemyMoveStun, double distance, StandEntity<?, ?> enemyStand,
                                                              AbstractMove<?, ?> enemyAttack) {
        return attacker.getCharge() >= HamonSpec.MAX_CHARGE ? MoveSelectionResult.STOP : MoveSelectionResult.PASS;
    }

    @Override
    protected @NonNull ChargeHamonMove getThis() {
        return this;
    }

    @Override
    public @NonNull ChargeHamonMove copy() {
        return copyExtras(new ChargeHamonMove(getDuration(), getMoveDistance(), getInterval()));
    }

    public static class Type extends AbstractBarrageAttack.Type<ChargeHamonMove> {
        public static final ChargeHamonMove.Type INSTANCE = new ChargeHamonMove.Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<ChargeHamonMove>, ChargeHamonMove> buildCodec(RecordCodecBuilder.Instance<ChargeHamonMove> instance) {
            return instance.group(extras(), attackExtras(), duration(), moveDistance(), interval())
                    .apply(instance, applyAttackExtras(ChargeHamonMove::new));
        }
    }
}
