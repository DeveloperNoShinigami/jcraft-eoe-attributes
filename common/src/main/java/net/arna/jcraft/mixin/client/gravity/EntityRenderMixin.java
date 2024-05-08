package net.arna.jcraft.mixin.client.gravity;


import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.gravity.util.RotationUtil;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityRenderer.class)
public abstract class EntityRenderMixin {
    @Redirect(
            method = "renderLabelIfPresent",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;getRotation()Lorg/joml/Quaternionf;"
            )
    )
    private Quaternionf redirect_renderLabelIfPresent_getRotation_0(EntityRenderDispatcher entityRenderDispatcher, Entity entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        if (gravityDirection == Direction.DOWN) {
            return entityRenderDispatcher.getRotation();
        }

        Quaternionf quaternion = new Quaternionf(RotationUtil.getCameraRotationQuaternion(gravityDirection));
        quaternion.conjugate();
        quaternion.mul(new Quaternionf(entityRenderDispatcher.getRotation()));
        return quaternion;
    }
}
