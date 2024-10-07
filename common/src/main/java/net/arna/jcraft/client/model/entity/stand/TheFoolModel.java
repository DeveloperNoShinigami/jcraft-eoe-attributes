package net.arna.jcraft.client.model.entity.stand;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.stand.StandType;
import net.arna.jcraft.common.entity.stand.TheFoolEntity;
import net.minecraft.resources.ResourceLocation;

public class TheFoolModel extends StandEntityModel<TheFoolEntity> {
    private static final ResourceLocation SAND_TEXTURE = JCraft.id("textures/entity/stands/the_fool/sand.png");
    //EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);

    public TheFoolModel() {
        super(StandType.THE_FOOL, 0.7854f, -0.349f, 30f);
    }

    @Override
    public ResourceLocation getTextureResource(final TheFoolEntity entity) {
        return entity.isSand() ? SAND_TEXTURE : super.getTextureResource(entity);
    }
}
