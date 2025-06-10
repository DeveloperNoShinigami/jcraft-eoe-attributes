package net.arna.jcraft.common.attack.moves.thefool;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.api.attack.MoveType;
import net.arna.jcraft.api.attack.moves.AbstractSimpleAttack;
import net.arna.jcraft.common.entity.stand.TheFoolEntity;

public class TFLaunchAttack extends AbstractSimpleAttack<TFLaunchAttack, TheFoolEntity> {
    public TFLaunchAttack(int cooldown, int windup, int duration, float moveDistance, float damage, int stun,
                          float hitboxSize, float knockback, float offset) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset);
    }

    @Override
    public @NonNull MoveType<TFLaunchAttack> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public void onInitiate(TheFoolEntity attacker) {
        super.onInitiate(attacker);

        attacker.setSand(true);
    }

    @Override
    protected @NonNull TFLaunchAttack getThis() {
        return this;
    }

    @Override
    public @NonNull TFLaunchAttack copy() {
        return copyExtras(new TFLaunchAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(), getStun(),
                getHitboxSize(), getKnockback(), getOffset()));
    }

    public static class Type extends AbstractSimpleAttack.Type<TFLaunchAttack> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<TFLaunchAttack>, TFLaunchAttack> buildCodec(RecordCodecBuilder.Instance<TFLaunchAttack> instance) {
            return attackDefault(instance, TFLaunchAttack::new);
        }
    }
}
