package net.arna.jcraft.client.model.entity.npc;

import lombok.NonNull;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.npc.AyaTsujiEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * The model for {@link AyaTsujiEntity}.
 * @see net.arna.jcraft.client.renderer.entity.npc.AyaTsujiRenderer AyaTsujiRenderer
 */
public final class AyaTsujiModel {
    private static final ResourceLocation model = JCraft.id("geo/aya_tsuji.geo.json");
    private static final ResourceLocation texture = JCraft.id("textures/entity/aya_tsuji.png");
    private static final ResourceLocation animation = JCraft.id("animations/aya_tsuji.animation.json");


    /*@Override
    public ResourceLocation getModelResource(final AyaTsujiEntity animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(final AyaTsujiEntity animatable) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(final AyaTsujiEntity animatable) {
        return animation;
    }

    @Override
    public void setCustomAnimations(final @NonNull AyaTsujiEntity animatable, final long instanceId, final AnimationState<AyaTsujiEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        this.getBone("head").ifPresent(head -> {
            head.setRotX(-animatable.getXRot() * Mth.DEG_TO_RAD);
            head.setRotY((animatable.getYRot() - animatable.getViewYRot(animationState.getPartialTick())) * Mth.DEG_TO_RAD);
        });
    }*/
}
