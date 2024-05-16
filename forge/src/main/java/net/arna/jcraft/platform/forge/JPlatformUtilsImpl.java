package net.arna.jcraft.platform.forge;

import net.arna.jcraft.client.rendering.api.MultiInstancePostProcessor;
import net.arna.jcraft.client.rendering.post.TimestopShaderFX;
import net.arna.jcraft.forge.client.JShaderRegistry;
import net.minecraft.client.gl.ShaderProgram;

public class JPlatformUtilsImpl {

    public static MultiInstancePostProcessor<TimestopShaderFX> getZaWarudo(){
        return JShaderRegistry.ZA_WARUDO;
    }

    public static ShaderProgram getTest() {
        return JShaderRegistry.TEST.getInstance().get();
    }

    public static ShaderProgram getRred() {
        return JShaderRegistry.RREDE.getInstance().get();
    }
}
