package net.arna.jcraft.common.attack.moves.shared;

import lombok.Getter;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.IAttacker;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.component.living.CommonCooldownsComponent;
import net.arna.jcraft.common.tickable.Timestops;
import net.arna.jcraft.common.util.CooldownType;
import net.arna.jcraft.common.attack.MobilityType;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import java.util.List;
import java.util.Set;

@Getter
public final class TimeSkipMove<A extends IAttacker<? extends A, ?>> extends AbstractMove<TimeSkipMove<A>, A> {
    private final double distance;

    public TimeSkipMove(int cooldown, double distance) {
        super(cooldown, 0, 0, 0);
        this.distance = distance;
        mobilityType = MobilityType.TELEPORT;
    }

    @Override
    public boolean canBeInitiated(A attacker) {
        if (Timestops.getTimestop(attacker.getUser()) != null) {
            return false;
        }
        return super.canBeInitiated(attacker);
    }

    @Override
    public void onInitiate(A attacker) {
        // Don't play the sounds
        getInitActions().forEach(action -> action.perform(attacker, attacker.getUser(), attacker.getMoveContext()));
    }

    @Override
    public @NonNull Set<LivingEntity> perform(A attacker, LivingEntity user, MoveContext ctx) {
        doTimeSkip(attacker, user, distance, getSounds());

        return Set.of();
    }

    public static void doTimeSkip(IAttacker<?, ?> attacker, LivingEntity user, double distance, List<SoundEvent> sounds) {
        boolean hasVehicle = user.isPassenger();

        if (hasVehicle) {
            distance /= 3;
        }

        Vec3 rotVec = user.getLookAngle();
        //todo: find length of line with direction rotVec, from the center of the stand users bounding box to the edge
        //      then subtract that from the position. this should prevent any TP clipping bullshit
        Vec3 eyePos = user.getEyePosition();
        HitResult hitResult = attacker.getEntityWorld().clip(
                new ClipContext(
                        eyePos,
                        eyePos.add(rotVec.scale(distance)),
                        ClipContext.Block.COLLIDER,
                        ClipContext.Fluid.NONE, user));
        Vec3 tpPos = hitResult.getLocation();

        // 3s minimum ult cooldown
        CommonCooldownsComponent cooldowns = JComponentPlatformUtils.getCooldowns(user);
        if (cooldowns.getCooldown(CooldownType.STAND_ULTIMATE) < 60) {
            cooldowns.setCooldown(CooldownType.STAND_ULTIMATE, 60);
        }

        if (hasVehicle) {
            user.getRootVehicle().setPos(tpPos.x, tpPos.y, tpPos.z);
        } else {
            user.teleportToWithTicket(tpPos.x, tpPos.y, tpPos.z);
        }

        for (SoundEvent sound : sounds) {
            attacker.getEntityWorld().playSound(null, tpPos.x, tpPos.y, tpPos.z, sound, SoundSource.PLAYERS, 1f, 1f);
        }
    }

    @Override
    protected @NonNull TimeSkipMove<A> getThis() {
        return this;
    }

    @Override
    public @NonNull TimeSkipMove<A> copy() {
        return copyExtras(new TimeSkipMove<>(getCooldown(), distance));
    }
}
