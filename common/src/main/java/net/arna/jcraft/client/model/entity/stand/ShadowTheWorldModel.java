package net.arna.jcraft.client.model.entity.stand;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.stand.ShadowTheWorldEntity;
import net.arna.jcraft.api.registry.JStandTypeRegistry;
import net.minecraft.resources.ResourceLocation;

import java.util.Locale;

/**
 * The {@link StandEntityModel} for {@link ShadowTheWorldEntity}.
 * @see net.arna.jcraft.client.renderer.entity.stands.ShadowTheWorldRenderer ShadowTheWorldRenderer
 */
public class ShadowTheWorldModel extends StandEntityModel<ShadowTheWorldEntity> {
    private static final ResourceLocation MODEL = JCraft.id("geo/" + JStandTypeRegistry.SHADOW_THE_WORLD.getId().getPath().toLowerCase(Locale.ROOT) + ".geo.json");

    public ShadowTheWorldModel() {
        super(JStandTypeRegistry.SHADOW_THE_WORLD.get(), -0.1745329251f, -0.1745329251f);
    }

    @Override
    public ResourceLocation getModelResource(final ShadowTheWorldEntity entity) {
        return MODEL;
    }
}
