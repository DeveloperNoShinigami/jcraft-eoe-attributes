package net.arna.jcraft.client.renderer.entity.stands;

import lombok.NonNull;
import net.arna.jcraft.api.registry.JStandTypeRegistry;
import net.arna.jcraft.common.entity.stand.WhiteSnakeEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * The {@link StandEntityRenderer} for {@link WhiteSnakeEntity}.
 */
@Environment(EnvType.CLIENT)
public class WhiteSnakeRenderer extends StandEntityRenderer<WhiteSnakeEntity> {

    public WhiteSnakeRenderer(final @NonNull EntityRendererProvider.Context context) {
        super(context, b -> b
                .addRenderLayer(new HandItemsRenderLayer<>()),
                JStandTypeRegistry.WHITE_SNAKE.get(), -0.10f, -0.10f);
    }
}
