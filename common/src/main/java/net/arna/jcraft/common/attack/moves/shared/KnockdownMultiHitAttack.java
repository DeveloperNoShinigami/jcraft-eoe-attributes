package net.arna.jcraft.common.attack.moves.shared;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntCollection;
import lombok.Getter;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.IAttacker;
import net.arna.jcraft.api.attack.MoveType;
import net.arna.jcraft.common.attack.moves.base.AbstractMultiHitAttack;
import net.arna.jcraft.registry.JStatusRegistry;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

@Getter
public final class KnockdownMultiHitAttack<A extends IAttacker<? extends A, ?>> extends AbstractMultiHitAttack<KnockdownMultiHitAttack<A>, A> {
    private final int knockdownDuration;

    public KnockdownMultiHitAttack(final int cooldown, final int duration, final float moveDistance, final float damage, final int stun, final float hitboxSize,
                                   final float knockback, final float offset, final @NonNull IntCollection hitMoments, final int knockdownDuration) {
        super(cooldown, duration, moveDistance, damage, stun, hitboxSize, knockback, offset, hitMoments);
        this.knockdownDuration = knockdownDuration;
    }

    @Override
    protected void processTarget(A attacker, LivingEntity target, Vec3 kbVec, DamageSource damageSource) {
        super.processTarget(attacker, target, kbVec, damageSource);

        target.addEffect(new MobEffectInstance(JStatusRegistry.KNOCKDOWN.get(), knockdownDuration, 0, true, false));
    }

    @Override
    public @NonNull MoveType<KnockdownMultiHitAttack<A>> getMoveType() {
        return Type.INSTANCE.cast();
    }

    @Override
    protected @NonNull KnockdownMultiHitAttack<A> getThis() {
        return this;
    }

    @Override
    public @NonNull KnockdownMultiHitAttack<A> copy() {
        return copyExtras(new KnockdownMultiHitAttack<>(getCooldown(), getDuration(), getMoveDistance(), getDamage(), getStun(),
                getHitboxSize(), getKnockback(), getOffset(), getHitMoments(), knockdownDuration));
    }

    public static class Type extends AbstractMultiHitAttack.Type<KnockdownMultiHitAttack<?>> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<KnockdownMultiHitAttack<?>>, KnockdownMultiHitAttack<?>>
        buildCodec(RecordCodecBuilder.Instance<KnockdownMultiHitAttack<?>> instance) {
            return instance.group(extras(), attackExtras(), cooldown(), duration(), moveDistance(), damage(),
                            stun(), hitboxSize(), knockback(), offset(), hitMoments(),
                            Codec.INT.fieldOf("knockdown_duration").forGetter(KnockdownMultiHitAttack::getKnockdownDuration))
                    .apply(instance, applyAttackExtras(KnockdownMultiHitAttack::new));
        }
    }
}
