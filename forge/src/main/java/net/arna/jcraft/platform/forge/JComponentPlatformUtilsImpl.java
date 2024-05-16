package net.arna.jcraft.platform.forge;


import net.arna.jcraft.common.component.entity.CommonGrabComponent;
import net.arna.jcraft.common.component.entity.CommonGravityComponent;
import net.arna.jcraft.common.component.living.*;
import net.arna.jcraft.common.component.player.CommonPhComponent;
import net.arna.jcraft.common.component.player.CommonSpecComponent;
import net.arna.jcraft.common.component.world.CommonShockwaveHandlerComponent;
import net.arna.jcraft.forge.capability.impl.entity.GrabCapability;
import net.arna.jcraft.forge.capability.impl.entity.TimeStopCapability;
import net.arna.jcraft.forge.capability.impl.living.*;
import net.arna.jcraft.forge.capability.impl.player.PhCapability;
import net.arna.jcraft.forge.capability.impl.player.SpecCapability;
import net.arna.jcraft.forge.capability.impl.world.ShockwaveHandlerCapability;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import java.util.Optional;

public class JComponentPlatformUtilsImpl {
    public static CommonStandComponent getStandData(LivingEntity entity) {
        return StandCapability.getCapability(entity);
    }


    public static CommonSpecComponent getSpecData(Player player) {
        return SpecCapability.getCapability(player);
    }


    public static CommonPhComponent getPhData(Player player) {
        return PhCapability.getCapability(player);
    }


    public static CommonCooldownsComponent getCooldowns(LivingEntity entity) {
        return CooldownsCapability.getCapability(entity);
    }


    public static Optional<TimeStopCapability> getTimeStopData(Entity entity) {
        return TimeStopCapability.getCapabilityOptional(entity);
    }


    public static CommonMiscComponent getMiscData(LivingEntity entity) {
        return MiscCapability.getCapability(entity);
    }


    public static CommonBombTrackerComponent getBombTracker(LivingEntity entity) {
        return BombTrackerCapability.getCapability(entity);
    }


    public static CommonGrabComponent getGrab(LivingEntity entity) {
        return GrabCapability.getCapability(entity);
    }


    public static CommonHitPropertyComponent getHitProperties(LivingEntity livingEntity) {
        return HitPropertyCapability.getCapability(livingEntity);
    }

    public static Optional<CommonGravityComponent> getGravity(Entity entity) {
        return Optional.empty(); //TODO
    }

    public static CommonGravityShiftComponent getGravityShift(Entity entity) {
        return null; //TODO
    }

    public static CommonShockwaveHandlerComponent getShockwaveHandler(Level world) {
        return ShockwaveHandlerCapability.getCapability(world);
    }


    public static CommonVampireComponent getVampirism(LivingEntity living) {
        return VampireCapability.getCapability(living);
    }
}
