package net.arna.jcraft.fabric.mixin.client;


import com.mojang.datafixers.util.Pair;
import net.arna.jcraft.fabric.client.JShaderRegistry;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.resource.ResourceFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(method = "loadPrograms", at = @At(value = "INVOKE_ASSIGN", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
    private void jcraft$registerShaders(ResourceFactory manager, CallbackInfo ci, List<Pair<ShaderProgram, Consumer<ShaderProgram>>> list, List<Pair<ShaderProgram, Consumer<ShaderProgram>>> list2) throws IOException {
        JShaderRegistry.init(manager);
        list2.addAll(JShaderRegistry.shaderList);
    }
}