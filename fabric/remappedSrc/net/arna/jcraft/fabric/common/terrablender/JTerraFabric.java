package net.arna.jcraft.fabric.common.terrablender;

import net.arna.jcraft.JCraft;
import terrablender.api.Regions;
import terrablender.api.SurfaceRuleManager;
import terrablender.api.TerraBlenderApi;

public class JTerraFabric implements TerraBlenderApi {
    private static boolean initialized = false;
    private static boolean modInitialized = false;

    @Override
    public void onTerraBlenderInitialized() {
        Regions.register(new OverworldRegionFabric(JCraft.id("overworld"), 4));
        initialized = true;
        if (modInitialized)
            registerSurfaceRules();
    }

    public static void onModInitialized() {
        modInitialized = true;
        if (initialized)
            registerSurfaceRules();
    }

    private static void registerSurfaceRules() {
        SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD, JCraft.MOD_ID, MaterialRulesFabric.makeRules());
    }
}
