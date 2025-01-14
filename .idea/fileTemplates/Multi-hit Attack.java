package ${PACKAGE_NAME};

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntCollection;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.core.data.MoveType;
import net.arna.jcraft.common.attack.moves.base.AbstractMultiHitAttack;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;

public class ${NAME} extends AbstractMultiHitAttack<${NAME}, ${ATTACKER}> {
    public ${NAME}(final int cooldown, final int duration, final float moveDistance, final float damage, final int stun,
                   final float hitboxSize, final float knockback, final float offset, final @NonNull IntCollection hitMoments) {
        super(cooldown, duration, moveDistance, damage, stun, hitboxSize, knockback, offset, hitMoments);
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
        return copyExtras(new ${NAME}(getCooldown(), getDuration(), getMoveDistance(), getDamage(),
                getStun(), getHitboxSize(), getKnockback(), getOffset(), getHitMoments()));
    }

    public static final class Type extends AbstractMultiHitAttack.Type<${NAME}> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<${NAME}>, ${NAME}>
        buildCodec(final RecordCodecBuilder.Instance<${NAME}> instance) {
            return multiHitDefault(instance, ${NAME}::new);
        }
    }
}
