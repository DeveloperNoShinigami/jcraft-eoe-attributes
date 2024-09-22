package net.arna.jcraft.common.attack.moves.thefool;

import lombok.NonNull;
import net.arna.jcraft.common.attack.moves.base.AbstractEffectInflictingBarrageAttack;
import net.arna.jcraft.common.entity.stand.TheFoolEntity;
import net.arna.jcraft.common.attack.MobilityType;
import net.arna.jcraft.registry.JStatusRegistry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import java.util.List;

public final class SandWaveAttack extends AbstractEffectInflictingBarrageAttack<SandWaveAttack, TheFoolEntity> {
    public SandWaveAttack(int cooldown, int windup, int duration, float moveDistance, float damage, int stun,
                          float hitboxSize, float knockback, float offset, int interval) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset, interval,
                List.of(new MobEffectInstance(JStatusRegistry.KNOCKDOWN.get(), 15, 0, true, false)));
        mobilityType = MobilityType.DASH;
        ranged = true;
    }

    @Override
    public void onInitiate(TheFoolEntity attacker) {
        super.onInitiate(attacker);

        attacker.setSand(true);
        attacker.setFree(false);
        attacker.setWave(true);
    }

    @Override
    public void tick(TheFoolEntity attacker, int moveStun) {
        super.tick(attacker, moveStun);

        final LivingEntity user = attacker.getUser();
        if (user == null || !user.onGround()) {
            return;
        }

        Vec3 rotVec = user.getLookAngle().scale(0.25);
        user.push(rotVec.x, 0, rotVec.z);
        user.hurtMarked = true;
    }

    @Override
    protected @NonNull SandWaveAttack getThis() {
        return this;
    }

    @Override
    public @NonNull SandWaveAttack copy() {
        return copyExtras(new SandWaveAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(),
                getStun(), getHitboxSize(), getKnockback(), getOffset(), getInterval()));
    }
}
