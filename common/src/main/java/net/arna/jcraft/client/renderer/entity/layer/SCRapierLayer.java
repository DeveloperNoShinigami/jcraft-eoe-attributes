package net.arna.jcraft.client.renderer.entity.layer;

import lombok.NonNull;
import mod.azure.azurelib.render.AzRendererPipelineContext;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.projectile.RapierProjectile;
import net.arna.jcraft.common.entity.stand.SilverChariotEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@Environment(EnvType.CLIENT)
public class SCRapierLayer extends AbstractRenderLayer<SilverChariotEntity> {
    private final static List<ResourceLocation> SKINS = IntStream.rangeClosed(0, 4)
            .mapToObj(i -> JCraft.id("textures/entity/stands/silver_chariot/rapier_" + (i == 0 ? "default" : "skin" + i) + ".png"))
            .toList();

    @Override
    public void render(final @NonNull AzRendererPipelineContext<UUID, SilverChariotEntity> pc) {
        final SilverChariotEntity sc = pc.animatable();
        if (!sc.hasRapier()) {
            return;
        }
        final SilverChariotEntity.Mode mode = sc.getMode();
        final RenderType cameo = RenderType.armorCutoutNoCull(
                mode == SilverChariotEntity.Mode.POSSESSED ? RapierProjectile.POSSESSED_TEXTURE :
                        mode == SilverChariotEntity.Mode.ARMORLESS ? RapierProjectile.ARMOR_OFF_TEXTURE :
                                SKINS.get(sc.getSkin())
        );
        pc.setRenderType(cameo);
        pc.setVertexConsumer(pc.multiBufferSource().getBuffer(cameo));
        pc.setPackedLight(OverlayTexture.NO_OVERLAY);
        pc.setRed(1f);
        pc.setGreen(1f);
        pc.setBlue(1f);
        pc.setAlpha(1f);
        pc.rendererPipeline().reRender(pc);
    }

}
