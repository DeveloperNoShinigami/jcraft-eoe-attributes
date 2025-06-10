package net.arna.jcraft.common.attack.moves.dirtydeedsdonedirtcheap;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.api.attack.MoveType;
import net.arna.jcraft.api.attack.StateContainer;
import net.arna.jcraft.api.attack.moves.AbstractGrabAttack;
import net.arna.jcraft.api.attack.moves.AbstractMove;
import net.arna.jcraft.common.entity.stand.D4CEntity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class D4CGrabAttack extends AbstractGrabAttack<D4CGrabAttack, D4CEntity, D4CEntity.State> {
    public D4CGrabAttack(final int cooldown, final int windup, final int duration, final float moveDistance,
                         final float damage, final int stun, final float hitboxSize, final float knockback,
                         final float offset, final AbstractMove<?, ? super D4CEntity> hitMove, final int grabDuration,
                         final double grabOffset) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset, hitMove,
                StateContainer.of(D4CEntity.State.THROW_HIT), grabDuration, grabOffset);
    }

    @Override
    public @NotNull MoveType<D4CGrabAttack> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public void onInitiate(D4CEntity attacker) {
        super.onInitiate(attacker);

        attacker.equipRevolver();
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final D4CEntity attacker, final LivingEntity user) {
        Set<LivingEntity> targets = super.perform(attacker, user);

        if (targets.isEmpty()) {
            attacker.getMainHandItem().shrink(1);
        }

        return targets;
    }

    @Override
    protected @NonNull D4CGrabAttack getThis() {
        return this;
    }

    @Override
    public @NonNull D4CGrabAttack copy() {
        return copyExtras(new D4CGrabAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(),
                getStun(), getHitboxSize(), getKnockback(), getOffset(), getHitMove(), getGrabDuration(), getGrabOffset()));
    }

    public static class Type extends AbstractGrabAttack.Type<D4CGrabAttack> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NotNull App<RecordCodecBuilder.Mu<D4CGrabAttack>, D4CGrabAttack> buildCodec(RecordCodecBuilder.Instance<D4CGrabAttack> instance) {
            return instance.group(extras(), attackExtras(), cooldown(), windup(), duration(), moveDistance(), damage(),
                            stun(), hitboxSize(), knockback(), offset(), this.<D4CEntity>hitMove(), grabDuration(), grabOffset())
                    .apply(instance, applyAttackExtras(D4CGrabAttack::new));
        }
    }
}
