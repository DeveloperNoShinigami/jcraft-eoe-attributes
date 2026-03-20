package com.bluelotuscoding.fabric.mixin;

import dev.architectury.registry.registries.RegistrySupplier;
import net.arna.jcraft.api.registry.JItemRegistry;
import net.arna.jcraft.fabric.client.JCraftFabricClient;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(value = JCraftFabricClient.class, remap = false)
public abstract class JCraftFabricClientMixin {
    /**
     * @author bluelotuscoding
     * @reason Bypassing BigItemRenderer initialization to avoid AbstractMethodError in 1.20.1 Dev Environment.
     * We redirect the access to JItemRegistry.DEBUG_WAND to always return null during onInitializeClient,
     * which effectively skips the problematic registration block.
     */
    @Redirect(
        method = "onInitializeClient",
        at = @At(value = "FIELD", target = "Lnet/arna/jcraft/api/registry/JItemRegistry;DEBUG_WAND:Ldev/architectury/registry/registries/RegistrySupplier;")
    )
    private RegistrySupplier<Item> jcraft_attributes$skipDebugWandRegistration() {
        return null;
    }
}
