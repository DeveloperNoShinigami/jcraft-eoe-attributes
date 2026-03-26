package com.bluelotuscoding.fabric;
 
import com.bluelotuscoding.JCraftAttributes;
import net.fabricmc.api.ModInitializer;
 
public class JCraftAttributesFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        JCraftAttributes.init();
    }
}
