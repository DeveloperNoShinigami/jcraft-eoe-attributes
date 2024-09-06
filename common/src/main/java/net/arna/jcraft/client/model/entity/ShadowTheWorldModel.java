package net.arna.jcraft.client.model.entity;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.stand.ShadowTheWorldEntity;
import net.arna.jcraft.common.entity.stand.StandType;
import net.minecraft.resources.ResourceLocation;

public class ShadowTheWorldModel extends StandEntityModel<ShadowTheWorldEntity> {
    private final String theWorldTypeName;
    public ShadowTheWorldModel()
    {
        super(StandType.SHADOW_THE_WORLD, -0.1745329251f, -0.1745329251f);
        theWorldTypeName = StandType.THE_WORLD.name().toLowerCase();
    }

    @Override
    public ResourceLocation getModelResource(ShadowTheWorldEntity entity) {
        return JCraft.id("geo/" + theWorldTypeName + ".geo.json");
    }
}
