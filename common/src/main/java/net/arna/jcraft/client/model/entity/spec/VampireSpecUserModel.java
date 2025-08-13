package net.arna.jcraft.client.model.entity.spec;

import lombok.NonNull;
import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.model.GeoModel;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.spec.VampireSpecUser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class VampireSpecUserModel extends GeoModel<VampireSpecUser> {
    private static final ResourceLocation module = JCraft.id("geo/humanoid.geo.json");
    private static final ResourceLocation texture = JCraft.id("textures/entity/vampire_spec_user.png");
    private static final ResourceLocation animation = JCraft.id("animations/spec/spec_user.animation.json");

    @Override
    public ResourceLocation getModelResource(final VampireSpecUser animatable) {
        return module;
    }

    @Override
    public ResourceLocation getTextureResource(final VampireSpecUser animatable) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(final VampireSpecUser animatable) {
        return animation;
    }

    @Override
    public void setCustomAnimations(final @NonNull VampireSpecUser animatable, final long instanceId, final AnimationState<VampireSpecUser> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        this.getBone("head").ifPresent(head -> {
            head.setRotX(-animatable.getXRot() * Mth.DEG_TO_RAD);
            head.setRotY((animatable.getYRot() - animatable.getViewYRot(animationState.getPartialTick())) * Mth.DEG_TO_RAD);
        });
    }
}
