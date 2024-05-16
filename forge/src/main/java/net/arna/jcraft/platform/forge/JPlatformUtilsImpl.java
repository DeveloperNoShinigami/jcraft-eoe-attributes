package net.arna.jcraft.platform.forge;

import net.arna.jcraft.client.rendering.api.MultiInstancePostProcessor;
import net.arna.jcraft.client.rendering.post.TimestopShaderFX;
import net.arna.jcraft.forge.client.JShaderRegistry;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;

public class JPlatformUtilsImpl {

    public static MultiInstancePostProcessor<TimestopShaderFX> getZaWarudo(){
        return JShaderRegistry.ZA_WARUDO;
    }

    public static ShaderInstance getTest() {
        return JShaderRegistry.TEST.getInstance().get();
    }

    public static ShaderInstance getRred() {
        return JShaderRegistry.RREDE.getInstance().get();
    }

    public static boolean isModLoaded(String name) {
        return ModList.get().isLoaded("name");
    }

    public static boolean isDevelopmentEnvironment() {
        return false; //TODO?
    }
}
