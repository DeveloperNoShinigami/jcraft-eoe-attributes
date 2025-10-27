package net.arna.jcraft.client.renderer.entity.vehicles;

import lombok.NonNull;
import mod.azure.azurelib.animation.controller.AzAnimationController;
import mod.azure.azurelib.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.animation.impl.AzEntityAnimator;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.client.renderer.entity.AbstractEntityRenderer;
import net.arna.jcraft.common.entity.vehicle.AbstractGroundVehicleEntity;
import net.arna.jcraft.common.entity.vehicle.RoadRollerEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * The {@link AbstractEntityRenderer} for {@link RoadRollerEntity}.
 */
@Environment(EnvType.CLIENT)
public class RoadRollerRenderer extends AbstractEntityRenderer<RoadRollerEntity> {

    public static final String ID = "road_roller";

    public RoadRollerRenderer(final @NonNull EntityRendererProvider.Context context) {
        super(context, RoadRollerAnimator::new, b -> b.setRenderType(RenderType.entityTranslucent(JCraft.id(TEXTURE_STR_TEMPLATE.formatted(ID)))), ID);
    }

    @Override
    public boolean shouldShowName(final @NonNull RoadRollerEntity animatable) {
        return animatable.shouldShowName() && super.shouldShowName(animatable); // why the fuck is shouldShowName ignored when the target isn't a Mob???
    }

    public static class RoadRollerAnimator extends AzEntityAnimator<RoadRollerEntity> {

        protected static final ResourceLocation ANIMATION = JCraft.id(ANIMATION_STR_TEMPLATE.formatted(ID));

        @Override
        public void registerControllers(final @NonNull AzAnimationControllerContainer<RoadRollerEntity> animationControllerContainer) {
            animationControllerContainer.add(AzAnimationController.builder(this, AbstractGroundVehicleEntity.DEATH_CONTROLLER).build());
            animationControllerContainer.add(AzAnimationController.builder(this, AbstractGroundVehicleEntity.SHAKE_CONTROLLER).build());
            animationControllerContainer.add(AzAnimationController.builder(this, AbstractGroundVehicleEntity.MOVEMENT_CONTROLLER).setTransitionLength(5).build());
            animationControllerContainer.add(AzAnimationController.builder(this, AbstractGroundVehicleEntity.STEERING_CONTROLLER).setTransitionLength(5).build());
            animationControllerContainer.add(AzAnimationController.builder(this, AbstractGroundVehicleEntity.HIT_CONTROLLER).build());
        }

        @Override
        public @NotNull ResourceLocation getAnimationLocation(final @NonNull RoadRollerEntity animatable) {
            return ANIMATION;
        }
    }
}