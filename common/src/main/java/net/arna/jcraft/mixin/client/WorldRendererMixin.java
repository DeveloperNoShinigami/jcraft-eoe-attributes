package net.arna.jcraft.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.arna.jcraft.client.rendering.skybox.SkyBoxManager;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(LevelRenderer.class)
public class WorldRendererMixin {

    @Unique private Entity entity;

    @ModifyArg(method = "renderEntity",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;render(Lnet/minecraft/world/entity/Entity;DDDFFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V")
    )
    private Entity jcraft$deltaTick(Entity entity) {
        this.entity = entity;
        return entity;
    }

    @ModifyArg(method = "renderEntity",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;render(Lnet/minecraft/world/entity/Entity;DDDFFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"),
            index = 5
    )
    private float jcraft$deltaTick(float yaw) {
        if (JComponentPlatformUtils.getTimeStopData(entity).isPresent()) {
            if (JComponentPlatformUtils.getTimeStopData(entity).get().getTicks() > 0) {
                return 0;
            } // Args 0 = ent, 5 = deltatick
        }

        return yaw;
    }

    @Inject(method = "renderSky", at = @At("HEAD"), cancellable = true)
    private void jcraft$renderSky(PoseStack matrices, Matrix4f matrix4f, float tickDelta, Camera camera, boolean bl, Runnable runnable, CallbackInfo ci) {
        SkyBoxManager skyboxManager = SkyBoxManager.getInstance();
        if (skyboxManager.isEnabled() && skyboxManager.getCurrentSkybox() != null) {
            runnable.run();
            skyboxManager.renderSkyBox(matrices, matrix4f, tickDelta, camera, bl);
            ci.cancel();
        }
    }
}
