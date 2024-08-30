package net.arna.jcraft.common.attack.moves.horus;

import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.core.ctx.MoveVariable;
import net.arna.jcraft.common.attack.moves.base.AbstractChargeAttack;
import net.arna.jcraft.common.entity.stand.HorusEntity;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.util.JUtils;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.Vec3;

public class HorusDivekickAttack extends AbstractChargeAttack<HorusDivekickAttack, HorusEntity, HorusEntity.State> {
    public static final MoveVariable<Vec3> LOOK_DIR = new MoveVariable<>(Vec3.class);
    public HorusDivekickAttack(int cooldown, int windup, int duration, float moveDistance, float damage, int stun, float hitboxSize, float knockback, float offset, HorusEntity.State hitAnimState) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset, hitAnimState);
    }

    private static final MobEffectInstance LEVITATE = new MobEffectInstance(MobEffects.LEVITATION, 9, 2, true, false);
    @Override
    public void onInitiate(HorusEntity attacker) {
        super.onInitiate(attacker);
        attacker.getMoveContext().set(LOOK_DIR, attacker.getUserOrThrow().getLookAngle().scale(1.5));
        attacker.getUserOrThrow().addEffect(LEVITATE);
    }

    @Override
    protected Vec3 advanceChargePos(StandEntity<?, ?> attacker, float moveDistance, int windupPoint) {
        return attacker.position().add(
                attacker.getMoveContext().get(LOOK_DIR).scale(moveDistance / windupPoint)
        );
    }

    @Override
    protected void tickChargeAttack(StandEntity<HorusEntity, HorusEntity.State> attacker, boolean shouldPerform, float moveDistance, int windupPoint) {
        super.tickChargeAttack(attacker, shouldPerform, moveDistance, windupPoint);
        if (attacker.getMoveStun() < windupPoint) {
            JUtils.setVelocity(
                    attacker.getUserOrThrow(),
                    attacker.getMoveContext().get(LOOK_DIR).scale(moveDistance / windupPoint)
            );
            if (attacker.getBlockStateOn().canOcclude()) endCharge((HorusEntity) attacker);
        }
    }

    @Override
    protected void endCharge(HorusEntity attacker) {
        super.endCharge(attacker);
        if (attacker.getUser() != null) attacker.getUserOrThrow().addEffect(LEVITATE);
    }

    @Override
    public void registerContextEntries(MoveContext ctx) {
        ctx.register(LOOK_DIR);
    }

    @Override
    protected @NonNull HorusDivekickAttack getThis() {
        return this;
    }

    @Override
    public @NonNull HorusDivekickAttack copy() {
        return copyExtras(new HorusDivekickAttack(getCooldown(), getWindup(), getDuration(),
                getMoveDistance(), getDamage(), getStun(), getHitboxSize(), getKnockback(), getOffset(), getHitAnimState()));
    }
}
