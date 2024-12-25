package net.arna.jcraft.common.attack.moves.thesun;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.data.MoveType;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.entity.projectile.MeteorProjectile;
import net.arna.jcraft.common.entity.stand.TheSunEntity;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.registry.JSoundRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

public class FireMeteorAttack extends AbstractMove<FireMeteorAttack, TheSunEntity> {
    @Getter
    private final int meteors;
    @Getter
    private final float meteorVelocity;
    @Getter
    private final boolean explosiveIfMax;
    private Vec3 targetPosition;

    public FireMeteorAttack(int cooldown, int windup, int duration, int meteors, float meteorVelocity, boolean explosiveIfMax) {
        super(cooldown, windup, duration, 0);
        this.meteors = meteors;
        this.meteorVelocity = meteorVelocity;
        this.explosiveIfMax = explosiveIfMax;
        ranged = true;
    }

    @Override
    public @NonNull MoveType<FireMeteorAttack> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public void onInitiate(TheSunEntity attacker) {
        super.onInitiate(attacker);

        targetPosition = attacker.acquireTargetPosition();
    }

    @Override
    public @NonNull Set<LivingEntity> perform(TheSunEntity attacker, LivingEntity user, MoveContext ctx) {
        Vec3 pos = attacker.randomPos();
        for (int i = 0; i < meteors; i++) {
            MeteorProjectile meteor = fireMeteor(attacker, user, pos, JUtils.getLookVector(pos, targetPosition).scale(meteorVelocity),
                    2.5f, 0f);
            meteor.setNoGravity(true);

            if (explosiveIfMax && attacker.getRawScale() == TheSunEntity.MAX_SCALE) {
                meteor.setExplosive(true);
            }
        }

        return Set.of();
    }

    public static MeteorProjectile fireMeteor(TheSunEntity attacker, @NonNull LivingEntity user, Vec3 pos, Vec3 velocity) {
        return fireMeteor(attacker, user, pos, velocity, 1.25f, 10f);
    }

    public static MeteorProjectile fireMeteor(TheSunEntity attacker, @NonNull LivingEntity user, Vec3 pos, Vec3 velocity, float speed, float divergence) {
        final MeteorProjectile meteor = new MeteorProjectile(attacker.level(), user, attacker);
        meteor.setSkin(attacker.getSkin());
        meteor.setPos(pos);
        meteor.shoot(velocity.x, velocity.y, velocity.z, speed, divergence);

        attacker.level().addFreshEntity(meteor);
        final AbstractMove<?, ? super TheSunEntity> move = attacker.getCurrentMove();
        if (move != null && !move.isBarrage()) {
            attacker.playSound(JSoundRegistry.SUN_METEOR_FIRE.get(), 1f, 1f);
        }

        return meteor;
    }

    @Override
    protected @NonNull FireMeteorAttack getThis() {
        return this;
    }

    @Override
    public @NonNull FireMeteorAttack copy() {
        return copyExtras(new FireMeteorAttack(getCooldown(), getWindup(), getDuration(), meteors, meteorVelocity, explosiveIfMax));
    }

    public static class Type extends AbstractMove.Type<FireMeteorAttack> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<FireMeteorAttack>, FireMeteorAttack> buildCodec(RecordCodecBuilder.Instance<FireMeteorAttack> instance) {
            return instance.group(extras(), cooldown(), windup(), duration(),
                    Codec.INT.fieldOf("meteors").forGetter(FireMeteorAttack::getMeteors),
                    Codec.FLOAT.fieldOf("meteor_velocity").forGetter(FireMeteorAttack::getMeteorVelocity),
                    Codec.BOOL.fieldOf("explosive_if_max").forGetter(FireMeteorAttack::isExplosiveIfMax)
                    ).apply(instance, applyExtras(FireMeteorAttack::new));
        }
    }
}
