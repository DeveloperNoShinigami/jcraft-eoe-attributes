package ${PACKAGE_NAME};

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.attack.core.data.MoveType;
import net.arna.jcraft.common.attack.moves.base.AbstractCounterAttack;
import net.arna.jcraft.common.attack.moves.shared.CounterMissMove;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;

public class ${NAME} extends AbstractCounterAttack<${NAME}, ${ATTACKER}> {
    private final CounterMissMove<${ATTACKER}> counterMiss = new CounterMissMove<>(${MISS_DURATION});

    public ${NAME}(final int cooldown, final int windup, final int duration, final float moveDistance) {
        super(cooldown, windup, duration, moveDistance);
    }

    @Override
    public @NonNull MoveType<${NAME}> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public void whiff(final @NonNull ${ATTACKER} attacker, final @NonNull LivingEntity user) {
        attacker.setMove(counterMiss, ${ATTACKER}.State.COUNTER_MISS);
        JCraft.stun(user, counterMiss.getDuration(), 0);
    }

    @Override
    public void counter(final @NonNull ${ATTACKER} attacker, final Entity countered, final DamageSource counteredDamageSource) {
        super.counter(attacker, countered, counteredDamageSource);

        // TODO Implement counter logic here
    }

    @Override
    protected @NonNull ${NAME} getThis() {
        return this;
    }

    @Override
    public @NonNull ${NAME} copy() {
        return copyExtras(new ${NAME}(getCooldown(), getWindup(), getDuration(), getMoveDistance()));
    }

    public static final class Type extends AbstractCounterAttack.Type<${NAME}> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<${NAME}>, ${NAME}>
        buildCodec(final RecordCodecBuilder.Instance<${NAME}> instance) {
            return baseDefault(instance, ${NAME}::new);
        }
    }
}
