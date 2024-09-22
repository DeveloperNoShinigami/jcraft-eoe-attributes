package net.arna.jcraft.common.attack.moves.magiciansred;

import lombok.NonNull;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractSimpleAttack;
import net.arna.jcraft.common.entity.projectile.RedBindEntity;
import net.arna.jcraft.common.entity.stand.MagiciansRedEntity;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.registry.JStatusRegistry;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;

public final class RedBindAttack extends AbstractSimpleAttack<RedBindAttack, MagiciansRedEntity> {
    public RedBindAttack(int cooldown, int windup, int duration, float attackDistance, float damage, int stun,
                         float hitboxSize, float knockback, float offset) {
        super(cooldown, windup, duration, attackDistance, damage, stun, hitboxSize, knockback, offset);
    }

    @Override
    public @NonNull Set<LivingEntity> perform(MagiciansRedEntity attacker, LivingEntity user, MoveContext ctx) {
        final Set<LivingEntity> targets = super.perform(attacker, user, ctx);
        if (targets.isEmpty()) {
            return targets;
        }

        final LivingEntity boundEntity = JUtils.getUserIfStand(targets.stream().findFirst().orElseThrow());

        if (JUtils.isBlocking(boundEntity)) {
            return Set.of();
        }

        // Remove Stand
        final StandEntity<?, ?> boundStand = JUtils.getStand(boundEntity);
        if (boundStand != null) {
            boundStand.setCurrentMove(null);
            boundStand.setMoveStun(0);
            boundStand.desummon();
        }

        // Stun
        boundEntity.removeEffect(JStatusRegistry.DAZED.get());
        JCraft.stun(boundEntity, RedBindEntity.LIFE_TIME, 0, user);

        // Create and bind
        final RedBindEntity redBind = new RedBindEntity(attacker.level());
        redBind.setPos(boundEntity.position());
        redBind.setMaster(user);
        redBind.setBoundEntity(boundEntity);
        attacker.level().addFreshEntity(redBind);

        return targets;
    }

    @Override
    protected @NonNull RedBindAttack getThis() {
        return this;
    }

    @Override
    public @NonNull RedBindAttack copy() {
        return copyExtras(new RedBindAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(), getStun(),
                getHitboxSize(), getKnockback(), getOffset()));
    }
}
