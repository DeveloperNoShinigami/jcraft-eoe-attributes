package net.arna.jcraft.mixin.client.azurelib;

import mod.azure.azurelib.render.entity.AzEntityRendererPipeline;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AzEntityRendererPipeline.class)
public interface AzEntityRendererPipelineAccessor {
    @Accessor(value = "modelRenderTranslations", remap = false)
    Matrix4f getModelRenderTranslations();
}
