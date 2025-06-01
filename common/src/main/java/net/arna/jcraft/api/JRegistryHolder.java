package net.arna.jcraft.api;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.Registry;

/**
 * Utility class for access to JCraft registries.
 */
public class JRegistryHolder {
    @ExpectPlatform
    public static Registry<StandType2> getStandTypeRegistry() {
        throw new AssertionError("This should not happen");
    }
}
