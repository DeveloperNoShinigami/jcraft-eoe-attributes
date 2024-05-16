package net.arna.jcraft.client.renderer.entity;

import mod.azure.azurelib.renderer.GeoEntityRenderer;
import net.arna.jcraft.client.model.entity.GETreeModel;
import net.arna.jcraft.common.entity.projectile.GETreeEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;


public class GETreeRenderer extends GeoEntityRenderer<GETreeEntity> {
    public GETreeRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new GETreeModel());
        shadowRadius = 2.5f;
    }
}
