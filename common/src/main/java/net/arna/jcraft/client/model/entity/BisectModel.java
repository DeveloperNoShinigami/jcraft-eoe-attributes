package net.arna.jcraft.client.model.entity;

import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.model.GeoModel;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.projectile.BisectProjectile;
import net.minecraft.resources.ResourceLocation;

/**
 * The {@link GeoModel} for {@link BisectProjectile}.
 */
public class BisectModel extends GeoModel<BisectProjectile> {
    @Override
    public ResourceLocation getModelResource(final BisectProjectile animatable) {
        return JCraft.id("geo/bisect.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(final BisectProjectile animatable) {
        return JCraft.id("textures/entity/projectiles/bisect.png");
    }

    @Override
    public ResourceLocation getAnimationResource(final BisectProjectile animatable) {
        return JCraft.id("animations/bisect.animation.json");
    }

    @Override
    public void setCustomAnimations(final BisectProjectile animatable, final long instanceId, final AnimationState<BisectProjectile> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        getBone("base").ifPresent(base -> {
            final float scale = animatable.getScale();
            base.setScaleX(scale);
            if (scale < 0.5f) {
                base.setScaleZ(scale);
            }
        });
    }
}
