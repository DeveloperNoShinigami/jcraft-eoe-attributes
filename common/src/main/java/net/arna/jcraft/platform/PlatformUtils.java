package net.arna.jcraft.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.arna.jcraft.common.component.entity.CommonGrabComponent;
import net.arna.jcraft.common.component.entity.CommonTimeStopComponent;
import net.arna.jcraft.common.component.living.*;
import net.arna.jcraft.common.component.player.CommonPhComponent;
import net.arna.jcraft.common.component.player.CommonSpecComponent;
import net.arna.jcraft.common.component.world.CommonShockwaveHandlerComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

/** What's Sterner Cooking???
 *
 * So some things are only available on either forge or fabric, so we need a way to use api specific methods sometimes.
 * Both our forge and fabric project have a {@link net/arna/platform/PlatformUtilsImpl} which handles the platform.
 */
public class PlatformUtils {

    /**
     * Check of a mod is loaded in runtime
     * @param modId mod id, for example "computercraft"
     * @return true if the mod is loaded
     */
    @ExpectPlatform
    static boolean isModLoaded(String modId) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static CommonStandComponent getStandData(LivingEntity entity) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static CommonSpecComponent getSpecData(PlayerEntity player) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static CommonPhComponent getPhData(PlayerEntity player) {
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
    public static CommonGravityShiftComponent getGravityShift(LivingEntity livingEntity) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static CommonShockwaveHandlerComponent getShockwaveHandler(World world) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static CommonVampireComponent getVampirism(LivingEntity living) {
        throw new AssertionError();
    }
}
