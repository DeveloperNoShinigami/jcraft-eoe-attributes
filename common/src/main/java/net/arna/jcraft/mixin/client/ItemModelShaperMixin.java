package net.arna.jcraft.mixin.client;

import net.arna.jcraft.client.rendering.ModelWithCustomRenderer;
import net.arna.jcraft.api.registry.JItemRegistry;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemModelShaper.class)
public class ItemModelShaperMixin {

    @Inject(method = "getItemModel(Lnet/minecraft/world/item/Item;)Lnet/minecraft/client/resources/model/BakedModel;", at = @At("RETURN"), cancellable = true)
    private void overrideStandDiscModel(Item item, CallbackInfoReturnable<BakedModel> cir) {
        // Ensure that stand discs are rendered using a custom renderer
        if (item == JItemRegistry.STAND_DISC.get()) {
            cir.setReturnValue(new ModelWithCustomRenderer(cir.getReturnValue()));
        }
    }
}
