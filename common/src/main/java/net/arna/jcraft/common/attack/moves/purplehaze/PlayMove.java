package net.arna.jcraft.common.attack.moves.purplehaze;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.data.MoveType;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.entity.stand.PurpleHazeEntity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;

public class PlayMove extends AbstractMove<PlayMove, PurpleHazeEntity> {
    public PlayMove(int cooldown, int windup, int duration) {
        super(cooldown, windup, duration, 0);
    }

    @Override
    public @NonNull MoveType<PlayMove> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(PurpleHazeEntity attacker, LivingEntity user, MoveContext ctx) {
        attacker.setCurrentMove(null);
        attacker.setMoveStun(0);
        attacker.desummon();
        return Set.of();
    }

    @Override
    protected @NonNull PlayMove getThis() {
        return this;
    }

    @Override
    public @NonNull PlayMove copy() {
        return copyExtras(new PlayMove(getCooldown(), getWindup(), getDuration()));
    }

    public static class Type extends AbstractMove.Type<PlayMove> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<PlayMove>, PlayMove> buildCodec(RecordCodecBuilder.Instance<PlayMove> instance) {
            return instance.group(extras(), cooldown(), windup(), duration()).apply(instance, applyExtras(PlayMove::new));
        }
    }
}
