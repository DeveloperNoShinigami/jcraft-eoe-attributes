package ${PACKAGE_NAME};

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.core.data.MoveType;
import net.arna.jcraft.common.attack.moves.base.AbstractSimpleAttack;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;

public class ${NAME} extends AbstractSimpleAttack<${NAME}, ${ATTACKER}> {
    public ${NAME}(final int cooldown, final int windup, final int duration, final float moveDistance,
                        final float damage, final int stun, final float hitboxSize, final float knockback, final float offset) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset);
    }

    @Override
    public @NonNull MoveType<${NAME}> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final ${ATTACKER} attacker, final LivingEntity user, final MoveContext ctx) {
        Set<LivingEntity> targets = super.perform(attacker, user, ctx);

        // TODO Implement attack logic here

        return targets;
    }

    @Override
    protected @NonNull ${NAME} getThis() {
        return this;
    }

    @Override
    public @NonNull ${NAME} copy() {
        return copyExtras(new ${NAME}(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(),
                getStun(), getHitboxSize(), getKnockback(), getOffset()));
    }

    public static final class Type extends AbstractSimpleAttack.Type<${NAME}> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<${NAME}>, ${NAME}>
        buildCodec(final RecordCodecBuilder.Instance<${NAME}> instance) {
            return attackDefault(instance, ${NAME}::new);
        }
    }
}
