package net.arna.jcraft.client.model.entity;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.AyaTsujiEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class AyaTsujiModel extends GeoModel<AyaTsujiEntity> {
    @Override
    public ResourceLocation getModelResource(AyaTsujiEntity animatable) {
        return JCraft.id("geo/aya_tsuji.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(AyaTsujiEntity animatable) {
        return JCraft.id("textures/entity/aya_tsuji.png");
    }

    @Override
    public ResourceLocation getAnimationResource(AyaTsujiEntity animatable) {
        // TODO Arna
        return JCraft.id("aya_tsuji");
    }
}
