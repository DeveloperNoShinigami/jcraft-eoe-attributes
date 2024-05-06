package net.arna.platform;

import net.fabricmc.loader.api.FabricLoader;

public class PlatformUtilsImpl {

    public static boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }
}
