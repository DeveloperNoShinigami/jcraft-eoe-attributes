package net.arna.jcraft.client.model.entity.stand;

import net.arna.jcraft.common.entity.stand.StandType;
import net.arna.jcraft.common.entity.stand.WhiteSnakeEntity;

/**
 * The {@link StandEntityModel} for {@link WhiteSnakeEntity}.
 * @see net.arna.jcraft.client.renderer.entity.stands.WhiteSnakeRenderer WhiteSnakeRenderer
 */
public class WhiteSnakeModel extends StandEntityModel<WhiteSnakeEntity> {
    public WhiteSnakeModel() {
        super(StandType.WHITE_SNAKE, -0.10f, -0.10f);
    }
}
