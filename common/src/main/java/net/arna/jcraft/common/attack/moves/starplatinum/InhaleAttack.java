package net.arna.jcraft.common.attack.moves.starplatinum;

import lombok.Getter;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.attack.moves.base.AbstractSimpleAttack;
import net.arna.jcraft.common.entity.stand.StarPlatinumEntity;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.gravity.util.RotationUtil;
import net.arna.jcraft.common.util.JUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import java.util.Set;

import static net.arna.jcraft.common.attack.moves.base.AbstractSimpleAttack.createBox;

@Getter
public class InhaleAttack extends AbstractMove<InhaleAttack, StarPlatinumEntity> {
    private final RandomSource random = RandomSource.create();
    private final int inhaleDuration;

    public InhaleAttack(int cooldown, int windup, int duration, float moveDistance, int inhaleDuration) {
        super(cooldown, windup, duration, moveDistance);
        this.inhaleDuration = inhaleDuration;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(StarPlatinumEntity attacker, LivingEntity user, MoveContext ctx) {
        attacker.setInhaleTime(inhaleDuration);
        return Set.of();
    }

    public void tickInhale(StarPlatinumEntity attacker) {
        int inhaleTime = attacker.getInhaleTime();
        if (inhaleTime <= 0 || !attacker.hasUser()) {
            return;
        }

        Vec3 rotVec = attacker.isFree() ? getRotVec(attacker) : attacker.getUserOrThrow().getLookAngle();
        Vec3 eyePos = attacker.isFree() ?
                new Vec3(attacker.getFreePos()).add(RotationUtil.vecPlayerToWorld(new Vec3(0, attacker.getBbHeight(), 0), GravityChangerAPI.getGravityDirection(attacker)))
                : attacker.getEyePosition();
        Vec3 fPos = eyePos.add(rotVec.scale(1.75));
        Vec3 ffPos = eyePos.add(rotVec.scale(3.25));

        if (attacker.level().isClientSide) {
            // Display particles for the two hitboxes
            Vec3 addVel = rotVec.add(random.nextDouble() * 2 - 1, random.nextDouble() * 2 - 1, random.nextDouble() * 2 - 1);
            Vec3 particlePos = fPos.add(addVel);

            attacker.level().addParticle(ParticleTypes.POOF,
                    particlePos.x,
                    particlePos.y,
                    particlePos.z,
                    -addVel.x / 10.0, -addVel.y / 10.0, -addVel.z / 10.0);

            addVel = rotVec.add(random.nextDouble() * 1.5 - 0.75, random.nextDouble() * 1.5 - 0.75, random.nextDouble() * 1.5 - 0.75);
            particlePos = ffPos.add(addVel);

            attacker.level().addParticle(ParticleTypes.POOF,
                    particlePos.x,
                    particlePos.y,
                    particlePos.z,
                    -addVel.x / 10.0, -addVel.y / 10.0, -addVel.z / 10.0);
        } else {
            attacker.setInhaleTime(--inhaleTime);

            if (inhaleTime > 0) {
                attacker.setRotationOffset(90);
            } else {
                attacker.setRotationOffset(225);
            }
            if (attacker.tickCount % 2 != 0) {
                return;
            }

            AABB fBox = createBox(fPos, 2);
            AABB ffBox = createBox(ffPos, 2);

            JUtils.displayHitbox(attacker.level(), fBox);
            JUtils.displayHitbox(attacker.level(), ffBox);
            Set<Entity> hits = AbstractSimpleAttack.findHits(attacker, Set.of(fBox, ffBox), null, Entity.class);

            for (Entity entity : hits) {
                double distance = entity.position().distanceTo(eyePos);
                if (distance > 3) // Falloff
                {
                    distance -= distance * distance * 0.1;
                }

                entity.setDeltaMovement(entity.getDeltaMovement()
                        .subtract(rotVec.x, 0, rotVec.z)
                        .scale(0.2 * distance));

                entity.hurtMarked = true;

                if (entity instanceof ServerPlayer player) {
                    player.connection.send(new ClientboundSetEntityMotionPacket(player));
                }
            }
        }
    }

    @Override
    protected @NonNull InhaleAttack getThis() {
        return this;
    }

    @Override
    public @NonNull InhaleAttack copy() {
        return copyExtras(new InhaleAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getInhaleDuration()));
    }
}
