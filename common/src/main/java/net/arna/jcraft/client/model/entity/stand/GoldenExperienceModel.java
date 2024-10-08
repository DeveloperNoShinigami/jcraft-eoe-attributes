package net.arna.jcraft.client.model.entity.stand;

import net.arna.jcraft.common.entity.stand.GoldExperienceEntity;
import net.arna.jcraft.common.entity.stand.StandType;

/**
 * The {@link StandEntityModel} for {@link GoldExperienceEntity}.
 * @see net.arna.jcraft.client.renderer.entity.stands.GoldExperienceRenderer GoldExperienceRenderer
 */
public class GoldenExperienceModel extends StandEntityModel<GoldExperienceEntity> {
    public GoldenExperienceModel() {
        super(StandType.GOLD_EXPERIENCE, 0, -0.1f);
    }
}
