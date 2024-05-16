package net.arna.jcraft.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.arna.jcraft.common.component.entity.CommonGrabComponent;
import net.arna.jcraft.common.component.entity.CommonGravityComponent;
import net.arna.jcraft.common.component.entity.CommonTimeStopComponent;
import net.arna.jcraft.common.component.living.*;
import net.arna.jcraft.common.component.player.CommonPhComponent;
import net.arna.jcraft.common.component.player.CommonSpecComponent;
import net.arna.jcraft.common.component.world.CommonShockwaveHandlerComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import java.util.Optional;

/**
 * What's Sterner Cooking???
 * <p>
 * So some things are only available on either forge or fabric, so we need a way to use api specific methods sometimes.
 * Both our forge and fabric project have a {@link net/arna/platform/$/JComponentPlatformUtilsImpl} which handles the platform.
 */
public class JComponentPlatformUtils {

    @ExpectPlatform
    public static CommonStandComponent getStandData(LivingEntity entity) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static CommonSpecComponent getSpecData(Player player) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static CommonPhComponent getPhData(Player player) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static CommonCooldownsComponent getCooldowns(LivingEntity entity) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static CommonTimeStopComponent getTimeStopData(Entity entity) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static CommonMiscComponent getMiscData(LivingEntity entity) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static CommonBombTrackerComponent getBombTracker(LivingEntity entity) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static CommonGrabComponent getGrab(LivingEntity entity) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static CommonHitPropertyComponent getHitProperties(LivingEntity livingEntity) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Optional<CommonGravityComponent> getGravity(Entity entity) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static CommonGravityShiftComponent getGravityShift(Entity entity) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static CommonShockwaveHandlerComponent getShockwaveHandler(Level world) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static CommonVampireComponent getVampirism(LivingEntity living) {
        throw new AssertionError();
    }
}
