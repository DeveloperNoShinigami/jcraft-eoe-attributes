package net.arna.jcraft.fabric.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.shaders.AbstractUniform;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.arna.jcraft.client.rendering.IJShader;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ChainedJsonException;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.util.GsonHelper;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * This class is for adding CORE shaders, not post processed
 */
public abstract class JShader extends ShaderInstance implements IJShader {

    protected Map<String, Consumer<AbstractUniform>> defaultUniformData;

    public JShader(ResourceProvider factory, ResourceLocation name, VertexFormat format) throws IOException {
        super(factory, name.toString(), format);
    }

    @Override
    public void setUniformDefaults() {
        for (Map.Entry<String, Consumer<AbstractUniform>> defaultDataEntry : getDefaultUniformData().entrySet()) {
            final AbstractUniform t = uniformMap.get(defaultDataEntry.getKey());
            defaultDataEntry.getValue().accept(t);
            float f = 0;
        }
    }

    public Map<String, Consumer<AbstractUniform>> getDefaultUniformData() {
        if (defaultUniformData == null) {
            defaultUniformData = new HashMap<>();
        }
        return defaultUniformData;
    }

    public abstract ShaderHolder getHolder();

    @Override
    public void parseUniformNode(JsonElement pJson) throws ChainedJsonException {
        super.parseUniformNode(pJson);

        JsonObject jsonobject = GsonHelper.convertToJsonObject(pJson, "uniform");
        String uniformName = GsonHelper.getAsString(jsonobject, "name");
        if (getHolder().uniformsToCache.contains(uniformName)) {
            Uniform uniform = uniforms.get(uniforms.size() - 1);

            Consumer<AbstractUniform> consumer;
            if (uniform.getType() <= 3) {
                final IntBuffer buffer = uniform.getIntBuffer();
                buffer.position(0);
                int[] array = new int[uniform.getCount()];
                for (int i = 0; i < uniform.getCount(); i++) {
                    array[i] = buffer.get(i);
                }
                consumer = u -> {
                    buffer.position(0);
                    buffer.put(array);
                };
            } else {
                final FloatBuffer buffer = uniform.getFloatBuffer();
                buffer.position(0);
                float[] array = new float[uniform.getCount()];
                for (int i = 0; i < uniform.getCount(); i++) {
                    array[i] = buffer.get(i);
                }
                consumer = u -> {
                    buffer.position(0);
                    buffer.put(array);
                };
            }

            getDefaultUniformData().put(uniformName, consumer);
        }
    }
}
