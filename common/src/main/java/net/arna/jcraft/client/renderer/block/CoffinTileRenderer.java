package net.arna.jcraft.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.azure.azurelib.renderer.GeoBlockRenderer;
import net.arna.jcraft.client.model.tile.CoffinModel;
import net.arna.jcraft.common.block.tile.CoffinTileEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.properties.BedPart;

public class CoffinTileRenderer extends GeoBlockRenderer<CoffinTileEntity> {
    public CoffinTileRenderer(final BlockEntityRendererProvider.Context context) {
        super(new CoffinModel());
    }

    @Override
    public void render(final CoffinTileEntity animatable, final float partialTick, final PoseStack poseStack,
                       final MultiBufferSource bufferSource, final int packedLight, final int packedOverlay) {
        if (BedPart.HEAD == animatable.getBlockState().getValue(BedBlock.PART)) {
            return;
        }

        this.animatable = animatable;

        defaultRender(poseStack, this.animatable, bufferSource, null, null, 0, partialTick, packedLight);
    }
}
