package net.arna.jcraft.client.model.tile;

import mod.azure.azurelib.model.GeoModel;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.block.tile.CoffinTileEntity;
import net.minecraft.resources.ResourceLocation;


public class CoffinModel extends GeoModel<CoffinTileEntity> {
    @Override
    public ResourceLocation getAnimationResource(CoffinTileEntity entity) {
        return JCraft.id("animations/coffin.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(CoffinTileEntity animatable) {
        return JCraft.id("geo/coffin.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(CoffinTileEntity entity) {
        return JCraft.id("textures/block/coffin.png");
    }
}