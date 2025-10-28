package net.arna.jcraft.client.renderer.entity.stands;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import lombok.NonNull;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.api.registry.JStandTypeRegistry;
import net.arna.jcraft.common.entity.stand.MandomEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public class MandomRenderer extends StandEntityRenderer<MandomEntity> {

    public MandomRenderer(final @NonNull EntityRendererProvider.Context context) {
        super(context, b -> b.setRenderType(renderType(RenderType::entityTranslucentCull)), JStandTypeRegistry.MANDOM.get());
    }

}