package com.bluelotuscoding.forge;
 
import com.bluelotuscoding.JCraftAttributes;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
 
@Mod("jcraft_attributes")
public class JCraftAttributesForge {
    public JCraftAttributesForge() {
        JCraftAttributes.init();
    }
}
