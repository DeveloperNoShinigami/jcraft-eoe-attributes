package net.arna.jcraft.client.model.entity;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.AyaTsujiEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class AyaTsujiModel extends GeoModel<AyaTsujiEntity> {
    @Override
    public Identifier getModelResource(AyaTsujiEntity animatable) {
        return JCraft.id("geo/aya_tsuji.geo.json");
    }

    @Override
    public Identifier getTextureResource(AyaTsujiEntity animatable) {
        return JCraft.id("textures/entity/aya_tsuji.png");
    }

    @Override
    public Identifier getAnimationResource(AyaTsujiEntity animatable) {
        // TODO Arna
        return JCraft.id("aya_tsuji");
    }
}
