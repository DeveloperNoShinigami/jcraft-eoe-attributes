package net.arna.jcraft.client.model.entity;

import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.model.GeoModel;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.AyaTsujiEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class AyaTsujiModel extends GeoModel<AyaTsujiEntity> {
    @Override
    public ResourceLocation getModelResource(final AyaTsujiEntity animatable) {
        return JCraft.id("geo/aya_tsuji.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(final AyaTsujiEntity animatable) {
        return JCraft.id("textures/entity/aya_tsuji.png");
    }

    @Override
    public ResourceLocation getAnimationResource(final AyaTsujiEntity animatable) {
        return JCraft.id("animations/aya_tsuji.animation.json");
    }

    @Override
    public void setCustomAnimations(final @NotNull AyaTsujiEntity animatable, final long instanceId, final AnimationState<AyaTsujiEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        this.getBone("head").ifPresent(head -> {
            head.setRotX(-animatable.getXRot() * Mth.DEG_TO_RAD);
            head.setRotY((animatable.getYRot() - animatable.getViewYRot(animationState.getPartialTick())) * Mth.DEG_TO_RAD);
        });
    }
}
