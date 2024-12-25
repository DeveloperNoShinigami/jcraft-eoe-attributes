package net.arna.jcraft.common.attack.moves.silverchariot;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.data.MoveType;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.core.ctx.MoveVariable;
import net.arna.jcraft.common.attack.moves.base.AbstractChargeAttack;
import net.arna.jcraft.common.entity.stand.SilverChariotEntity;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.minecraft.world.phys.Vec3;

public final class SCChargeAttack extends AbstractChargeAttack<SCChargeAttack, SilverChariotEntity, SilverChariotEntity.State> {
    public static final MoveVariable<Vec3> LOOK_DIR = new MoveVariable<>(Vec3.class);

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
        attacker.getMoveContext().set(LOOK_DIR, attacker.getUserOrThrow().getLookAngle());
    }

    @Override
    protected Vec3 advanceChargePos(final StandEntity<?, ?> attacker, final float moveDistance, final int windupPoint) {
        return attacker.position().add(
                attacker.getMoveContext().get(LOOK_DIR).scale(moveDistance / windupPoint)
        );
    }

    @Override
    public void registerExtraContextEntries(final MoveContext ctx) {
        ctx.register(LOOK_DIR);
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
