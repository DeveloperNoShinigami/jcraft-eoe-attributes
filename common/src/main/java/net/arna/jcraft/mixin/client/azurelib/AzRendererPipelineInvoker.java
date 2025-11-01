package net.arna.jcraft.mixin.client.azurelib;

import mod.azure.azurelib.render.AzRendererPipeline;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AzRendererPipeline.class)
public interface AzRendererPipelineInvoker<K, T> {

    @Invoker(value = "updateAnimatedTextureFrame", remap = false)
    void updateAnimatedTextureFrame(T animatable);

}
