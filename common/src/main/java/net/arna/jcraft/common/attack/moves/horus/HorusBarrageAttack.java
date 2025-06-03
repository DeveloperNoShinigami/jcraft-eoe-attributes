package net.arna.jcraft.common.attack.moves.horus;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.MoveInputType;
import net.arna.jcraft.api.attack.MoveType;
import net.arna.jcraft.common.attack.moves.base.AbstractBarrageAttack;
import net.arna.jcraft.common.entity.projectile.IcicleProjectile;
import net.arna.jcraft.common.entity.stand.HorusEntity;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public final class HorusBarrageAttack extends AbstractBarrageAttack<HorusBarrageAttack, HorusEntity> {

    public HorusBarrageAttack(final int cooldown, final int windup, final int duration, final float moveDistance,
                              final float damage, final int stun, final float hitboxSize, final float knockback,
                              final float offset, final int interval) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset, interval);
        withHoldable();
    }

    @Override
    public @NotNull MoveType<HorusBarrageAttack> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(HorusEntity attacker, LivingEntity user) {
        Set<LivingEntity> targets = super.perform(attacker, user);

        final IcicleProjectile icicle = new IcicleProjectile(attacker.level(), user);
        icicle.shootFromRotation(user, user.getXRot(), user.getYRot(), 0.0F, 1.75F, 0.1F);

        final Vec3 heightOffset = GravityChangerAPI.getEyeOffset(user).scale(0.75);
        icicle.setPos(attacker.getBaseEntity().position().add(heightOffset).add(
                attacker.getRandom().nextGaussian() / 3,
                attacker.getRandom().nextGaussian() / 3,
                attacker.getRandom().nextGaussian() / 3
        ));
        //icicle.withReflect();

        attacker.level().addFreshEntity(icicle);

        return targets;
    }

    @Override
    public void onUserMoveInput(final HorusEntity attacker, final MoveInputType type, final boolean pressed, final boolean moveInitiated) {
        super.onUserMoveInput(attacker, type, pressed, moveInitiated);
        // Must be held
        if (type.getMoveClass() == getMoveClass() && !pressed) attacker.cancelMove();
    }

    @Override
    protected @NonNull HorusBarrageAttack getThis() {
        return this;
    }

    @Override
    public @NonNull HorusBarrageAttack copy() {
        return copyExtras(new HorusBarrageAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(), getStun(),
                getHitboxSize(), getKnockback(), getOffset(), getInterval()));
    }

    public static class Type extends AbstractBarrageAttack.Type<HorusBarrageAttack> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NotNull App<RecordCodecBuilder.Mu<HorusBarrageAttack>, HorusBarrageAttack> buildCodec(RecordCodecBuilder.Instance<HorusBarrageAttack> instance) {
            return barrageDefault(instance, HorusBarrageAttack::new);
        }
    }
}
