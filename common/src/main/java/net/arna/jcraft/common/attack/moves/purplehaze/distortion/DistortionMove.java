package net.arna.jcraft.common.attack.moves.purplehaze.distortion;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.data.MoveType;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.entity.stand.PurpleHazeDistortionEntity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;

public class DistortionMove extends AbstractMove<DistortionMove, PurpleHazeDistortionEntity> {
    public DistortionMove(int cooldown) {
        super(cooldown, 0, 0, 0);
    }

    @Override
    public @NonNull MoveType<DistortionMove> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(PurpleHazeDistortionEntity attacker, LivingEntity user, MoveContext ctx) {
        attacker.nextPoisonType();
        return Set.of();
    }

    @Override
    protected @NonNull DistortionMove getThis() {
        return this;
    }

    @Override
    public @NonNull DistortionMove copy() {
        return copyExtras(new DistortionMove(getCooldown()));
    }

    public static class Type extends AbstractMove.Type<DistortionMove> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<DistortionMove>, DistortionMove> buildCodec(RecordCodecBuilder.Instance<DistortionMove> instance) {
            return instance.group(extras(), cooldown()).apply(instance, applyExtras(DistortionMove::new));
        }
    }
}
