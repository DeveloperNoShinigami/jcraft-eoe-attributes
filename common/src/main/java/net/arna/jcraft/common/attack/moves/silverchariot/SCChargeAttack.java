package net.arna.jcraft.common.attack.moves.silverchariot;

import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.core.ctx.MoveVariable;
import net.arna.jcraft.common.attack.moves.base.AbstractChargeAttack;
import net.arna.jcraft.common.entity.stand.SilverChariotEntity;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class SCChargeAttack extends AbstractChargeAttack<SCChargeAttack, SilverChariotEntity, SilverChariotEntity.State> {
    public static final MoveVariable<Vec3> LOOK_DIR = new MoveVariable<>(Vec3.class);
    public SCChargeAttack(int cooldown, int windup, int duration, float moveDistance, float damage, int stun,
                          float hitboxSize, float knockback, float offset, SilverChariotEntity.State hitAnimState) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset, hitAnimState);
    }

    @Override
    public void onInitiate(SilverChariotEntity attacker) {
        super.onInitiate(attacker);
        attacker.getMoveContext().set(LOOK_DIR, attacker.getUserOrThrow().getLookAngle());
    }

    @Override
    protected Vec3 advanceChargePos(StandEntity<?, ?> attacker, float moveDistance, int windupPoint) {
        return attacker.position().add(
                attacker.getMoveContext().get(LOOK_DIR).scale(moveDistance / windupPoint)
        );
    }

    @Override
    public void registerContextEntries(MoveContext ctx) {
        ctx.register(LOOK_DIR);
    }

    @NotNull
    @Override
    protected SCChargeAttack getThis() {
        return this;
    }

    @NotNull
    @Override
    public SCChargeAttack copy() {
        return copyExtras(new SCChargeAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(), getStun(),
                getHitboxSize(), getKnockback(), getOffset(), getHitAnimState()));
    }
}
