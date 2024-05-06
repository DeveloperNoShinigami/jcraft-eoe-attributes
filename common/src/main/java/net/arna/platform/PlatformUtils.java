package net.arna.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;

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
}
