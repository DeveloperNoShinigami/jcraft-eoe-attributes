package net.arna.jcraft.client.renderer.entity.layer;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.stand.SilverChariotEntity;
import net.arna.jcraft.common.entity.stand.StandType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import java.util.List;
import java.util.stream.IntStream;

import static net.arna.jcraft.common.entity.projectile.RapierProjectile.ARMOR_OFF_TEXTURE;
import static net.arna.jcraft.common.entity.projectile.RapierProjectile.POSSESSED_TEXTURE;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

public class SCRapierLayer extends GeoRenderLayer<SilverChariotEntity> {
    private static final ResourceLocation MODEL = JCraft.id("geo/silver_chariot.geo.json");
    private static List<ResourceLocation> skins;

    public SCRapierLayer(GeoRenderer<SilverChariotEntity> entityRendererIn) {
        super(entityRendererIn);

        skins = IntStream.rangeClosed(0, StandType.SILVER_CHARIOT.getSkinCount())
                .mapToObj(i -> JCraft.id("textures/entity/stands/silver_chariot/rapier_" + (i == 0 ? "default" : "skin" + i) + ".png"))
                .toList();
    }

    @Override
    public void render(PoseStack matrixStackIn, SilverChariotEntity animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferIn, VertexConsumer buffer, float partialTicks, int packedLightIn, int packedOverlay) {
        if (animatable.hasRapier()) {
            SilverChariotEntity.Mode mode = animatable.getMode();

            RenderType cameo = RenderType.armorCutoutNoCull(
                    mode == SilverChariotEntity.Mode.POSSESSED ? POSSESSED_TEXTURE :
                            mode == SilverChariotEntity.Mode.ARMORLESS ? ARMOR_OFF_TEXTURE :
                                    skins.get(animatable.getSkin())
            );

            matrixStackIn.pushPose();
            getRenderer().reRender(getDefaultBakedModel(animatable), matrixStackIn, bufferIn, animatable, cameo,
                    bufferIn.getBuffer(cameo), partialTicks, packedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1f);
            matrixStackIn.popPose();
        }
    }
}
