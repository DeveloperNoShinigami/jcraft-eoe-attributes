package net.arna.jcraft.client.renderer.block;

import mod.azure.azurelib.renderer.GeoBlockRenderer;
import net.arna.jcraft.client.model.tile.CoffinModel;
import net.arna.jcraft.common.block.tile.CoffinTileEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class CoffinTileRenderer extends GeoBlockRenderer<CoffinTileEntity> {
    public CoffinTileRenderer(BlockEntityRendererProvider.Context context) {
        super(new CoffinModel());
    }
}
