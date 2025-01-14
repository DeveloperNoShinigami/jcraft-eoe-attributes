package net.arna.jcraft.common.attack.moves.cream;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.MobilityType;
import net.arna.jcraft.common.attack.core.data.MoveType;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.entity.stand.CreamEntity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

@Getter
public final class BallModeEnterMove extends AbstractMove<BallModeEnterMove, CreamEntity> {
    public BallModeEnterMove(final int cooldown, final int windup, final int duration, final float moveDistance) {
        super(cooldown, windup, duration, moveDistance);
        mobilityType = MobilityType.FLIGHT;
    }

    @Override
    public @NonNull MoveType<BallModeEnterMove> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final CreamEntity attacker, final LivingEntity user, final MoveContext ctx) {
        attacker.beginHalfBall();
        return Set.of();
    }

    @Override
    protected @NonNull BallModeEnterMove getThis() {
        return this;
    }

    @Override
    public @NonNull BallModeEnterMove copy() {
        return copyExtras(new BallModeEnterMove(getCooldown(), getWindup(), getDuration(), getMoveDistance()));
    }

    public static class Type extends AbstractMove.Type<BallModeEnterMove> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NotNull App<RecordCodecBuilder.Mu<BallModeEnterMove>, BallModeEnterMove> buildCodec(RecordCodecBuilder.Instance<BallModeEnterMove> instance) {
            return baseDefault(instance, BallModeEnterMove::new);
        }
    }
}
