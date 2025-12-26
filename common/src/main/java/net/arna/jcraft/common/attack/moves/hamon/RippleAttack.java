package net.arna.jcraft.common.attack.moves.hamon;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.api.attack.MoveType;
import net.arna.jcraft.api.attack.moves.AbstractSimpleAttack;
import net.arna.jcraft.common.entity.projectile.HamonWaveEntity;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.spec.HamonSpec;
import net.arna.jcraft.common.util.JParticleType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public final class RippleAttack extends AbstractSimpleAttack<RippleAttack, HamonSpec> {
    public RippleAttack(final int cooldown, final int windup, final int duration, final float moveDistance, final float damage, final int stun,
                        final float hitboxSize, final float knockback, final float offset) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset);
        ranged = true;
        hitSpark = JParticleType.HIT_SPARK_2;
    }

    @Override
    public @NotNull MoveType<RippleAttack> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final HamonSpec attacker, final LivingEntity user) {
        final Set<LivingEntity> targets = super.perform(attacker, user);

        final Level level = user.level();

        final HamonWaveEntity wave = new HamonWaveEntity(level);
        wave.copyPosition(user);
        wave.setMaster(user);
        GravityChangerAPI.setGravity(wave, GravityChangerAPI.getGravityList(user));
        level.addFreshEntity(wave);

        return targets;
    }

    @Override
    protected @NonNull RippleAttack getThis() {
        return this;
    }

    @Override
    public @NonNull RippleAttack copy() {
        return copyExtras(new RippleAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(),
                getStun(), getHitboxSize(), getKnockback(), getOffset()));
    }

    public static class Type extends AbstractSimpleAttack.Type<RippleAttack> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NotNull App<RecordCodecBuilder.Mu<RippleAttack>, RippleAttack> buildCodec(RecordCodecBuilder.Instance<RippleAttack> instance) {
            return attackDefault(instance, RippleAttack::new);
        }
    }
}
