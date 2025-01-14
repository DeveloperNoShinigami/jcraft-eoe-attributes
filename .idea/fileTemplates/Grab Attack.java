package ${PACKAGE_NAME};

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.core.data.MoveType;
import net.arna.jcraft.common.attack.core.data.StateContainer;
import net.arna.jcraft.common.attack.moves.base.AbstractGrabAttack;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class ${NAME} extends AbstractGrabAttack<${NAME}, ${ATTACKER}> {
    public ${NAME}(final int cooldown, final int windup, final int duration, final float moveDistance,
                   final float damage, final int stun, final float hitboxSize, final float knockback,
                   final float offset, final AbstractMove<?, ? super ${ATTACKER}> hitMove, final int grabDuration,
                   final double grabOffset) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset, hitMove,
                StateContainer.of(${ATTACKER}.State.GRAB_HIT), grabDuration, grabOffset);
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
                getStun(), getHitboxSize(), getKnockback(), getOffset(), getHitMove(), getGrabDuration(), getGrabOffset()));
    }

    public static final class Type extends AbstractGrabAttack.Type<${NAME}> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NotNull App<RecordCodecBuilder.Mu<${NAME}>, ${NAME}> buildCodec(RecordCodecBuilder.Instance<${NAME}> instance) {
            return instance.group(extras(), attackExtras(), cooldown(), windup(), duration(), moveDistance(), damage(),
                            stun(), hitboxSize(), knockback(), offset(), this.<${ATTACKER}>hitMove(), grabDuration(), grabOffset())
                    .apply(instance, applyAttackExtras(${NAME}::new));
        }
    }
}
