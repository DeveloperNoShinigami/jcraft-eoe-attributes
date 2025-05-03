package net.arna.jcraft.common.attack.actions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.arna.jcraft.common.attack.core.IAttacker;
import net.arna.jcraft.common.attack.core.MoveAction;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.core.data.MoveActionType;
import net.arna.jcraft.common.util.JCodecUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;

import java.util.Set;

@Getter
@RequiredArgsConstructor(staticName = "of")
public class ScoreboardAction extends MoveAction<ScoreboardAction, IAttacker<?, ?>> {
    private final String objective;
    private final ScoreboardActionType actionType;
    private final int value;

    @Override
    public void perform(final IAttacker<?, ?> attacker, final LivingEntity user, final MoveContext ctx, final Set<LivingEntity> targets) {
        MinecraftServer server = attacker.getEntityWorld().getServer();
        if (server == null) {
            return;
        }

        ServerScoreboard scoreboard = server.getScoreboard();
        Objective objective = scoreboard.getOrCreateObjective(this.objective);
        Score score = scoreboard.getOrCreatePlayerScore(user.getScoreboardName(), objective);

        switch (this.actionType) {
            case ADD -> score.setScore(score.getScore() + this.value);
            case MULTIPLY -> score.setScore(score.getScore() * this.value);
            case SET -> score.setScore(this.value);
        }
    }

    @Override
    public @NonNull MoveActionType<ScoreboardAction> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements MoveActionType<ScoreboardAction> {
        public static final Type INSTANCE = new Type();
        @Getter
        private final Codec<ScoreboardAction> codec = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.STRING.fieldOf("objective").forGetter(ScoreboardAction::getObjective),
                        JCodecUtils.createEnumCodec(ScoreboardActionType.class).fieldOf("actionType").forGetter(ScoreboardAction::getActionType),
                        Codec.INT.fieldOf("value").forGetter(ScoreboardAction::getValue)
                ).apply(instance, ScoreboardAction::of));
    }

    @Getter
    public enum ScoreboardActionType {
        ADD("add"),
        MULTIPLY("multiply"),
        SET("set");

        private final String name;

        ScoreboardActionType(String name) {
            this.name = name;
        }
    }
}
