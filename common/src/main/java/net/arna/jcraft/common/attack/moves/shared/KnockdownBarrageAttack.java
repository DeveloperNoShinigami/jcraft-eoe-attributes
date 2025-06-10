package net.arna.jcraft.common.attack.moves.shared;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import lombok.NonNull;
import net.arna.jcraft.api.attack.IAttacker;
import net.arna.jcraft.api.attack.MoveType;
import net.arna.jcraft.api.attack.moves.AbstractBarrageAttack;
import net.arna.jcraft.registry.JStatusRegistry;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

@Getter
public final class KnockdownBarrageAttack<A extends IAttacker<? extends A, ?>> extends AbstractBarrageAttack<KnockdownBarrageAttack<A>, A> {
    private final int knockdownDuration;

    public KnockdownBarrageAttack(final int cooldown, final int windup, final int duration, final float moveDistance, final float damage, final int stun,
                                  final float hitboxSize, final float knockback, final float offset, final int interval, final int knockdownDuration) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset, interval);
        this.knockdownDuration = knockdownDuration;
    }

    @Override
    protected void processTarget(A attacker, LivingEntity target, Vec3 kbVec, DamageSource damageSource) {
        super.processTarget(attacker, target, kbVec, damageSource);

        target.addEffect(new MobEffectInstance(JStatusRegistry.KNOCKDOWN.get(), knockdownDuration, 0, true, false));
    }

    @Override
    public @NonNull MoveType<KnockdownBarrageAttack<A>> getMoveType() {
        return Type.INSTANCE.cast();
    }

    @Override
    protected @NonNull KnockdownBarrageAttack<A> getThis() {
        return this;
    }

    @Override
    public @NonNull KnockdownBarrageAttack<A> copy() {
        return copyExtras(new KnockdownBarrageAttack<>(getCooldown(), getWindup(), getDuration(), getMoveDistance(),
                getDamage(), getStun(), getHitboxSize(), getKnockback(), getOffset(), getInterval(), getKnockdownDuration()));
    }

    public static class Type extends AbstractBarrageAttack.Type<KnockdownBarrageAttack<?>> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<KnockdownBarrageAttack<?>>, KnockdownBarrageAttack<?>> buildCodec(RecordCodecBuilder.Instance<KnockdownBarrageAttack<?>> instance) {
            return instance.group(extras(), attackExtras(), cooldown(), windup(), duration(), moveDistance(), damage(),
                    stun(), hitboxSize(), knockback(), offset(), interval(),
                    Codec.INT.fieldOf("knockdown_duration").forGetter(KnockdownBarrageAttack::getKnockdownDuration))
                    .apply(instance, applyAttackExtras(KnockdownBarrageAttack::new));
        }
    }
}
