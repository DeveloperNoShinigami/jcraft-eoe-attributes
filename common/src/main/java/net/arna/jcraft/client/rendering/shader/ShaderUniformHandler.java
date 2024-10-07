package net.arna.jcraft.client.rendering.shader;

import net.minecraft.client.renderer.ShaderInstance;

@FunctionalInterface
public interface ShaderUniformHandler {
    void updateShaderData(final ShaderInstance instance);
}