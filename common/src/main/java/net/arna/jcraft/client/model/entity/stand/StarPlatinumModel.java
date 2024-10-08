package net.arna.jcraft.client.model.entity.stand;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.stand.AbstractStarPlatinumEntity;
import net.arna.jcraft.common.entity.stand.StandType;
import net.minecraft.resources.ResourceLocation;

/**
 * The {@link StandEntityModel} for {@link net.arna.jcraft.common.entity.stand.StarPlatinumEntity StarPlatinumEntity}
 * and {@link net.arna.jcraft.common.entity.stand.SPTWEntity SPTWEntity}.
 * @see net.arna.jcraft.client.renderer.entity.stands.StarPlatinumRenderer StarPlatinumRenderer
 * @see net.arna.jcraft.client.renderer.entity.stands.SPTWRenderer SPTWRenderer
 */
public class StarPlatinumModel extends StandEntityModel<AbstractStarPlatinumEntity<?, ?>> {
    private static final ResourceLocation MODEL = JCraft.id("geo/star_platinum.geo.json");

    public StarPlatinumModel(final boolean theWorld) {
        super(theWorld ? StandType.STAR_PLATINUM_THE_WORLD : StandType.STAR_PLATINUM);
    }

    @Override
    public ResourceLocation getModelResource(final AbstractStarPlatinumEntity<?, ?> object) {
        return MODEL;
    }
}
