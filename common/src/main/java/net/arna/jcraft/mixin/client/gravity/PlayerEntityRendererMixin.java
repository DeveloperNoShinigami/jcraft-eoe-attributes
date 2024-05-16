package net.arna.jcraft.mixin.client.gravity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.gravity.util.RotationUtil;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PlayerRenderer.class)
public abstract class PlayerEntityRendererMixin {
    @ModifyVariable(
            method = "setupRotations(Lnet/minecraft/client/player/AbstractClientPlayer;Lcom/mojang/blaze3d/vertex/PoseStack;FFF)V",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/client/player/AbstractClientPlayer;getViewVector(F)Lnet/minecraft/world/phys/Vec3;"
            )
    )
    private Vec3 modify_setupTransforms_Vec3d_0(Vec3 vec3d, AbstractClientPlayer abstractClientPlayerEntity, PoseStack matrixStack, float f, float g, float h) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(abstractClientPlayerEntity);
        if (gravityDirection == Direction.DOWN) {
            return vec3d;
        }

        return RotationUtil.vecWorldToPlayer(vec3d, gravityDirection);
    }
}
