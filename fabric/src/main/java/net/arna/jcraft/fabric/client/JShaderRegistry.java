package net.arna.jcraft.fabric.client;

import com.mojang.datafixers.util.Pair;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.client.rendering.api.PostProcessHandler;
import net.arna.jcraft.client.rendering.post.TimestopShaderPostProcessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.resource.ResourceFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class JShaderRegistry {
    public static List<Pair<ShaderProgram, Consumer<ShaderProgram>>> shaderList;

    //Core
    public static ShaderHolder TEST = new ShaderHolder(JCraft.id("space"), VertexFormats.POSITION_TEXTURE,"DiffuseSampler", "DepthSampler", "OutSize", "ViewPort");

    public static ShaderHolder RREDE = new ShaderHolder( JCraft.id("rrede"), VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);

    //Post Processed
    public static final TimestopShaderPostProcessor ZA_WARUDO = new TimestopShaderPostProcessor();

    public static void init(ResourceFactory manager) throws IOException {
        shaderList = new ArrayList<>();
        registerShader(TEST.createInstance(manager));
        registerShader(RREDE.createInstance(manager));

        PostProcessHandler.addInstance(ZA_WARUDO);
    }

    public static void registerShader(JShader jShaderInstance) {
        registerShader(jShaderInstance, (shader) -> {

        });
    }

    public static void registerShader(ShaderProgram shader, Consumer<ShaderProgram> onLoaded) {
        shaderList.add(Pair.of(shader, onLoaded));
    }
}
