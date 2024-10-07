package net.arna.jcraft.common.attack.moves.killerqueen.bitesthedust;

import it.unimi.dsi.fastutil.ints.IntCollection;
import lombok.NonNull;
import net.arna.jcraft.common.attack.moves.base.AbstractMultiHitAttack;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.killerqueen.DetonateAttack;
import net.arna.jcraft.common.entity.stand.KQBTDEntity;
import net.arna.jcraft.registry.JStatusRegistry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import java.util.Set;

public final class BTDGrabHitAttack extends AbstractMultiHitAttack<BTDGrabHitAttack, KQBTDEntity> {
    public BTDGrabHitAttack(final int cooldown, final int duration, final float attackDistance, final float damage, final int stun, final float hitboxSize,
                            final float knockback, final float offset, final @NonNull IntCollection hitMoments) {
        super(cooldown, duration, attackDistance, damage, stun, hitboxSize, knockback, offset, hitMoments);
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final KQBTDEntity attacker, final LivingEntity user, final MoveContext ctx) {
        Set<LivingEntity> targets = super.perform(attacker, user, ctx);
        switch (getBlow(attacker)) {
            case 0 -> {
                for (LivingEntity ent : targets) {
                    ent.addEffect(new MobEffectInstance(JStatusRegistry.KNOCKDOWN.get(), 40, 0, true, false, true));
                }
            }
            case 2 -> DetonateAttack.explode(attacker, user, attacker.position().subtract(0, .5, 0));
        }

        return targets;
    }

    @Override
    protected @NonNull BTDGrabHitAttack getThis() {
        return this;
    }

    @Override
    public @NonNull BTDGrabHitAttack copy() {
        return copyExtras(new BTDGrabHitAttack(getCooldown(), getDuration(), getMoveDistance(), getDamage(), getStun(), getHitboxSize(),
                getKnockback(), getOffset(), getHitMoments()));
    }
}
