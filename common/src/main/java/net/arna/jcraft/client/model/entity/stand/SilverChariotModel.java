package net.arna.jcraft.client.model.entity.stand;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.stand.SilverChariotEntity;
import net.arna.jcraft.common.entity.stand.StandType;
import net.minecraft.resources.ResourceLocation;

public class SilverChariotModel extends StandEntityModel<SilverChariotEntity> {
    private static final ResourceLocation NO_ARMOR_TEXTURE = JCraft.id("textures/entity/stands/silver_chariot/no_armor.png");
    private static final ResourceLocation POSSESSED_TEXTURE = JCraft.id("textures/entity/stands/silver_chariot/possessed.png");

    public SilverChariotModel() {
        super(StandType.SILVER_CHARIOT, 0, -0.2f);
    }

    @Override
    public ResourceLocation getTextureResource(final SilverChariotEntity entity) {
        return switch (entity.getMode()) {
            case ARMORLESS -> NO_ARMOR_TEXTURE;
            case POSSESSED -> POSSESSED_TEXTURE;
            default -> super.getTextureResource(entity);
        };
    }
}
