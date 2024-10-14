package net.arna.jcraft.common.attack.moves.thehand;

import lombok.NonNull;
import net.arna.jcraft.common.attack.core.BlockableType;
import net.arna.jcraft.common.attack.moves.base.AbstractSimpleAttack;
import net.arna.jcraft.common.entity.damage.JDamageSources;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.entity.stand.TheHandEntity;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.registry.JStatusRegistry;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public final class EraseAttack extends AbstractSimpleAttack<EraseAttack, TheHandEntity> {
    public EraseAttack(final int cooldown, final int windup, final int duration, final float moveDistance, final float damage, final int stun,
                       final float hitboxSize, final float knockback, final float offset) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset);
        withBlockableType(BlockableType.NON_BLOCKABLE);
    }

    @Override
    protected void processTarget(final TheHandEntity attacker, final LivingEntity target, final Vec3 kbVec, final DamageSource damageSource) {
        StandEntity.damageLogic(attacker.getEntityWorld(), target, kbVec, getStun(), getStunType().ordinal(), true,
                0, isLift(), getBlockStun(), damageSource, attacker.getUserOrThrow(), getHitAnimation(), true, false);

        target.removeEffect(JStatusRegistry.DAZED.get());
        StandEntity<?, ?> stand = JUtils.getStand(target);
        if (stand != null) stand.blocking = false;
        StandEntity.trueDamage(getDamage(), JDamageSources.stand(attacker), target);
        target.addEffect(
                new MobEffectInstance(JStatusRegistry.KNOCKDOWN.get(), 15, 0)
        );
    }

    @Override
    protected @NonNull EraseAttack getThis() {
        return this;
    }

    @Override
    public @NonNull EraseAttack copy() {
        return copyExtras(new EraseAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(),
                getDamage(), getStun(), getHitboxSize(), getKnockback(), getOffset()));
    }
}
