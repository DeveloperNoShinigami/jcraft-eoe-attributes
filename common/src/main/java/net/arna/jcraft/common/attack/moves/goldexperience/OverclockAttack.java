package net.arna.jcraft.common.attack.moves.goldexperience;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.data.MoveType;
import net.arna.jcraft.common.attack.core.StunType;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractSimpleAttack;
import net.arna.jcraft.common.entity.stand.GoldExperienceEntity;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.util.JParticleType;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.registry.JStatusRegistry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public final class OverclockAttack extends AbstractSimpleAttack<OverclockAttack, GoldExperienceEntity> {
    public OverclockAttack(final int cooldown, final int windup, final int duration, final float moveDistance,
                           final float damage, final int stun, final float hitboxSize, final float knockback, final float offset) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset);
        withStunType(StunType.LAUNCH);
        hitSpark = JParticleType.HIT_SPARK_3;
    }

    @Override
    public @NotNull MoveType<OverclockAttack> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final GoldExperienceEntity attacker, final LivingEntity user, final MoveContext ctx) {
        final Set<LivingEntity> targets = super.perform(attacker, user, ctx);

        for (LivingEntity target : targets) {
            target.addEffect(new MobEffectInstance(JStatusRegistry.DAZED.get(), 60, 3, true, false));
            target.addEffect(new MobEffectInstance(JStatusRegistry.OUTOFBODY.get(), 60, 0, false, true));

            final Vec3 upDir = new Vec3(GravityChangerAPI.getGravityDirection(user).step());
            JUtils.setVelocity(target, upDir.scale(-0.8));
        }

        return targets;
    }

    @Override
    protected @NonNull OverclockAttack getThis() {
        return this;
    }

    @Override
    public @NonNull OverclockAttack copy() {
        return copyExtras(new OverclockAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(), getStun(),
                getHitboxSize(), getKnockback(), getOffset()));
    }

    public static class Type extends AbstractSimpleAttack.Type<OverclockAttack> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NotNull App<RecordCodecBuilder.Mu<OverclockAttack>, OverclockAttack> buildCodec(RecordCodecBuilder.Instance<OverclockAttack> instance) {
            return attackDefault(instance, OverclockAttack::new);
        }
    }
}
