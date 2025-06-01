package net.arna.jcraft.api.forge;

import net.arna.jcraft.api.StandType2;
import net.arna.jcraft.forge.JCraftForge;
import net.minecraft.core.Registry;

public class JRegistryHolderImpl {
    public static Registry<StandType2> getStandTypeRegistry() {
        return JCraftForge.getStandTypeRegistry();
    }
}
