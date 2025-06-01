package net.arna.jcraft.api.fabric;

import net.arna.jcraft.api.StandType2;
import net.arna.jcraft.fabric.JCraftFabric;
import net.minecraft.core.Registry;

public class JRegistryHolderImpl {
    public static Registry<StandType2> getStandTypeRegistry() {
        return JCraftFabric.getStandTypeRegistry();
    }
}
