package net.arna.jcraft.common.attack.moves.cmoon;

import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.entity.stand.CMoonEntity;
import net.arna.jcraft.common.attack.MobilityType;
import net.arna.jcraft.registry.JStatusRegistry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import java.util.Set;

public final class GravitationalHopMove extends AbstractMove<GravitationalHopMove, CMoonEntity> {
    public GravitationalHopMove(int cooldown) {
        super(cooldown, 0, 0, 0f);
        mobilityType = MobilityType.HIGHJUMP;
    }

    @Override
    public void onInitiate(CMoonEntity attacker) {
        getInitActions().forEach(action -> action.perform(attacker, attacker.getUser(), attacker.getMoveContext()));
    }

    @Override
    public @NonNull Set<LivingEntity> perform(CMoonEntity attacker, LivingEntity user, MoveContext ctx) {
        if (user.onGround()) {
            if (user.hasEffect(JStatusRegistry.WEIGHTLESS.get())) {
                user.removeEffect(JStatusRegistry.WEIGHTLESS.get());
            }
            user.addEffect(new MobEffectInstance(JStatusRegistry.WEIGHTLESS.get(), 200, 1));
        } else {
            user.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 60, 1));
            user.push(0, 1.0, 0);
        }

        user.hurtMarked = true;
        return Set.of();
    }

    @Override
    protected @NonNull GravitationalHopMove getThis() {
        return this;
    }

    @Override
    public @NonNull GravitationalHopMove copy() {
        return copyExtras(new GravitationalHopMove(getCooldown()));
    }
}
