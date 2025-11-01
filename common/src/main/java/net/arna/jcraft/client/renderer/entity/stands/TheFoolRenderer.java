package net.arna.jcraft.client.renderer.entity.stands;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import lombok.NonNull;
import net.arna.jcraft.api.registry.JStandTypeRegistry;
import net.arna.jcraft.client.model.entity.stand.TheFoolModel;
import net.arna.jcraft.common.entity.stand.TheFoolEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * The {@link StandEntityRenderer} for {@link TheFoolEntity}.
 */
@Environment(EnvType.CLIENT)
public class TheFoolRenderer extends StandEntityRenderer<TheFoolEntity> {

    public TheFoolRenderer(final @NonNull EntityRendererProvider.Context context) {
        super(context, JStandTypeRegistry.THE_FOOL.get(), 0.7854f, -0.349f, 30f);
    }

}
