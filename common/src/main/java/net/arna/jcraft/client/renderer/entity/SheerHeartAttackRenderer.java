package net.arna.jcraft.client.renderer.entity;

import net.arna.jcraft.client.model.entity.SheerHeartAttackModel;
import net.arna.jcraft.common.entity.SheerHeartAttackEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * The {@link AbstractEntityRenderer} for {@link SheerHeartAttackEntity}.
 * @see SheerHeartAttackModel
 */
@Environment(EnvType.CLIENT)
public class SheerHeartAttackRenderer extends AbstractEntityRenderer<SheerHeartAttackEntity> {

    public static final String ID = "sha";

    public SheerHeartAttackRenderer(final EntityRendererProvider.Context context) {
        super(context, () -> new EntityAnimator<>(ID), ID);
    }

    /*
    @Override
    public RenderType getRenderType(final SheerHeartAttackEntity animatable, final ResourceLocation texture, final @Nullable MultiBufferSource bufferSource, final float partialTick) {
        return RenderType.entityTranslucent(getGeoModel().getTextureResource(animatable));
    }*/
}