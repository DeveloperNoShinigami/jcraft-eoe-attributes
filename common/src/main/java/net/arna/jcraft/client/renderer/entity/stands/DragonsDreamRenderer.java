package net.arna.jcraft.client.renderer.entity.stands;

import lombok.NonNull;
import net.arna.jcraft.api.registry.JStandTypeRegistry;
import net.arna.jcraft.common.entity.stand.DragonsDreamEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * The {@link StandEntityRenderer} for {@link DragonsDreamEntity}.
 */
@Environment(EnvType.CLIENT)
public class DragonsDreamRenderer extends StandEntityRenderer<DragonsDreamEntity> {

    public DragonsDreamRenderer(final @NonNull EntityRendererProvider.Context renderManager) {
        super(renderManager, JStandTypeRegistry.DRAGONS_DREAM.get(), 0.0f, 1.5707f);
    }

}
