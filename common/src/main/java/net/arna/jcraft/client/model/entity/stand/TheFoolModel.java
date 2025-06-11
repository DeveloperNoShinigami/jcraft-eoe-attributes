package net.arna.jcraft.client.model.entity.stand;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.stand.TheFoolEntity;
import net.arna.jcraft.api.registry.JStandTypeRegistry;
import net.minecraft.resources.ResourceLocation;

/**
 * The {@link StandEntityModel} for {@link TheFoolEntity}.
 * @see net.arna.jcraft.client.renderer.entity.stands.TheFoolRenderer TheFoolRenderer
 */
public class TheFoolModel extends StandEntityModel<TheFoolEntity> {
    private static final ResourceLocation SAND_TEXTURE = JCraft.id("textures/entity/stands/the_fool/sand.png");

    public TheFoolModel() {
        super(JStandTypeRegistry.THE_FOOL.get(), 0.7854f, -0.349f, 30f);
    }

    @Override
    public ResourceLocation getTextureResource(final TheFoolEntity entity) {
        return entity.isSand() ? SAND_TEXTURE : super.getTextureResource(entity);
    }
}
