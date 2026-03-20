package com.bluelotuscoding;

import com.bluelotuscoding.api.config.JAttributeConfig;
import com.bluelotuscoding.api.registry.JAttributeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JCraftAttributes {
    public static final String MOD_ID = "jcraft_attributes";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void init() {
        LOGGER.info("[JCraft Attributes] Initializing Version 1.1.0...");

        try {
            // 1. Dependency Check
            checkDependencies();

            // 2. Mod Initialization
            JAttributeConfig.load();
            JAttributeRegistry.init();

            dev.architectury.event.events.common.CommandRegistrationEvent.EVENT.register((dispatcher, registrySelection, selection) -> {
                try {
                    com.bluelotuscoding.api.command.JAttributeCommands.register(dispatcher);
                } catch (Exception e) {
                    LOGGER.error("[JCraft Attributes] Failed to register commands", e);
                }
            });

            net.arna.jcraft.common.events.JEntityEvents.POST_ADD.register((entity, worldGenSpawned) -> {
                try {
                    if (entity instanceof net.minecraft.world.entity.LivingEntity living) {
                        com.bluelotuscoding.api.attribute.StandAttributeManager.updateAttributes(living);
                    }
                } catch (Exception e) {
                    LOGGER.error("[JCraft Attributes] Error in entity POST_ADD event", e);
                }
                return dev.architectury.event.EventResult.pass();
            });

            LOGGER.info("[JCraft Attributes] Initialization finished successfully.");
        } catch (Exception e) {
            LOGGER.error("[JCraft Attributes] CRITICAL: Initialization failed during startup!", e);
            // We still throw if core registry fails, but individual event failures are caught above.
            throw new RuntimeException("JCraft Attributes failed to initialize", e);
        }
    }

    private static void checkDependencies() {
        String[] mods = {"jcraft", "azurelib", "player-animator", "cardinal-components-base", "cloth-config"};
        for (String mod : mods) {
            if (net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded(mod)) {
                String version = net.fabricmc.loader.api.FabricLoader.getInstance()
                    .getModContainer(mod)
                    .map(m -> m.getMetadata().getVersion().getFriendlyString())
                    .orElse("Unknown");
                LOGGER.info("[JCraft Attributes] Detected dependency: {} (Version: {})", mod, version);
            } else {
                LOGGER.warn("[JCraft Attributes] MISSING RECOMMENDED DEPENDENCY: {}. This may cause crashes or broken features!", mod);
            }
        }
    }
}
