package net.arna.jcraft.mixin;

import net.arna.jcraft.api.component.living.CommonVampireComponent;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FoodData.class)
public class HungerManagerMixin {
    @Unique
    private CommonVampireComponent vampireComponent;
    @Unique
    private boolean isVampire;

    @Inject(method = "getFoodLevel", at = @At("HEAD"), cancellable = true)
    void jcraft$getBloodLevel(CallbackInfoReturnable<Integer> cir) {
        if (this.isVampire) {
            cir.setReturnValue((int) Math.floor(vampireComponent.getBlood()));
        }
    }

    @Inject(method = "getSaturationLevel", at = @At("HEAD"), cancellable = true)
    void jcraft$getSaturationLevel(CallbackInfoReturnable<Float> cir) {
        if (this.isVampire) {
            cir.setReturnValue(0f);
        }
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    void jcraft$updateVampirism(Player player, CallbackInfo ci) {
        this.vampireComponent = JComponentPlatformUtils.getVampirism(player);
        this.isVampire = vampireComponent.isVampire();

        if (isVampire) {
            ci.cancel();
        }
    }
}
