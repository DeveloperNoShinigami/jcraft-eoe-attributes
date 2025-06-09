package net.arna.jcraft.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.arna.jcraft.client.renderer.item.StandDiscItemRenderer;
import net.arna.jcraft.registry.JItemRegistry;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntityWithoutLevelRenderer.class)
public class BlockEntityWithoutLevelRendererMixin {

    @Inject(method = "renderByItem", at = @At("HEAD"), cancellable = true)
    private void overrideRenderForStandDiscs(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack,
                                             MultiBufferSource buffer, int packedLight, int packedOverlay, CallbackInfo ci) {
        if (stack.is(JItemRegistry.STAND_DISC.get())) {
            StandDiscItemRenderer.render(stack, displayContext, poseStack, buffer, packedLight, packedOverlay);
            ci.cancel();
        }
    }
}
