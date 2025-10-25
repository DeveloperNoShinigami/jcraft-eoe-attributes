package net.arna.jcraft.client.renderer.entity.layer;

import mod.azure.azurelib.model.AzBone;
import mod.azure.azurelib.render.AzRendererPipelineContext;
import mod.azure.azurelib.render.layer.AzRenderLayer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.Entity;

import java.util.UUID;

@Environment(EnvType.CLIENT)
public abstract class AbstractRenderLayer<T extends Entity> implements AzRenderLayer<UUID, T> {

    protected AbstractRenderLayer() {
        /* Left empty on purpose */
    }

    @Override
    public void preRender(AzRendererPipelineContext<UUID, T> context) {
        /* Left empty on purpose */
    }

    @Override
    public void render(AzRendererPipelineContext<UUID, T> context) {
        /* Left empty on purpose */
    }

    @Override
    public void renderForBone(AzRendererPipelineContext<UUID, T> context, AzBone bone) {
        /* Left empty on purpose */
    }
}
