package net.arna.jcraft.client.renderer.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.azure.azurelib.model.AzBone;
import mod.azure.azurelib.render.AzRendererPipelineContext;
import mod.azure.azurelib.render.layer.AzRenderLayer;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.stand.SilverChariotEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static net.arna.jcraft.common.entity.projectile.RapierProjectile.ARMOR_OFF_TEXTURE;
import static net.arna.jcraft.common.entity.projectile.RapierProjectile.POSSESSED_TEXTURE;

@Environment(EnvType.CLIENT)
public class SCRapierLayer extends AbstractRenderLayer<SilverChariotEntity> {
    private static List<ResourceLocation> skins;

    /*public SCRapierLayer(final GeoRenderer<SilverChariotEntity> entityRendererIn) {
        super(entityRendererIn);

        skins = IntStream.rangeClosed(0, 4)
                .mapToObj(i -> JCraft.id("textures/entity/stands/silver_chariot/rapier_" + (i == 0 ? "default" : "skin" + i) + ".png"))
                .toList();
    }

    @Override
    public void render(final PoseStack poseStack, final SilverChariotEntity animatable, final BakedGeoModel bakedModel, final RenderType renderType,
                       final MultiBufferSource bufferIn, final VertexConsumer buffer, final float partialTicks, final int packedLight, final int packedOverlay) {
        if (animatable.hasRapier()) {
            SilverChariotEntity.Mode mode = animatable.getMode();

            final RenderType cameo = RenderType.armorCutoutNoCull(
                    mode == SilverChariotEntity.Mode.POSSESSED ? POSSESSED_TEXTURE :
                            mode == SilverChariotEntity.Mode.ARMORLESS ? ARMOR_OFF_TEXTURE :
                                    skins.get(animatable.getSkin())
            );

            getRenderer().reRender(bakedModel, poseStack, bufferIn, animatable, cameo,
                    bufferIn.getBuffer(cameo), partialTicks, packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1f);
        }
    }*/
}
