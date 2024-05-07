package net.arna.jcraft.platform;

import net.arna.jcraft.common.component.entity.CommonGrabComponent;
import net.arna.jcraft.common.component.entity.CommonTimeStopComponent;
import net.arna.jcraft.common.component.living.*;
import net.arna.jcraft.common.component.player.CommonPhComponent;
import net.arna.jcraft.common.component.player.CommonSpecComponent;
import net.arna.jcraft.common.component.world.CommonShockwaveHandlerComponent;
import net.arna.jcraft.fabric.common.component.JComponents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class PlatformUtilsImpl {

    public static boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    
    public static CommonStandComponent getStandData(LivingEntity entity) {
        return JComponents.STAND.get(entity);
    }

    
    public static CommonSpecComponent getSpecData(PlayerEntity player) {
        return JComponents.SPEC.get(player);
    }

    
    public static CommonPhComponent getPhData(PlayerEntity player) {
        return JComponents.PH.get(player);
    }

    
    public static CommonCooldownsComponent getCooldowns(LivingEntity entity) {
        return JComponents.COOLDOWNS.get(entity);
    }

    
    public static CommonTimeStopComponent getTimeStopData(Entity entity) {
        return JComponents.TIME_STOP.get(entity);
    }

    
    public static CommonMiscComponent getMiscData(LivingEntity entity) {
        return JComponents.MISC.get(entity);
    }

    
    public static CommonBombTrackerComponent getBombTracker(LivingEntity entity) {
        return JComponents.BOMB_TRACKER.get(entity);
    }

    
    public static CommonGrabComponent getGrab(LivingEntity entity) {
        return JComponents.GRAB.get(entity);
    }

    
    public static CommonHitPropertyComponent getHitProperties(LivingEntity livingEntity) {
        return JComponents.HIT_PROPERTY.get(livingEntity);
    }

    
    public static CommonGravityShiftComponent getGravityShift(LivingEntity livingEntity) {
        return JComponents.GRAVITY_SHIFT.get(livingEntity);
    }

    
    public static CommonShockwaveHandlerComponent getShockwaveHandler(World world) {
        return JComponents.SHOCKWAVE_HANDLER.get(world);
    }

    
    public static CommonVampireComponent getVampirism(LivingEntity living) {
        return JComponents.VAMPIRE.get(living);
    }
}
