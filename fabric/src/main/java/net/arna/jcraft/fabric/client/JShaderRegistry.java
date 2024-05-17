package net.arna.jcraft.fabric.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.datafixers.util.Pair;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.client.rendering.api.PostProcessHandler;
import net.arna.jcraft.client.rendering.post.TimestopShaderPostProcessor;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceProvider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class JShaderRegistry {
    public static List<Pair<ShaderInstance, Consumer<ShaderInstance>>> shaderList;

    //Core
    public static ShaderHolder TEST = new ShaderHolder(JCraft.id("space"), DefaultVertexFormat.POSITION_TEX,"DiffuseSampler", "DepthSampler", "OutSize", "ViewPort");

    public static ShaderHolder RREDE = new ShaderHolder( JCraft.id("rrede"), DefaultVertexFormat.NEW_ENTITY);

    //Post Processed
    public static final TimestopShaderPostProcessor ZA_WARUDO = new TimestopShaderPostProcessor();

    public static void init(ResourceProvider manager) throws IOException {
        shaderList = new ArrayList<>();
        registerShader(TEST.createInstance(manager));
        registerShader(RREDE.createInstance(manager));

        PostProcessHandler.addInstance(ZA_WARUDO);
    }

    public static void registerShader(JShader jShaderInstance) {
        registerShader(jShaderInstance, (shader) -> {

        });
    }

    public static void registerShader(ShaderInstance shader, Consumer<ShaderInstance> onLoaded) {
        shaderList.add(Pair.of(shader, onLoaded));
    }
}
