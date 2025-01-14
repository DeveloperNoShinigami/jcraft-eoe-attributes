package net.arna.jcraft.common.attack.moves.cmoon;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.data.MoveType;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.entity.stand.CMoonEntity;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public final class GravityShiftMove extends AbstractMove<GravityShiftMove, CMoonEntity> {
    public GravityShiftMove(final int cooldown, final int windup, final int duration, final float moveDistance) {
        super(cooldown, windup, duration, moveDistance);
    }

    @Override
    public @NotNull MoveType<GravityShiftMove> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final CMoonEntity attacker, final LivingEntity user, final MoveContext ctx) {
        JComponentPlatformUtils.getGravityShift(user).startRadial();
        return Set.of();
    }

    @Override
    protected @NonNull GravityShiftMove getThis() {
        return this;
    }

    @Override
    public @NonNull GravityShiftMove copy() {
        return copyExtras(new GravityShiftMove(getCooldown(), getWindup(), getDuration(), getMoveDistance()));
    }

    public static class Type extends AbstractMove.Type<GravityShiftMove> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NotNull App<RecordCodecBuilder.Mu<GravityShiftMove>, GravityShiftMove> buildCodec(RecordCodecBuilder.Instance<GravityShiftMove> instance) {
            return baseDefault(instance, GravityShiftMove::new);
        }
    }
}
