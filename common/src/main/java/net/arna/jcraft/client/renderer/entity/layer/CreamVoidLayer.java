package net.arna.jcraft.client.renderer.entity.layer;

import lombok.NonNull;
import mod.azure.azurelib.render.AzRendererPipelineContext;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.stand.CreamEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@Environment(EnvType.CLIENT)
public class CreamVoidLayer extends AbstractRenderLayer<CreamEntity> {
    private static final List<ResourceLocation> SKINS = IntStream.range(0, 4).mapToObj(
            i -> JCraft.id("textures/entity/stands/cream/void" + i + ".png")).toList();

    @Override
    public void render(final @NonNull AzRendererPipelineContext<UUID, CreamEntity> pc) {
        final CreamEntity cream = pc.animatable();
        if (cream.tickCount == 0) {
            return;
        }
        final RenderType cameo = RenderType.dragonExplosionAlpha(SKINS.get(cream.getSkin()));
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
