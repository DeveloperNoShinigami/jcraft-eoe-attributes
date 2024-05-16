package net.arna.jcraft.client.renderer.entity.layer;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.stand.TheWorldOverHeavenEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Map;

public class TWOHEyesLayer extends GeoRenderLayer<TheWorldOverHeavenEntity> {
    private static final ResourceLocation LAYER = new ResourceLocation(JCraft.MOD_ID, "textures/entity/stands/the_world_over_heaven/eyes.png");
    private static final ResourceLocation MODEL = new ResourceLocation(JCraft.MOD_ID, "geo/the_world_over_heaven.geo.json");
    private static final Map<Integer, Vector3f> overwriteColors =
            Map.ofEntries(
                    Map.entry(0, new Vector3f(1f, 1f, 1f)), // Default, WHITE

                    Map.entry(1, new Vector3f(1f, 0.2f, 0.2f)),  // Unwatchable, RED
                    Map.entry(2, new Vector3f(0.6f, 0.2f, 1f)),  // DoT, PURPLE
                    Map.entry(3, new Vector3f(0.2f, 1f, 0.2f)),  // Heal, GREEN

                    Map.entry(4, new Vector3f(1f, 0.8f, 0)) // Heavy, YELLOW
            );

    public TWOHEyesLayer(GeoRenderer<TheWorldOverHeavenEntity> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack matrixStackIn, TheWorldOverHeavenEntity twoh, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer bufferIn, float partialTick, int packedLightIn, int packedOverlay) {

        Vector3f color = overwriteColors.get(twoh.getOverwriteType());
        RenderType cameo = RenderType.eyes(LAYER);

        matrixStackIn.pushPose();
        getRenderer().reRender(getDefaultBakedModel(twoh), matrixStackIn, bufferSource, twoh, cameo,
                bufferSource.getBuffer(cameo), partialTick, packedLightIn, OverlayTexture.NO_OVERLAY, color.x(), color.y(), color.z(), 1f);
        matrixStackIn.popPose();
    }
}
