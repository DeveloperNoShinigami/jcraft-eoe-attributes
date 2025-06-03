package net.arna.jcraft.common.attack.moves.cream;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import lombok.NonNull;
import net.arna.jcraft.api.attack.MoveType;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.entity.stand.CreamEntity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

@Getter
public final class BallModeExitMove extends AbstractMove<BallModeExitMove, CreamEntity> {
    public BallModeExitMove(final int cooldown, final int windup, final int duration, final float moveDistance) {
        super(cooldown, windup, duration, moveDistance);
    }

    @Override
    public @NonNull MoveType<BallModeExitMove> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final CreamEntity attacker, final LivingEntity user, final MoveContext ctx) {
        attacker.endHalfBall();
        return Set.of();
    }

    @Override
    protected @NonNull BallModeExitMove getThis() {
        return this;
    }

    @Override
    public @NonNull BallModeExitMove copy() {
        return copyExtras(new BallModeExitMove(getCooldown(), getWindup(), getDuration(), getMoveDistance()));
    }

    public static class Type extends AbstractMove.Type<BallModeExitMove> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NotNull App<RecordCodecBuilder.Mu<BallModeExitMove>, BallModeExitMove> buildCodec(RecordCodecBuilder.Instance<BallModeExitMove> instance) {
            return baseDefault(instance, BallModeExitMove::new);
        }
    }
}
