package net.arna.jcraft.client.model.entity.stand;

import net.arna.jcraft.common.entity.stand.WhiteSnakeEntity;
import net.arna.jcraft.api.registry.JStandTypeRegistry;

/**
 * The {@link StandEntityModel} for {@link WhiteSnakeEntity}.
 * @see net.arna.jcraft.client.renderer.entity.stands.WhiteSnakeRenderer WhiteSnakeRenderer
 */
public class WhiteSnakeModel extends StandEntityModel<WhiteSnakeEntity> {
    public WhiteSnakeModel() {
        super(JStandTypeRegistry.WHITE_SNAKE.get(), -0.10f, -0.10f);
    }
}
