package net.arna.jcraft.common.attack.moves.thesun;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import lombok.NonNull;
import net.arna.jcraft.api.attack.MoveType;
import net.arna.jcraft.api.attack.moves.AbstractMove;
import net.arna.jcraft.common.entity.projectile.SunBeamProjectile;
import net.arna.jcraft.common.entity.stand.TheSunEntity;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.registry.JSoundRegistry;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector2f;

import java.util.Set;

public class FireSunBeamAttack extends AbstractMove<FireSunBeamAttack, TheSunEntity> {
    @Getter
    private final int beams;
    @Getter
    private final float divergence;
    private Vec3 targetPosition; // No need to store in ctx, set in #onInitiate and used in #perform

    public FireSunBeamAttack(int cooldown, int windup, int duration, int beams, float divergence) {
        super(cooldown, windup, duration, 0);
        this.beams = beams;
        this.divergence = divergence;
        ranged = true;
    }

    @Override
    public @NonNull MoveType<FireSunBeamAttack> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public void onInitiate(TheSunEntity attacker) {
        super.onInitiate(attacker);

        targetPosition = attacker.acquireTargetPosition();
    }

    @Override
    public boolean shouldPerform(TheSunEntity attacker, int moveStun) {
        // If beams = 3, executes at tick windup, windup + 4 and windup + 8.
        return attacker.hasUser() && moveStun <= getWindupPoint() &&
                (getWindupPoint() - moveStun) / 4 < beams && (getWindupPoint() - moveStun) % 4 == 0;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(TheSunEntity attacker, LivingEntity user) {
        final Vec3 pos = attacker.randomPos();

        final SunBeamProjectile sunBeam = new SunBeamProjectile(attacker.level(), user, attacker);
        sunBeam.setSkin(attacker.getSkin());
        sunBeam.setPos(pos);

        final Vector2f pitchYaw = JUtils.getLookPY(pos, targetPosition);
        final float pitch = pitchYaw.x, yaw = pitchYaw.y;
        final Vec3 lookVec = new Vec3(
                -Mth.sin(yaw * 0.017453292F) * Mth.cos(pitch * 0.017453292F),
                -Mth.sin((pitch) * 0.017453292F),
                Mth.cos(yaw * 0.017453292F) * Mth.cos(pitch * 0.017453292F)
        );

        sunBeam.setXRot(pitch + (attacker.getRandom().nextFloat() - 0.5f) * divergence);
        sunBeam.setYRot(yaw + (attacker.getRandom().nextFloat() - 0.5f) * divergence);
        sunBeam.shoot(lookVec.x, lookVec.y, lookVec.z, 0.01f, divergence);

        attacker.level().addFreshEntity(sunBeam);
        attacker.playSound(JSoundRegistry.SUN_BEAM_RAY.get(), 1f, 1f);

        return Set.of();
    }

    @Override
    protected @NonNull FireSunBeamAttack getThis() {
        return this;
    }

    @Override
    public @NonNull FireSunBeamAttack copy() {
        return copyExtras(new FireSunBeamAttack(getCooldown(), getWindup(), getDuration(), beams, divergence));
    }

    public static class Type extends AbstractMove.Type<FireSunBeamAttack> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<FireSunBeamAttack>, FireSunBeamAttack> buildCodec(RecordCodecBuilder.Instance<FireSunBeamAttack> instance) {
            return instance.group(extras(), cooldown(), windup(), duration(),
                    Codec.INT.fieldOf("beams").forGetter(FireSunBeamAttack::getBeams),
                    Codec.FLOAT.fieldOf("divergence").forGetter(FireSunBeamAttack::getDivergence))
                    .apply(instance, applyExtras(FireSunBeamAttack::new));
        }
    }
}
