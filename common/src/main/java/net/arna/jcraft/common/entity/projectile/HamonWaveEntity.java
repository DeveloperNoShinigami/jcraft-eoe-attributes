package net.arna.jcraft.common.entity.projectile;

import net.arna.jcraft.api.AttackData;
import net.arna.jcraft.api.Attacks;
import net.arna.jcraft.api.attack.enums.StunType;
import net.arna.jcraft.api.component.living.CommonHitPropertyComponent;
import net.arna.jcraft.api.registry.JEntityTypeRegistry;
import net.arna.jcraft.api.registry.JParticleTypeRegistry;
import net.arna.jcraft.client.renderer.features.HamonParticlesFeatureRenderer;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.gravity.util.RotationUtil;
import net.arna.jcraft.common.util.JUtils;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HamonWaveEntity extends JAttackEntity {
    public final int LIFETIME = 30;
    public final float MAX_SIZE = 10.0f;
    private float lastSize = 0.0f;
    private DamageSource damageSource = null;
    public HamonWaveEntity(Level world) {
        super(JEntityTypeRegistry.HAMON_WAVE.get(), world);
    }

    @Override
    public void setMaster(LivingEntity m) {
        super.setMaster(m);
        damageSource = level().damageSources().indirectMagic(m, this);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return true;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public void push(@NotNull Entity entity) {
        // intentionally left empty
    }

    @Override
    public boolean canCollideWith(@NotNull Entity entity) {
        return false;
    }

    @Override
    public void tick() {
        super.tick();

        if (tickCount > LIFETIME) {
            discard();
            return;
        }

        final float size = Mth.lerp((float) tickCount / LIFETIME, 0.0F, MAX_SIZE);
        final Vec3 position = position();
        final Direction gravity = GravityChangerAPI.getGravityDirection(this);

        if (level() instanceof ServerLevel serverLevel) {
            List<Entity> potentialHits = serverLevel.getEntities(
                    this,
                    AABB.ofSize(position(), size * 2, size * 2, size * 2),
                    EntitySelector.NO_CREATIVE_OR_SPECTATOR
            );

            // TODO: replace with actual intersection check
            for (Entity potentialHit : potentialHits) {
                if (potentialHit instanceof LivingEntity living) {
                    final double distanceSqr = potentialHit.distanceToSqr(position);

                    if (
                            distanceSqr < ((size - 0.5) * (size - 0.5)) ||
                            distanceSqr > ((size + 0.5) * (size + 0.5))
                    ) {
                        continue;
                    }

                    Attacks.damageLogic(serverLevel, living, new AttackData(
                            living.position().subtract(position).scale(0.3),
                            10, StunType.BURSTABLE.ordinal(), false, 5f, true, 2,
                            damageSource, master, CommonHitPropertyComponent.HitAnimation.LAUNCH,
                            null, false, false
                    ));
                }
            }
        } else if (level() instanceof ClientLevel clientLevel) {
            final float numParticles = size * 10;

            HamonParticlesFeatureRenderer.prepareHamonAura(this);

            for (int i = 0; i < (int) numParticles; i++) {
                final int theta = Math.round((float) i / numParticles * 360.0F);
                final float a = Mth.sin(Mth.DEG_TO_RAD * theta) * size;
                final float b = Mth.cos(Mth.DEG_TO_RAD * theta) * size;

                final Vec3 offset = RotationUtil.vecPlayerToWorld(new Vec3(a, 0, b), gravity);

                clientLevel.addParticle(
                        JUtils.chooseRandom(random,
                                JParticleTypeRegistry.AURA_ARC.get(),
                                JParticleTypeRegistry.AURA_BLOB.get(),
                                JParticleTypeRegistry.HAMON_SPARK.get(),
                                ParticleTypes.ELECTRIC_SPARK
                        ),
                        position.x + offset.x,
                        position.y + offset.y,
                        position.z + offset.z,
                        0, 0, 0
                );
            }
        }

        lastSize = size;
    }
}
