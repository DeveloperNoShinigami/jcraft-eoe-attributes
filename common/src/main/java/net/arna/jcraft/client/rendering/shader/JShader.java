package net.arna.jcraft.client.rendering.shader;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.Uniform;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidHierarchicalFileException;
import net.minecraft.util.JsonHelper;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * This class is for adding CORE shaders, not post processed
 */
public abstract class JShader extends ShaderProgram {

    protected Map<String, Consumer<Uniform>> defaultUniformData;

    public JShader(ResourceFactory factory, Identifier name, VertexFormat format) throws IOException {
        super(factory, name.toString(), format);
    }

    public void setUniformDefaults() {
        for (Map.Entry<String, Consumer<Uniform>> defaultDataEntry : getDefaultUniformData().entrySet()) {
            final Uniform t = loadedUniforms.get(defaultDataEntry.getKey());
            defaultDataEntry.getValue().accept(t);
            float f = 0;
        }
    }

    public Map<String, Consumer<Uniform>> getDefaultUniformData() {
        if (defaultUniformData == null) {
            defaultUniformData = new HashMap<>();
        }
        return defaultUniformData;
    }

    public abstract ShaderHolder getHolder();

    @Override
    public void addUniform(JsonElement pJson) throws InvalidHierarchicalFileException {
        super.addUniform(pJson);

        JsonObject jsonobject = JsonHelper.asObject(pJson, "uniform");
        String uniformName = JsonHelper.getString(jsonobject, "name");
        if (getHolder().uniformsToCache.contains(uniformName)) {
            GlUniform uniform = uniforms.get(uniforms.size() - 1);

            Consumer<Uniform> consumer;
            if (uniform.getDataType() <= 3) {
                final IntBuffer buffer = uniform.getIntData();
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
                final FloatBuffer buffer = uniform.getFloatData();
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
