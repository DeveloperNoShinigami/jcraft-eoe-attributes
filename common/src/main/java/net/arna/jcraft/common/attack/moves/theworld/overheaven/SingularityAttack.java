package net.arna.jcraft.common.attack.moves.theworld.overheaven;

import lombok.NonNull;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.attack.moves.base.AbstractSimpleAttack;
import net.arna.jcraft.common.entity.damage.JDamageSources;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.entity.stand.TheWorldOverHeavenEntity;
import net.arna.jcraft.registry.JStatusRegistry;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public final class SingularityAttack extends AbstractSimpleAttack<SingularityAttack, TheWorldOverHeavenEntity> {
    private final boolean blockBypass;

    public SingularityAttack(final int cooldown, final int windup, final int duration, final float moveDistance, final float damage, final int stun,
                             final float hitboxSize, final float knockback, final float offset, final boolean blockBypass) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset);
        this.blockBypass = blockBypass;
    }

    @Override
    protected void processTarget(final TheWorldOverHeavenEntity attacker, final LivingEntity target, final Vec3 kbVec, final DamageSource damageSource) {
        StandEntity.damageLogic(attacker.getEntityWorld(), target, kbVec, getStun(), getStunType().ordinal(), true,
                0, isLift(), getBlockStun(), damageSource, attacker.getUserOrThrow(), getHitAnimation(), true, false);

        if (blockBypass) {
            target.removeEffect(JStatusRegistry.DAZED.get());
            JCraft.stun(target, getStun(), 0, attacker);
        }

        StandEntity.trueDamage(6, JDamageSources.stand(attacker), target);
    }

    @Override
    protected @NonNull SingularityAttack getThis() {
        return this;
    }

    @Override
    public @NonNull SingularityAttack copy() {
        return copyExtras(new SingularityAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(),
                getDamage(), getStun(), getHitboxSize(), getKnockback(), getOffset(), blockBypass));
    }
}
