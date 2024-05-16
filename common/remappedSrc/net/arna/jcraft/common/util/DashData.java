package net.arna.jcraft.common.util;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.component.living.CommonCooldownsComponent;
import net.arna.jcraft.common.network.s2c.PlayerAnimPacket;
import net.arna.jcraft.common.spec.JSpec;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.arna.jcraft.registry.JStatusRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class DashData {
    public final Vec3 dashVector;
    public final LivingEntity entity;
    public boolean finished = false;
    private int duration = 10;

    public DashData(Vec3 dashVector, LivingEntity entity) {
        this.dashVector = dashVector;
        this.entity = entity;
    }

    public void tickDash() {
        duration--;
        if (entity.hasEffect(JStatusRegistry.DAZED.get())) { // Being stunned stops dashes
            finished = true;
            return;
        }
        if (duration <= 5) { // 5 ticks of movement, then recovery
            if (duration <= 0) {
                finished = true;
            }
            return;
        }
        entity.setDeltaMovement(entity.getDeltaMovement().add(dashVector).scale(0.5));
        entity.hurtMarked = true;
    }

    public static boolean isDashing(LivingEntity entity) {
        return JCraft.dashes.containsKey(entity);
    }

    public static DashData getDash(LivingEntity entity) {
        return JCraft.dashes.get(entity);
    }

    public static void tryDash(int forward, int side, LivingEntity entity) {
        CommonCooldownsComponent cooldowns = JComponentPlatformUtils.getCooldowns(entity);
        if (cooldowns.getCooldown(CooldownType.DASH) > 0 || !entity.onGround() || entity.hasEffect(JStatusRegistry.DAZED.get()) || entity.hasEffect(JStatusRegistry.KNOCKDOWN.get())) {
            return;
        }
        cooldowns.setCooldown(CooldownType.DASH, JCraft.dashCooldown);

        double dashSpeed = 0.75;
        Vec3 rotVec = Vec3.directionFromRotation(entity.getXRot(), entity.getYRot());
        rotVec = rotVec.yRot(1.57079632679f * side); // L/R

        if (side != 0) {
            dashSpeed *= 0.75; // Sideways speed nerf
            if (forward == 1) {
                rotVec = rotVec.yRot(-0.785398163397f * side); // Forward diagonals
            }
        }
        if (forward == -1) {
            rotVec = rotVec.yRot(side == 0 ? 3.14159265359f : 0.785398163397f * side); // Back diagonals
            dashSpeed *= 0.75; // Backwards speed nerf
        }

        JCraft.dashes.put(entity, new DashData(rotVec.normalize().scale(dashSpeed), entity));

        // Syncs dash anim (unless already attacking with a spec) with every player in the vicinity
        if (entity instanceof ServerPlayer player) {
            JSpec<?, ?> spec = JUtils.getSpec(player);

            if (spec == null || spec.moveStun < 1) {
                JUtils.around((ServerLevel) entity.level(), entity.position(), 96).forEach(
                        serverPlayer -> PlayerAnimPacket.send(player, serverPlayer, "dash"));
            }
        }
    }
}