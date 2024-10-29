package net.arna.jcraft.common.attack.moves.thehand;

import it.unimi.dsi.fastutil.ints.IntSet;
import lombok.NonNull;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.attack.core.BlockableType;
import net.arna.jcraft.common.attack.moves.base.AbstractMultiHitAttack;
import net.arna.jcraft.common.entity.damage.JDamageSources;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.entity.stand.TheHandEntity;
import net.arna.jcraft.common.util.JParticleType;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.registry.JStatusRegistry;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public final class MultiHitEraseAttack extends AbstractMultiHitAttack<MultiHitEraseAttack, TheHandEntity> {
    public MultiHitEraseAttack(final int cooldown, final int duration, final float moveDistance, final float damage, final int stun,
                               final float hitboxSize, final float knockback, final float offset, final IntSet hitMoments) {
        super(cooldown, duration, moveDistance, damage, stun, hitboxSize, knockback, offset, hitMoments);
        withBlockableType(BlockableType.NON_BLOCKABLE);
        withHitSpark(JParticleType.INVERTED_HIT_SPARK_3);
    }

    @Override
    protected void processTarget(final TheHandEntity attacker, final LivingEntity target, final Vec3 kbVec, final DamageSource damageSource) {
        StandEntity.damageLogic(attacker.getEntityWorld(), target, kbVec, getStun(), getStunType().ordinal(), true,
                0, isLift(), getBlockStun(), damageSource, attacker.getUserOrThrow(), getHitAnimation(), true, false);

        target.removeEffect(JStatusRegistry.DAZED.get());
        StandEntity<?, ?> stand = JUtils.getStand(target);
        if (stand != null) stand.blocking = false;
        JCraft.stun(target, getStun(), 0, attacker);
        StandEntity.trueDamage(getDamage(), JDamageSources.stand(attacker), target);
    }

    @Override
    protected @NonNull MultiHitEraseAttack getThis() {
        return this;
    }

    @Override
    public @NonNull MultiHitEraseAttack copy() {
        return copyExtras(new MultiHitEraseAttack(getCooldown(), getDuration(), getMoveDistance(),
                getDamage(), getStun(), getHitboxSize(), getKnockback(), getOffset(), getHitMoments()));
    }
}
