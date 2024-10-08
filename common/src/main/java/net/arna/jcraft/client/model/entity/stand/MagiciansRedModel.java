package net.arna.jcraft.client.model.entity.stand;

import net.arna.jcraft.common.entity.stand.MagiciansRedEntity;
import net.arna.jcraft.common.entity.stand.StandType;

/**
 * The {@link StandEntityModel} for {@link MagiciansRedEntity}.
 * @see net.arna.jcraft.client.renderer.entity.stands.MagiciansRedRenderer MagiciansRedRenderer
 */
public class MagiciansRedModel extends StandEntityModel<MagiciansRedEntity> {
    public MagiciansRedModel() {
        super(StandType.MAGICIANS_RED, -0.10f, -0.05f);
    }
}
