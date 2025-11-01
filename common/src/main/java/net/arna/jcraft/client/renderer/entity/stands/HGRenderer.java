package net.arna.jcraft.client.renderer.entity.stands;

import lombok.NonNull;
import net.arna.jcraft.api.registry.JStandTypeRegistry;
import net.arna.jcraft.common.entity.stand.HGEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * The {@link StandEntityRenderer} for {@link HGEntity}.
 */
@Environment(EnvType.CLIENT)
public class HGRenderer extends StandEntityRenderer<HGEntity> {

    public HGRenderer(final @NonNull EntityRendererProvider.Context context) {
        super(context, JStandTypeRegistry.HIEROPHANT_GREEN.get(), 0f, -0.2f);
    }

}