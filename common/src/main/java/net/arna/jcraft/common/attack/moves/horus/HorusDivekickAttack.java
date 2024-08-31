package net.arna.jcraft.common.attack.moves.horus;

import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.core.ctx.MoveVariable;
import net.arna.jcraft.common.attack.moves.base.AbstractChargeAttack;
import net.arna.jcraft.common.entity.stand.HorusEntity;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.util.JUtils;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public final class HorusDivekickAttack extends AbstractChargeAttack<HorusDivekickAttack, HorusEntity, HorusEntity.State> {
    public static final MoveVariable<Vec3> LOOK_DIR = new MoveVariable<>(Vec3.class);
    public HorusDivekickAttack(int cooldown, int windup, int duration, float moveDistance, float damage, int stun, float hitboxSize, float knockback, float offset, HorusEntity.State hitAnimState) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset, hitAnimState);
    }

    private static final MobEffectInstance LEVITATE = new MobEffectInstance(MobEffects.SLOW_FALLING, 9, 2, true, false);
    @Override
    public void onInitiate(HorusEntity attacker) {
        super.onInitiate(attacker);
        LivingEntity user = attacker.getUserOrThrow();
        if (attacker.isFree()) attacker.setFree(false);

        attacker.getMoveContext().set(LOOK_DIR, user.getLookAngle().scale(0.65));

        int duration = 20 + (int)user.getXRot();
        if (duration < getWindup()) duration = getWindup();
        withDuration(duration);

        user.addEffect(LEVITATE);
    }

    @Override
    protected Vec3 advanceChargePos(StandEntity<?, ?> attacker, float moveDistance, int windupPoint) {
        return attacker.position().add(
                attacker.getMoveContext().get(LOOK_DIR)
        );
    }

    @Override
    protected void tickChargeAttack(StandEntity<HorusEntity, HorusEntity.State> attacker, boolean shouldPerform, float moveDistance, int windupPoint) {
        super.tickChargeAttack(attacker, shouldPerform, moveDistance, windupPoint);
        if (attacker.getMoveStun() < windupPoint) {
            if (attacker.getBlockStateOn().canOcclude()) {
                endCharge((HorusEntity) attacker);
            } else {
                LivingEntity user = attacker.getUserOrThrow();
                GravityChangerAPI.setWorldVelocity(user, attacker.getMoveContext().get(LOOK_DIR));
                JUtils.syncVelocityUpdate(user);
                user.resetFallDistance();
            }
        }
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
