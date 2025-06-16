package net.arna.jcraft.mixin.client;

public class WorldRendererMixin {

    /* TODO: this
    @ModifyVariable(
            method = "renderSnowAndRain(Lnet/minecraft/client/renderer/LightTexture;FDDD)V",
            at = @At("STORE"),
            ordinal = 6
    )
    private float jcraft$stopRain(float original) {
        if (JClientUtils.isInTSRange(minecraft.player.position())) {
            return 0f;
        }
        return original;
    }

    @ModifyVariable(
            method = "renderClouds(Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Matrix4f;FDDD)V",
            at = @At(value = "HEAD"),
            ordinal = 0,
            argsOnly = true
    )
    private float jcraft$stopClouds(float original) {
        if (JClientUtils.isInTSRange(minecraft.player.position())) {
            return 0f;
        }
        return original;
    }
     */
}
