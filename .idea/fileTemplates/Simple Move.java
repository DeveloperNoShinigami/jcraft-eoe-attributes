package ${PACKAGE_NAME};

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.IAttacker;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.core.data.MoveType;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;

public class ${NAME} extends AbstractMove<${NAME}, ${ATTACKER}> {
    public ${NAME}(final int cooldown, final int windup, final int duration, final float moveDistance) {
        super(cooldown, windup, duration, moveDistance);
    }

    @Override
    public @NonNull MoveType<${NAME}> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final ${ATTACKER} attacker, final LivingEntity user, final MoveContext ctx) {
        return Set.of();
    }

    @Override
    protected @NonNull ${NAME} getThis() {
        return this;
    }

    @Override
    public @NonNull ${NAME} copy() {
        return copyExtras(new ${NAME}(getCooldown(), getWindup(), getDuration(), getMoveDistance()));
    }

    public static final class Type extends AbstractMove.Type<${NAME}> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<${NAME}>, ${NAME}>
        buildCodec(final RecordCodecBuilder.Instance<${NAME}> instance) {
            return baseDefault(instance, ${NAME}::new);
        }
    }
}
