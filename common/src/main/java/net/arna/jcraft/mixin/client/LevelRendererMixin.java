package net.arna.jcraft.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.arna.jcraft.client.rendering.api.callbacks.PostWorldRenderCallback;
import net.arna.jcraft.mixin_logic.StillDepthHolder;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import org.joml.Matrix4f;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Inject(
            method = "renderLevel",
            slice = @Slice(from = @At(value = "FIELD:LAST", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/renderer/LevelRenderer;transparencyChain:Lnet/minecraft/client/renderer/PostChain;")),
            at = {
                    // Only one of these is run, depending on the user's settings.
                    @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/PostChain;process(F)V"),
                    @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;depthMask(Z)V", ordinal = 1, shift = At.Shift.AFTER)
            }
    )
    private void hookPostWorldRender(PoseStack matrices, float tickDelta, long nanoTime, boolean renderBlockOutline,
                                     Camera camera, GameRenderer renderer, LightTexture lmTexManager, Matrix4f matrix4f, CallbackInfo ci) {
        ((StillDepthHolder) Minecraft.getInstance().getMainRenderTarget()).jcraft$freezeDepth();
        PostWorldRenderCallback.EVENT.invoker().onWorldRendered(matrices, camera, tickDelta, nanoTime);
    }
}
