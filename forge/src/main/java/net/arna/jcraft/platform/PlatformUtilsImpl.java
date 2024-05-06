package net.arna.jcraft.platform;

import net.minecraftforge.fml.ModList;


public class PlatformUtilsImpl {

    public static boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }
}
