package net.arna.jcraft.platform;

import net.fabricmc.loader.api.FabricLoader;

public class PlatformUtilsImpl {

    public static boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }
}
