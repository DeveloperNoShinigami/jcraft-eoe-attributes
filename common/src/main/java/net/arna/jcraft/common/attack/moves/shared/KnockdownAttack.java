package net.arna.jcraft.common.attack.moves.shared;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.IAttacker;
import net.arna.jcraft.common.attack.core.data.MoveType;
import net.arna.jcraft.common.attack.moves.base.AbstractSimpleAttack;
import net.arna.jcraft.registry.JStatusRegistry;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

@Getter
public final class KnockdownAttack<A extends IAttacker<? extends A, ?>> extends AbstractSimpleAttack<KnockdownAttack<A>, A> {
    private final int knockdownDuration;

    public KnockdownAttack(final int cooldown, final int windup, final int duration, final float moveDistance, final float damage, final int stun,
                           final float hitboxSize, final float knockback, final float offset, final int knockdownDuration) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset);
        this.knockdownDuration = knockdownDuration;
    }

    @Override
    protected void processTarget(A attacker, LivingEntity target, Vec3 kbVec, DamageSource damageSource) {
        super.processTarget(attacker, target, kbVec, damageSource);

        target.addEffect(new MobEffectInstance(JStatusRegistry.KNOCKDOWN.get(), knockdownDuration, 0, true, false));
    }

    @Override
    public @NonNull MoveType<KnockdownAttack<A>> getMoveType() {
        return Type.INSTANCE.cast();
    }

    @Override
    protected @NonNull KnockdownAttack<A> getThis() {
        return this;
    }

    @Override
    public @NonNull KnockdownAttack<A> copy() {
        return copyExtras(new KnockdownAttack<>(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(), getStun(),
                getHitboxSize(), getKnockback(), getOffset(), getKnockdownDuration()));
    }

    public static class Type extends AbstractSimpleAttack.Type<KnockdownAttack<?>> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<KnockdownAttack<?>>, KnockdownAttack<?>> buildCodec(RecordCodecBuilder.Instance<KnockdownAttack<?>> instance) {
            return instance.group(extras(), attackExtras(), cooldown(), windup(), duration(), moveDistance(), damage(),
                    stun(), hitboxSize(), knockback(), offset(),
                    Codec.INT.fieldOf("knockdown_duration").forGetter(KnockdownAttack::getKnockdownDuration))
                    .apply(instance, applyAttackExtras(KnockdownAttack::new));
        }
    }
}
