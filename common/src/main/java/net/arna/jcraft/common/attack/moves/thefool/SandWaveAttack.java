package net.arna.jcraft.common.attack.moves.thefool;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.MobilityType;
import net.arna.jcraft.api.attack.MoveType;
import net.arna.jcraft.common.attack.moves.base.AbstractBarrageAttack;
import net.arna.jcraft.common.entity.stand.TheFoolEntity;
import net.arna.jcraft.registry.JStatusRegistry;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

@Getter
public final class SandWaveAttack extends AbstractBarrageAttack<SandWaveAttack, TheFoolEntity> {
    private final int knockdownDuration;

    public SandWaveAttack(final int cooldown, final int windup, final int duration, final float moveDistance,
                          final float damage, final int stun, final float hitboxSize, final float knockback,
                          final float offset, final int interval, final int knockdownDuration) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset, interval);
        this.knockdownDuration = knockdownDuration;
        mobilityType = MobilityType.DASH;
        ranged = true;
    }

    @Override
    public @NonNull MoveType<SandWaveAttack> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public void onInitiate(final TheFoolEntity attacker) {
        super.onInitiate(attacker);

        attacker.setSand(true);
        attacker.setFree(false);
        attacker.setWave(true);
    }

    @Override
    public void activeTick(final TheFoolEntity attacker, final int moveStun) {
        super.activeTick(attacker, moveStun);

        final LivingEntity user = attacker.getUser();
        if (user == null || !user.onGround()) {
            return;
        }

        Vec3 rotVec = user.getLookAngle().scale(0.25);
        user.push(rotVec.x, 0, rotVec.z);
        user.hurtMarked = true;
    }

    @Override
    protected void processTarget(TheFoolEntity attacker, LivingEntity target, Vec3 kbVec, DamageSource damageSource) {
        super.processTarget(attacker, target, kbVec, damageSource);

        int knockdownDuration = 15;
        target.addEffect(new MobEffectInstance(JStatusRegistry.KNOCKDOWN.get(), knockdownDuration, 0, true, false));
    }

    @Override
    protected @NonNull SandWaveAttack getThis() {
        return this;
    }

    @Override
    public @NonNull SandWaveAttack copy() {
        return copyExtras(new SandWaveAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(),
                getStun(), getHitboxSize(), getKnockback(), getOffset(), getInterval(), knockdownDuration));
    }

    public static class Type extends AbstractBarrageAttack.Type<SandWaveAttack> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<SandWaveAttack>, SandWaveAttack> buildCodec(RecordCodecBuilder.Instance<SandWaveAttack> instance) {
            return instance.group(extras(), attackExtras(), cooldown(), windup(), duration(), moveDistance(), damage(),
                    stun(), hitboxSize(), knockback(), offset(), interval(),
                            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("knockdown_duration").forGetter(SandWaveAttack::getKnockdownDuration))
                    .apply(instance, applyAttackExtras(SandWaveAttack::new));
        }
    }
}
