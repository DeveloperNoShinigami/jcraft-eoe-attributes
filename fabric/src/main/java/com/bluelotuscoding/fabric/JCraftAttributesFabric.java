package com.bluelotuscoding.fabric;
 
import com.bluelotuscoding.JCraftAttributes;
import net.fabricmc.api.ModInitializer;
 
public class JCraftAttributesFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        JCraftAttributes.init();
        
        // Add info to crash reports
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback.RegistrationContext.class.getName(); // Just to trigger some Fabric classes
        });
        
        // Use the official Crash Report Info API if available
        // Note: In 1.20.1 Fabric API, this is usually handled via Entrypoints or explicit registration
    }
}
