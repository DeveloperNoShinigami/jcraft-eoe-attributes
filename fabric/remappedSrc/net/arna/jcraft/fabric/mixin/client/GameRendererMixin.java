package net.arna.jcraft.fabric.mixin.client;


import com.mojang.datafixers.util.Pair;
import net.arna.jcraft.fabric.client.JShaderRegistry;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceProvider;
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
    private void jcraft$registerShaders(ResourceProvider manager, CallbackInfo ci, List<Pair<ShaderInstance, Consumer<ShaderInstance>>> list, List<Pair<ShaderInstance, Consumer<ShaderInstance>>> list2) throws IOException {
        JShaderRegistry.init(manager);
        list2.addAll(JShaderRegistry.shaderList);
    }
}