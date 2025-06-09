package net.arna.jcraft.client.rendering;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ModelWithCustomRenderer implements BakedModel {
    private final BakedModel model;

    public ModelWithCustomRenderer(BakedModel model) {
        this.model = model;
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, @NotNull RandomSource random) {
        return model.getQuads(state, direction, random);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return model.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return model.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return model.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return true;
    }

    @Override
    public @NotNull TextureAtlasSprite getParticleIcon() {
        return model.getParticleIcon();
    }

    @Override
    public @NotNull ItemTransforms getTransforms() {
        return model.getTransforms();
    }

    @Override
    public @NotNull ItemOverrides getOverrides() {
        return model.getOverrides();
    }
}
