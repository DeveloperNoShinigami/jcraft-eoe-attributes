package net.arna.jcraft.client.model.entity;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.DarbyOlderEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class DarbyOlderModel extends GeoModel<DarbyOlderEntity> {
    @Override
    public Identifier getModelResource(DarbyOlderEntity animatable) {
        return JCraft.id("geo/darby_older.geo.json");
    }

    @Override
    public Identifier getTextureResource(DarbyOlderEntity animatable) {
        return JCraft.id("textures/entity/darby_older.png");
    }

    @Override
    public Identifier getAnimationResource(DarbyOlderEntity animatable) {
        // TODO Arna
        return JCraft.id("darby_older");
    }
}
