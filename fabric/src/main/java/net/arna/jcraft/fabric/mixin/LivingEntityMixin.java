package net.arna.jcraft.fabric.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.arna.jcraft.registry.JStatusRegistry;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    /**
     * Forge equivalent: jcraft.forge.events.RuntimeEvents.stopBreathing()
     */
    @WrapOperation(
            method = "baseTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;setAirSupply(I)V",
                    ordinal = 2
            )
    )
    public void jcraft$stopBreathing(LivingEntity instance, int airSupply, Operation<Void> original) {
        if (!instance.hasEffect(JStatusRegistry.HYPOXIA.get())) {
            original.call(instance, airSupply);
        }
    }
}
