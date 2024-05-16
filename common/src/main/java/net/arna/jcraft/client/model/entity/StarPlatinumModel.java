package net.arna.jcraft.client.model.entity;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.stand.AbstractStarPlatinumEntity;
import net.arna.jcraft.common.entity.stand.StandType;
import net.minecraft.resources.ResourceLocation;

public class StarPlatinumModel extends StandEntityModel<AbstractStarPlatinumEntity<?, ?>> {
    //EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
    private static final ResourceLocation MODEL = JCraft.id("geo/star_platinum.geo.json");

    public StarPlatinumModel(boolean theWorld) {
        super(theWorld ? StandType.STAR_PLATINUM_THE_WORLD : StandType.STAR_PLATINUM);
    }

    @Override
    public ResourceLocation getModelResource(AbstractStarPlatinumEntity<?, ?> object) {
        return MODEL;
    }
}
