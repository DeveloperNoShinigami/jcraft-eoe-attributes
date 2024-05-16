package net.arna.jcraft.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.arna.jcraft.client.util.JClientUtils;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(BlockEntityRenderDispatcher.class)
public class BlockEntityRenderDispatcherMixin {

    /**
     * @author Arna
     * @reason MixinExtras was causing the mod to consider Sodium a dependency, the change itself is minor enough.
     */
    @Overwrite
    private static <T extends BlockEntity> void setupAndRender(BlockEntityRenderer<T> renderer, T blockEntity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers) {
        Level world = blockEntity.getLevel();
        int i;
        if (world != null) {
            i = LevelRenderer.getLightColor(world, blockEntity.getBlockPos());
        } else {
            i = 15728880;
        }

        renderer.render(
                blockEntity,
                JClientUtils.getTicksIfInTSRange(blockEntity.getBlockPos()) > 0 ? 0 : tickDelta,
                matrices,
                vertexConsumers,
                i,
                OverlayTexture.NO_OVERLAY);
    }
}
