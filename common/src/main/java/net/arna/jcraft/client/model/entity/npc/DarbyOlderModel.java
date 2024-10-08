package net.arna.jcraft.client.model.entity.npc;

import mod.azure.azurelib.model.GeoModel;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.npc.AyaTsujiEntity;
import net.arna.jcraft.common.entity.npc.DarbyOlderEntity;
import net.minecraft.resources.ResourceLocation;

/**
 * The {@link GeoModel} for {@link DarbyOlderEntity}.
 * @see net.arna.jcraft.client.renderer.entity.npc.DarbyOlderRenderer DarbyOlderRenderer
 */
public class DarbyOlderModel extends GeoModel<DarbyOlderEntity> {
    @Override
    public ResourceLocation getModelResource(final DarbyOlderEntity animatable) {
        return JCraft.id("geo/darby_older.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(final DarbyOlderEntity animatable) {
        return JCraft.id("textures/entity/darby_older.png");
    }

    @Override
    public ResourceLocation getAnimationResource(final DarbyOlderEntity animatable) {
        return JCraft.id("animations/darby_older.animation.json");
    }
}
