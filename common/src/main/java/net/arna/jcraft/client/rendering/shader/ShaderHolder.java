package net.arna.jcraft.client.rendering.shader;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

/**
 * So we can get more uniforms for our CORE shaders
 */
public class ShaderHolder {

    public final Identifier shaderLocation;
    public final VertexFormat shaderFormat;

    protected JShader shaderInstance;
    public Collection<String> uniformsToCache;
    private final RenderPhase.ShaderProgram shard = new RenderPhase.ShaderProgram(getInstance());

    public ShaderHolder(Identifier shaderLocation, VertexFormat shaderFormat, String... uniformsToCache) {
        this.shaderLocation = shaderLocation;
        this.shaderFormat = shaderFormat;
        this.uniformsToCache = new ArrayList<>(List.of(uniformsToCache));
    }

    public JShader createInstance(ResourceFactory provider) throws IOException {
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

    public Supplier<ShaderProgram> getInstance() {
        return () -> shaderInstance;
    }

    public RenderPhase.ShaderProgram getShard() {
        return shard;
    }
}