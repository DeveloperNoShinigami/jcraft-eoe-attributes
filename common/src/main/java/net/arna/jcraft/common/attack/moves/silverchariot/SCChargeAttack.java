package net.arna.jcraft.common.attack.moves.silverchariot;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.arna.jcraft.api.attack.MoveType;
import net.arna.jcraft.api.attack.moves.AbstractChargeAttack;
import net.arna.jcraft.common.entity.stand.SilverChariotEntity;
import net.arna.jcraft.api.stand.StandEntity;
import net.minecraft.world.phys.Vec3;

public final class SCChargeAttack extends AbstractChargeAttack<SCChargeAttack, SilverChariotEntity, SilverChariotEntity.State> {
    @Getter @Setter
    private Vec3 lookDir;

    public SCChargeAttack(final int cooldown, final int windup, final int duration, final float moveDistance, final float damage, final int stun,
                          final float hitboxSize, final float knockback, final float offset) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset, SilverChariotEntity.State.P_CHARGE_HIT);
    }

    @Override
    public @NonNull MoveType<SCChargeAttack> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public void onInitiate(final SilverChariotEntity attacker) {
        super.onInitiate(attacker);
        lookDir = attacker.getUserOrThrow().getLookAngle();
    }

    @Override
    protected Vec3 advanceChargePos(final StandEntity<?, ?> attacker, final float moveDistance, final int windupPoint) {
        return attacker.position().add(lookDir.scale(moveDistance / windupPoint));
    }

    @NonNull
    @Override
    protected SCChargeAttack getThis() {
        return this;
    }

    @NonNull
    @Override
    public SCChargeAttack copy() {
        return copyExtras(new SCChargeAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(), getStun(),
                getHitboxSize(), getKnockback(), getOffset()));
    }

    public static class Type extends AbstractChargeAttack.Type<SCChargeAttack> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<SCChargeAttack>, SCChargeAttack> buildCodec(RecordCodecBuilder.Instance<SCChargeAttack> instance) {
            return attackDefault(instance, SCChargeAttack::new);
        }
    }
}
