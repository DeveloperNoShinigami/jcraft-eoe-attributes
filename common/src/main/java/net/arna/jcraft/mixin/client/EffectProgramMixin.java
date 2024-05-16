package net.arna.jcraft.mixin.client;

import com.mojang.blaze3d.shaders.EffectProgram;
import net.arna.jcraft.client.rendering.api.JGLImportProcessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(EffectProgram.class)
public class EffectProgramMixin {
/*
    @ModifyArg(method = "createFromResource", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gl/EffectShaderStage;load(Lnet/minecraft/client/gl/ShaderStage$Type;Ljava/lang/String;Ljava/io/InputStream;Ljava/lang/String;Lnet/minecraft/client/gl/GlImportProcessor;)I"
    ), index = 4)
    private static GlImportProcessor jcraft$useCustomPreprocessor(GlImportProcessor par5) {
        return new JGLImportProcessor();
    }

 */
}
