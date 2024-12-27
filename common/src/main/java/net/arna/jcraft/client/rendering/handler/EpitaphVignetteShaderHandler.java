package net.arna.jcraft.client.rendering.handler;

import com.mojang.blaze3d.vertex.PoseStack;
import lombok.NonNull;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.client.gui.hud.EpitaphOverlay;
import net.arna.jcraft.client.rendering.api.PostEffect;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;

public class EpitaphVignetteShaderHandler extends StandShaderHandler {
    public static final EpitaphVignetteShaderHandler INSTANCE = new EpitaphVignetteShaderHandler();
    private static final PostEffect EFFECT = new PostEffect(JCraft.id("shaders/post/epitaph_vignette.json"));

    private EpitaphVignetteShaderHandler() {
        if (INSTANCE != null) throw new IllegalStateException("An instance already exists.");
    }

    @Override
    public void onWorldRendered(final @NonNull PoseStack matrices, final @NonNull Camera camera, final float tickDelta, final long nanoTime) {
        EFFECT.getUniform("Intensity").set(EpitaphOverlay.getVignetteIntensity());
        EFFECT.getUniform("Extend").set(EpitaphOverlay.getVignetteExtend());
    }

    @Override
    public void renderEffect(final float tickDelta) {
        if (EpitaphOverlay.shouldRenderVignette())
            EFFECT.render(tickDelta);
    }

    @Override
    public void tick(Minecraft client) {}
}
