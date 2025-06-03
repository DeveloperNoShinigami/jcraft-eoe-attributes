package net.arna.jcraft.common.attack.moves.shared;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.IAttacker;
import net.arna.jcraft.api.attack.MoveType;
import net.arna.jcraft.common.attack.moves.base.AbstractUppercutAttack;

@Getter
public final class SimpleUppercutAttack<A extends IAttacker<? extends A, ?>> extends AbstractUppercutAttack<SimpleUppercutAttack<A>, A> {
    public SimpleUppercutAttack(final int cooldown, final int windup, final int duration, final float moveDistance, final float damage, final int stun,
                                final float hitboxSize, final float knockback, final float offset, final float strength) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset, strength);
    }

    @Override
    public @NonNull MoveType<SimpleUppercutAttack<A>> getMoveType() {
        return Type.INSTANCE.cast();
    }

    @Override
    protected @NonNull SimpleUppercutAttack<A> getThis() {
        return this;
    }

    @Override
    public @NonNull SimpleUppercutAttack<A> copy() {
        return copyExtras(new SimpleUppercutAttack<>(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(),
                getStun(), getHitboxSize(), getKnockback(), getOffset(), getStrength()));
    }

    public static class Type extends AbstractUppercutAttack.Type<SimpleUppercutAttack<?>> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<SimpleUppercutAttack<?>>, SimpleUppercutAttack<?>>
        buildCodec(RecordCodecBuilder.Instance<SimpleUppercutAttack<?>> instance) {
            return uppercutDefault(instance, SimpleUppercutAttack::new);
        }
    }
}
