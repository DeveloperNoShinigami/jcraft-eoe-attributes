package net.arna.jcraft.client.renderer.entity.layer;

import lombok.NonNull;
import mod.azure.azurelib.render.AzRendererPipelineContext;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.stand.KQBTDEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

@Environment(EnvType.CLIENT)
public class KQBTDEyesLayer extends AbstractRenderLayer<KQBTDEntity> {
    private static final ResourceLocation LAYER = new ResourceLocation(JCraft.MOD_ID, "textures/entity/stands/killer_queen_bites_the_dust/eyes.png");

    @Override
    public void render(final @NonNull AzRendererPipelineContext<UUID, KQBTDEntity> pc) {
        final RenderType cameo = RenderType.eyes(LAYER);
        pc.setRenderType(cameo);
        pc.setVertexConsumer(pc.multiBufferSource().getBuffer(cameo));
        pc.setPackedLight(15728640);
        pc.setPackedLight(OverlayTexture.NO_OVERLAY);
        pc.setRed(1f);
        pc.setGreen(1f);
        pc.setBlue(1f);
        pc.setAlpha(1f);
        pc.rendererPipeline().reRender(pc);
    }

}
