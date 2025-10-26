package net.arna.jcraft.client.renderer.entity.stands;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import lombok.NonNull;
import net.arna.jcraft.api.registry.JStandTypeRegistry;
import net.arna.jcraft.client.model.entity.stand.DragonsDreamModel;
import net.arna.jcraft.common.entity.stand.DragonsDreamEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * The {@link StandEntityRenderer} for {@link DragonsDreamEntity}.
 * @see DragonsDreamModel
 */
@Environment(EnvType.CLIENT)
public class DragonsDreamRenderer extends StandEntityRenderer<DragonsDreamEntity> {

    public DragonsDreamRenderer(final @NonNull EntityRendererProvider.Context renderManager) {
        super(renderManager, JStandTypeRegistry.DRAGONS_DREAM.get());
    }

}
