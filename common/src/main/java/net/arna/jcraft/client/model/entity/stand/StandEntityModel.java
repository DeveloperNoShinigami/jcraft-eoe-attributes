package net.arna.jcraft.client.model.entity.stand;

import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.model.GeoModel;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.client.util.JClientUtils;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.entity.stand.StandType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.List;
import java.util.stream.IntStream;

public class StandEntityModel<E extends StandEntity<?, ?>> extends GeoModel<E> {
    private final StandType type;
    private final ResourceLocation model;
    private final List<ResourceLocation> skins;
    private final ResourceLocation animation;
    private final float torsoPitchOffset, headPitchOffset, velInfluence;
    // public float prevTorsoPitch, prevHeadPitch, prevBasePitch = 0.0f;

    public StandEntityModel(StandType type) {
        this(type, 0f, 0f);
    }

    public StandEntityModel(StandType type, float torsoPitchOffset, float headPitchOffset) {
        this(type, torsoPitchOffset, headPitchOffset, 90f);
    }

    public StandEntityModel(StandType type, float torsoPitchOffset, float headPitchOffset, float velInfluence) {
        this.type = type;
        String typeName = type.name().toLowerCase();
        model = JCraft.id("geo/" + typeName + ".geo.json");
        skins = IntStream.rangeClosed(0, type.getSkinCount())
                .mapToObj(i -> JCraft.id("textures/entity/stands/" + typeName + "/" + (i == 0 ? "default" : "skin" + i) + ".png"))
                .toList();
        animation = JCraft.id("animations/" + typeName + ".animation.json");

        this.torsoPitchOffset = torsoPitchOffset;
        this.headPitchOffset = headPitchOffset;
        this.velInfluence = velInfluence;
    }

    @Override
    public ResourceLocation getModelResource(E entity) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(E entity) {
        return skins.get(Mth.clamp(entity.getSkin(), 0, type.getSkinCount()));
    }

    @Override
    public ResourceLocation getAnimationResource(E entity) {
        return animation;
    }

    @Override
    public void setCustomAnimations(E animatable, long instanceId, AnimationState<E> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        if (skipCustomAnimations() || !animatable.hasUser()) {
            return;
        }

        // Note: KingCrimsonEntity overrides this due to some inversions, we should probably change the model later and standardize him.
        JClientUtils.animateGenericHumanoid(this, animatable, animatable.getUser(), animationState.getPartialTick(),
                true, true, torsoPitchOffset, headPitchOffset, velInfluence);
    }

    protected boolean skipCustomAnimations() {
        return false;
    }
}
