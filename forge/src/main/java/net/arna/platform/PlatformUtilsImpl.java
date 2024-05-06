package net.arna.platform;

import net.minecraftforge.fml.ModList;


public class PlatformUtilsImpl {

    public static boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }
}
