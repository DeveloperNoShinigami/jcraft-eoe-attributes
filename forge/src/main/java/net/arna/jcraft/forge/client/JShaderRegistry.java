package net.arna.jcraft.forge.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.datafixers.util.Pair;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.client.rendering.api.PostProcessHandler;
import net.arna.jcraft.client.rendering.post.TimestopShaderPostProcessor;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = JCraft.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class JShaderRegistry {
    //Core
    public static ShaderHolder TEST = new ShaderHolder(JCraft.id("space"), DefaultVertexFormat.POSITION_TEX,"DiffuseSampler", "DepthSampler", "OutSize", "ViewPort");

    public static ShaderHolder RREDE = new ShaderHolder( JCraft.id("rrede"), DefaultVertexFormat.NEW_ENTITY);

    //Post Processed
    public static final TimestopShaderPostProcessor ZA_WARUDO = new TimestopShaderPostProcessor();

    @SubscribeEvent
    public static void shaderRegistry(RegisterShadersEvent event) throws IOException {
        var resourceManager = event.getResourceProvider();

        registerShader(event, TEST.createInstance(resourceManager));
        registerShader(event, RREDE.createInstance(resourceManager));

        PostProcessHandler.addInstance(ZA_WARUDO);
    }

    public static void registerShader(RegisterShadersEvent event, JShader extendedShaderInstance) {
        event.registerShader(extendedShaderInstance, s -> {});
    }

}
