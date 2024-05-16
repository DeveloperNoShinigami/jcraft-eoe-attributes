package net.arna.jcraft.mixin.client.gravity;

import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.BiomeAmbientSoundsHandler;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

//method_26271 refers to a lambda which is why this class may cause mixin warnings/errors
@Mixin(BiomeAmbientSoundsHandler.class)
public abstract class BiomeEffectSoundPlayerMixin {
    @Redirect(
            method = "method_26271",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;getEyeY()D"
            )
    )
    private double redirect_method_26271_getEyeY_0(LocalPlayer clientPlayerEntity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(clientPlayerEntity);
        if (gravityDirection == Direction.DOWN) {
            return clientPlayerEntity.getEyeY();
        }

        return clientPlayerEntity.getEyePosition().y;
    }

    @Redirect(
            method = "method_26271",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;getX()D"
            )
    )
    private double redirect_method_26271_getX_0(LocalPlayer clientPlayerEntity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(clientPlayerEntity);
        if (gravityDirection == Direction.DOWN) {
            return clientPlayerEntity.getX();
        }

        return clientPlayerEntity.getEyePosition().x;
    }

    @Redirect(
            method = "method_26271",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;getZ()D"
            )
    )
    private double redirect_method_26271_getZ_0(LocalPlayer clientPlayerEntity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(clientPlayerEntity);
        if (gravityDirection == Direction.DOWN) {
            return clientPlayerEntity.getZ();
        }

        return clientPlayerEntity.getEyePosition().z;
    }

    @Redirect(
            method = "method_26271",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;getEyeY()D",
                    ordinal = 1
            )
    )
    private double redirect_method_26271_getEyeY_1(LocalPlayer clientPlayerEntity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(clientPlayerEntity);
        if (gravityDirection == Direction.DOWN) {
            return clientPlayerEntity.getEyeY();
        }

        return clientPlayerEntity.getEyePosition().y;
    }

    @Redirect(
            method = "method_26271",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;getX()D",
                    ordinal = 1
            )
    )
    private double redirect_method_26271_getX_1(LocalPlayer clientPlayerEntity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(clientPlayerEntity);
        if (gravityDirection == Direction.DOWN) {
            return clientPlayerEntity.getX();
        }

        return clientPlayerEntity.getEyePosition().x;
    }

    @Redirect(
            method = "method_26271",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;getZ()D",
                    ordinal = 1
            )
    )
    private double redirect_method_26271_getZ_1(LocalPlayer clientPlayerEntity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(clientPlayerEntity);
        if (gravityDirection == Direction.DOWN) {
            return clientPlayerEntity.getZ();
        }

        return clientPlayerEntity.getEyePosition().z;
    }

    @Redirect(
            method = "method_26271",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;getEyeY()D",
                    ordinal = 2
            )
    )
    private double redirect_method_26271_getEyeY_2(LocalPlayer clientPlayerEntity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(clientPlayerEntity);
        if (gravityDirection == Direction.DOWN) {
            return clientPlayerEntity.getEyeY();
        }

        return clientPlayerEntity.getEyePosition().y;
    }

    @Redirect(
            method = "method_26271",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;getX()D",
                    ordinal = 2
            )
    )
    private double redirect_method_26271_getX_2(LocalPlayer clientPlayerEntity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(clientPlayerEntity);
        if (gravityDirection == Direction.DOWN) {
            return clientPlayerEntity.getX();
        }

        return clientPlayerEntity.getEyePosition().x;
    }

    @Redirect(
            method = "method_26271",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;getZ()D",
                    ordinal = 2
            )
    )
    private double redirect_method_26271_getZ_2(LocalPlayer clientPlayerEntity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(clientPlayerEntity);
        if (gravityDirection == Direction.DOWN) {
            return clientPlayerEntity.getZ();
        }

        return clientPlayerEntity.getEyePosition().z;
    }
}
