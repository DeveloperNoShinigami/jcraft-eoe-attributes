package net.arna.jcraft.common.attack.moves.metallica;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.api.attack.MoveType;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.entity.projectile.ScalpelProjectile;
import net.arna.jcraft.common.entity.stand.MetallicaEntity;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.gravity.util.RotationUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

public class FanTossAttack extends AbstractMove<FanTossAttack, MetallicaEntity> {
    public FanTossAttack(int cooldown, int windup, int duration, float moveDistance) {
        super(cooldown, windup, duration, moveDistance);
        ranged = true;
    }

    @Override
    public @NonNull MoveType<FanTossAttack> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(MetallicaEntity attacker, LivingEntity user) {
        final float offset = 10.0F;
        int index = 0;
        // 0 -> 1 -> -1 -> 2 -> -2
        for (int i = 0; i < 5; i++) {
            ScalpelProjectile scalpel = ScalpelProjectile.fromMetallica(attacker);
            if (scalpel == null) continue;

            if (i % 2 == 0) index -= i;
            else index += i;

            final float pitch = user.getXRot();
            final float yaw = user.getYRot() + index * offset;
            Vec3 rotVec = RotationUtil.vecPlayerToWorld(RotationUtil.rotToVec(yaw, pitch), GravityChangerAPI.getGravityDirection(user));
            scalpel.shoot(rotVec.x, rotVec.y, rotVec.z, 1.75F, 0.1F);

            Vec3 upVec = GravityChangerAPI.getEyeOffset(attacker.getUserOrThrow());
            Vec3 heightOffset = upVec.scale(0.75);
            scalpel.setPos(attacker.getBaseEntity().position().add(heightOffset));

            attacker.level().addFreshEntity(scalpel);
        }

        return Set.of();
    }

    @Override
    protected @NonNull FanTossAttack getThis() {
        return this;
    }

    @Override
    public @NonNull FanTossAttack copy() {
        return copyExtras(new FanTossAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance()));
    }

    public static class Type extends AbstractMove.Type<FanTossAttack> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<FanTossAttack>, FanTossAttack> buildCodec(RecordCodecBuilder.Instance<FanTossAttack> instance) {
            return baseDefault(instance, FanTossAttack::new);
        }
    }
}
