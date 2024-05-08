package net.arna.jcraft.common.util;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.component.living.CommonCooldownsComponent;
import net.arna.jcraft.common.network.s2c.PlayerAnimPacket;
import net.arna.jcraft.common.spec.JSpec;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.arna.jcraft.registry.JStatusRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class DashData {
    public final Vec3d dashVector;
    public final LivingEntity entity;
    public boolean finished = false;
    private int duration = 10;

    public DashData(Vec3d dashVector, LivingEntity entity) {
        this.dashVector = dashVector;
        this.entity = entity;
    }

    public void tickDash() {
        duration--;
        if (entity.hasStatusEffect(JStatusRegistry.DAZED)) { // Being stunned stops dashes
            finished = true;
            return;
        }
        if (duration <= 5) { // 5 ticks of movement, then recovery
            if (duration <= 0) {
                finished = true;
            }
            return;
        }
        entity.setVelocity(entity.getVelocity().add(dashVector).multiply(0.5));
        entity.velocityModified = true;
    }

    public static boolean isDashing(LivingEntity entity) {
        return JCraft.dashes.containsKey(entity);
    }

    public static DashData getDash(LivingEntity entity) {
        return JCraft.dashes.get(entity);
    }

    public static void tryDash(int forward, int side, LivingEntity entity) {
        CommonCooldownsComponent cooldowns = JComponentPlatformUtils.getCooldowns(entity);
        if (cooldowns.getCooldown(CooldownType.DASH) > 0 || !entity.isOnGround() || entity.hasStatusEffect(JStatusRegistry.DAZED) || entity.hasStatusEffect(JStatusRegistry.KNOCKDOWN)) {
            return;
        }
        cooldowns.setCooldown(CooldownType.DASH, JCraft.dashCooldown);

        double dashSpeed = 0.75;
        Vec3d rotVec = Vec3d.fromPolar(entity.getPitch(), entity.getYaw());
        rotVec = rotVec.rotateY(1.57079632679f * side); // L/R

        if (side != 0) {
            dashSpeed *= 0.75; // Sideways speed nerf
            if (forward == 1) {
                rotVec = rotVec.rotateY(-0.785398163397f * side); // Forward diagonals
            }
        }
        if (forward == -1) {
            rotVec = rotVec.rotateY(side == 0 ? 3.14159265359f : 0.785398163397f * side); // Back diagonals
            dashSpeed *= 0.75; // Backwards speed nerf
        }

        JCraft.dashes.put(entity, new DashData(rotVec.normalize().multiply(dashSpeed), entity));

        // Syncs dash anim (unless already attacking with a spec) with every player in the vicinity
        if (entity instanceof ServerPlayerEntity player) {
            JSpec<?, ?> spec = JUtils.getSpec(player);

            if (spec == null || spec.moveStun < 1) {
                JUtils.around((ServerWorld) entity.getWorld(), entity.getPos(), 96).forEach(
                        serverPlayer -> PlayerAnimPacket.send(player, serverPlayer, "dash"));
            }
        }
    }
}