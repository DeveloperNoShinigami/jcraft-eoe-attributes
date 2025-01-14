package net.arna.jcraft.common.attack.moves.shadowtheworld;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.data.MoveType;
import net.arna.jcraft.common.attack.moves.base.AbstractChargeAttack;
import net.arna.jcraft.common.entity.stand.ShadowTheWorldEntity;

public final class STWChargeAttack extends AbstractChargeAttack<STWChargeAttack, ShadowTheWorldEntity, ShadowTheWorldEntity.State> {
    public STWChargeAttack(final int cooldown, final int windup, final int duration, final float moveDistance, final float damage, final int stun,
                           final float hitboxSize, final float knockback, final float offset) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset, ShadowTheWorldEntity.State.CHARGE_HIT);
    }

    @Override
    public @NonNull MoveType<STWChargeAttack> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    protected @NonNull STWChargeAttack getThis() {
        return this;
    }

    @Override
    public @NonNull STWChargeAttack copy() {
        return copyExtras(new STWChargeAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(), getStun(),
                getHitboxSize(), getKnockback(), getOffset()));
    }

    public static class Type extends AbstractChargeAttack.Type<STWChargeAttack> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<STWChargeAttack>, STWChargeAttack> buildCodec(RecordCodecBuilder.Instance<STWChargeAttack> instance) {
            return attackDefault(instance, STWChargeAttack::new);
        }
    }
}
