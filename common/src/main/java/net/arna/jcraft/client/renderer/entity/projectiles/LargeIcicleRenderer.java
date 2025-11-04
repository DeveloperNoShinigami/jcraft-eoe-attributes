package net.arna.jcraft.client.renderer.entity.projectiles;

import lombok.NonNull;
import mod.azure.azurelib.render.AzRendererPipelineContext;
import mod.azure.azurelib.render.entity.AzEntityRendererPipeline;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.client.renderer.entity.AbstractEntityRenderer;
import net.arna.jcraft.common.entity.projectile.LargeIcicleProjectile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import java.util.UUID;

/**
 * The {@link AbstractEntityRenderer} for {@link LargeIcicleProjectile}.
 */
@Environment(EnvType.CLIENT)
public class LargeIcicleRenderer extends ProjectileRenderer<LargeIcicleProjectile> {

    public static final String ID = "large_icicle";

    public LargeIcicleRenderer(final @NonNull EntityRendererProvider.Context pc) {
        super(pc, () -> new EntityAnimator<>(ID), b -> b
                .setRenderType(RenderType.entityTranslucent(JCraft.id(TEXTURE_STR_TEMPLATE.formatted(ID))))
                .setModelRenderer((pipeline, layer) -> new ProjectileModelRenderer<>((AzEntityRendererPipeline<LargeIcicleProjectile>)pipeline, layer) {
                    @Override
                    protected void midRender(final @NonNull AzRendererPipelineContext<UUID, LargeIcicleProjectile> pc) {
                        final float scale = pc.animatable().getScale();
                        pc.poseStack().scale(scale, scale, scale);
                    }
                }),
                ID);
    }

}
