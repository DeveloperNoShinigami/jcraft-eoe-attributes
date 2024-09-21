package net.arna.jcraft.client.model.entity.stand;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.stand.ShadowTheWorldEntity;
import net.arna.jcraft.common.entity.stand.StandType;
import net.minecraft.resources.ResourceLocation;

import java.util.Locale;

public class ShadowTheWorldModel extends StandEntityModel<ShadowTheWorldEntity> {
    private static final ResourceLocation MODEL = JCraft.id("geo/" + StandType.THE_WORLD.name().toLowerCase(Locale.ROOT) + ".geo.json");

    public ShadowTheWorldModel() {
        super(StandType.SHADOW_THE_WORLD, -0.1745329251f, -0.1745329251f);
    }

    @Override
    public ResourceLocation getModelResource(ShadowTheWorldEntity entity) {
        return MODEL;
    }
}
