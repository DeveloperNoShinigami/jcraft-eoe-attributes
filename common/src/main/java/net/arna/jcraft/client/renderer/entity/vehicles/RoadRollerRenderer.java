package net.arna.jcraft.client.renderer.entity.vehicles;

import lombok.NonNull;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.client.model.entity.RoadRollerModel;
import net.arna.jcraft.client.renderer.entity.AbstractEntityRenderer;
import net.arna.jcraft.common.entity.vehicle.RoadRollerEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * The {@link AbstractEntityRenderer} for {@link RoadRollerEntity}.
 */
@Environment(EnvType.CLIENT)
public class RoadRollerRenderer extends AbstractEntityRenderer<RoadRollerEntity> {

    public static final String ID = "road_roller";

    public RoadRollerRenderer(final @NonNull EntityRendererProvider.Context context) {
        super(context, () -> new EntityAnimator<>(ID), b -> b.setRenderType(RenderType.entityTranslucent(JCraft.id(TEXTURE_STR_TEMPLATE.formatted(ID)))), ID);
    }

    @Override
    public boolean shouldShowName(final RoadRollerEntity animatable) {
        return animatable.shouldShowName() && super.shouldShowName(animatable); // why the fuck is shouldShowName ignored when the target isn't a Mob???
    }
}