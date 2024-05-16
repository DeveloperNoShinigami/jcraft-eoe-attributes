package net.arna.jcraft.mixin.client.gravity;


import com.mojang.blaze3d.vertex.PoseStack;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.gravity.util.RotationUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityRenderer.class)
public abstract class EntityRenderMixin {
    @Redirect(
            method = "renderNameTag",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;cameraOrientation()Lorg/joml/Quaternionf;"
            )
    )
    private Quaternionf redirect_renderLabelIfPresent_getRotation_0(EntityRenderDispatcher entityRenderDispatcher, Entity entity, Component text, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if (gravityDirection == Direction.DOWN) {
            return entityRenderDispatcher.cameraOrientation();
        }

        Quaternionf quaternion = new Quaternionf(RotationUtil.getCameraRotationQuaternion(gravityDirection));
        quaternion.conjugate();
        quaternion.mul(new Quaternionf(entityRenderDispatcher.cameraOrientation()));
        return quaternion;
    }
}
