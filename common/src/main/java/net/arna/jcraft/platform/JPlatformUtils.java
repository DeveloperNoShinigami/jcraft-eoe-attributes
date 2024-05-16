package net.arna.jcraft.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.arna.jcraft.client.rendering.api.MultiInstancePostProcessor;
import net.arna.jcraft.client.rendering.post.TimestopShaderFX;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gl.ShaderProgram;

public class JPlatformUtils {

    @ExpectPlatform
    public static MultiInstancePostProcessor<TimestopShaderFX> getZaWarudo(){
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @ExpectPlatform
    public static ShaderProgram getTest() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @ExpectPlatform
    public static ShaderProgram getRred() {
        throw new UnsupportedOperationException("Not implemented yet");
    }



}
