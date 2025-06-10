package net.arna.jcraft.common.attack.moves.thehand;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.api.attack.MoveType;

public class SimpleEraseAttack extends AbstractEraseAttack<SimpleEraseAttack> {
    public SimpleEraseAttack(int cooldown, int windup, int duration, float moveDistance, float damage, int stun,
                             float hitboxSize, float knockback, float offset) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset);
    }

    @Override
    public @NonNull MoveType<SimpleEraseAttack> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    protected @NonNull SimpleEraseAttack getThis() {
        return this;
    }

    @Override
    public @NonNull SimpleEraseAttack copy() {
        return copyExtras(new SimpleEraseAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(),
                getDamage(), getStun(), getHitboxSize(), getKnockback(), getOffset()));
    }

    public static class Type extends AbstractEraseAttack.Type<SimpleEraseAttack> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<SimpleEraseAttack>, SimpleEraseAttack> buildCodec(RecordCodecBuilder.Instance<SimpleEraseAttack> instance) {
            return attackDefault(instance, SimpleEraseAttack::new);
        }
    }
}
