package net.arna.jcraft.client.renderer.entity.stands;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.arna.jcraft.api.registry.JStandTypeRegistry;
import net.arna.jcraft.client.model.entity.stand.KQBTDModel;
import net.arna.jcraft.client.renderer.entity.layer.KQBTDEyesLayer;
import net.arna.jcraft.common.entity.stand.KQBTDEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * The {@link StandEntityRenderer} for {@link KQBTDEntity}.
 * @see KQBTDModel
 */
public class KQBTDRenderer extends StandEntityRenderer<KQBTDEntity> {

    public KQBTDRenderer(final EntityRendererProvider.Context context) {
        super(context, b -> b.addRenderLayer(new KQBTDEyesLayer()), JStandTypeRegistry.KILLER_QUEEN_BITES_THE_DUST.get());
    }

}
