package net.arna.jcraft.client.renderer.entity.stands;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import lombok.NonNull;
import net.arna.jcraft.api.registry.JStandTypeRegistry;
import net.arna.jcraft.client.model.entity.stand.KillerQueenModel;
import net.arna.jcraft.common.entity.stand.KillerQueenEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * The {@link StandEntityRenderer} for {@link KillerQueenEntity}.
 */
@Environment(EnvType.CLIENT)
public class KillerQueenRenderer extends StandEntityRenderer<KillerQueenEntity> {

    public KillerQueenRenderer(final @NonNull EntityRendererProvider.Context context) {
        super(context, JStandTypeRegistry.KILLER_QUEEN.get());
    }

}
