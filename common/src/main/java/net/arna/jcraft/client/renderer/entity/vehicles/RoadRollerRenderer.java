package net.arna.jcraft.client.renderer.entity.vehicles;

import mod.azure.azurelib.renderer.GeoEntityRenderer;
import net.arna.jcraft.client.model.entity.RoadRollerModel;
import net.arna.jcraft.common.entity.vehicle.RoadRollerEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * The {@link GeoEntityRenderer} for {@link RoadRollerEntity}.
 * @see RoadRollerModel
 */
public class RoadRollerRenderer extends GeoEntityRenderer<RoadRollerEntity> {

    public RoadRollerRenderer(final EntityRendererProvider.Context context) {
        super(context, new RoadRollerModel());
    }

    @Override
    public RenderType getRenderType(final RoadRollerEntity animatable, final ResourceLocation texture, final @Nullable MultiBufferSource bufferSource, final float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

    @Override
    public boolean shouldShowName(final RoadRollerEntity animatable) {
        return animatable.shouldShowName() && super.shouldShowName(animatable); // why the fuck is shouldShowName ignored when the target isn't a Mob???
    }
}