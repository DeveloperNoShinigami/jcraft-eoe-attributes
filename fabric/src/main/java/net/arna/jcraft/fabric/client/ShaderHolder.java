package net.arna.jcraft.fabric.client;

import com.mojang.blaze3d.vertex.VertexFormat;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;

/**
 * So we can get more uniforms for our CORE shaders
 */
public class ShaderHolder {

    public final ResourceLocation shaderLocation;
    public final VertexFormat shaderFormat;

    protected JShader shaderInstance;
    public Collection<String> uniformsToCache;
    private final RenderStateShard.ShaderStateShard shard = new RenderStateShard.ShaderStateShard(getInstance());

    public ShaderHolder(ResourceLocation shaderLocation, VertexFormat shaderFormat, String... uniformsToCache) {
        this.shaderLocation = shaderLocation;
        this.shaderFormat = shaderFormat;
        this.uniformsToCache = new ArrayList<>(List.of(uniformsToCache));
    }

    public JShader createInstance(ResourceProvider provider) throws IOException {
        ShaderHolder shaderHolder = this;
        JShader shaderInstance = new JShader(provider, shaderLocation, shaderFormat) {
            @Override
            public ShaderHolder getHolder() {
                return shaderHolder;
            }
        };
        this.shaderInstance = shaderInstance;
        return shaderInstance;
    }

    public Supplier<ShaderInstance> getInstance() {
        return () -> shaderInstance;
    }

    public RenderStateShard.ShaderStateShard getShard() {
        return shard;
    }
}