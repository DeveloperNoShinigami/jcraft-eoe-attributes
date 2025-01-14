package net.arna.jcraft.common.attack.moves.horus;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.core.data.MoveType;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.entity.projectile.LargeIcicleProjectile;
import net.arna.jcraft.common.entity.stand.HorusEntity;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

public class IcicleFireAttack extends AbstractMove<IcicleFireAttack, HorusEntity> {
    public static int MAX_ICICLE_CHARGE_TIME = 30;

    public IcicleFireAttack(int cooldown, int windup, int duration, float moveDistance) {
        super(cooldown, windup, duration, moveDistance);
    }

    @Override
    public @NonNull MoveType<IcicleFireAttack> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public boolean shouldPerform(final HorusEntity attacker, final int moveStun) {
        return super.shouldPerform(attacker, moveStun);
    }

    @Override
    public @NonNull Set<LivingEntity> perform(HorusEntity attacker, LivingEntity user, MoveContext ctx) {
        final LargeIcicleProjectile instantIcicle = new LargeIcicleProjectile(attacker.level(), user);
        float scale = Mth.clamp(getChargeTime(attacker) / (MAX_ICICLE_CHARGE_TIME - 2.0f), 0.1f, 1.0f);
        instantIcicle.setScale(scale);
        instantIcicle.setInstant(true);

        final Vec3 heightOffset = GravityChangerAPI.getEyeOffset(user).scale(0.75);

        final Vec3 velocity = user.getLookAngle().scale(0.01);
        final double e = velocity.x, f = velocity.y, g = velocity.z;
        final double l = velocity.horizontalDistance();

        instantIcicle.moveTo(attacker.getX() + heightOffset.x, attacker.getY() + heightOffset.y, attacker.getZ() + heightOffset.z,
                (float) (Mth.atan2(-e, -g) * 57.2957763671875),
                (float) (Mth.atan2(f, l) * 57.2957763671875)
        );
        instantIcicle.setDeltaMovement(velocity);
        instantIcicle.lock();

        attacker.level().addFreshEntity(instantIcicle);

        return Set.of();
    }

    @Override
    protected @NonNull IcicleFireAttack getThis() {
        return this;
    }

    @Override
    public @NonNull IcicleFireAttack copy() {
        return copyExtras(new IcicleFireAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance()));
    }

    public static class Type extends AbstractMove.Type<IcicleFireAttack> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<IcicleFireAttack>, IcicleFireAttack> buildCodec(RecordCodecBuilder.Instance<IcicleFireAttack> instance) {
            return baseDefault(instance, IcicleFireAttack::new);
        }
    }
}
