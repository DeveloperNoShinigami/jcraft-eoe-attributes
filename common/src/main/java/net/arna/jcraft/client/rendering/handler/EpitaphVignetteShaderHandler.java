package net.arna.jcraft.client.rendering.handler;

import dev.architectury.event.events.client.ClientTickEvent;
import ladysnake.satin.api.event.PostWorldRenderCallbackV2;
import ladysnake.satin.api.event.ShaderEffectRenderCallback;
import ladysnake.satin.api.managed.ManagedShaderEffect;
import ladysnake.satin.api.managed.ShaderEffectManager;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.client.gui.hud.EpitaphOverlay;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.NotNull;

public class EpitaphVignetteShaderHandler extends StandShaderHandler {
    public static final EpitaphVignetteShaderHandler INSTANCE = new EpitaphVignetteShaderHandler();
    private static final ManagedShaderEffect SHADER = ShaderEffectManager.getInstance().manage(JCraft.id("shaders/post/epitaph_vignette.json"));

    private EpitaphVignetteShaderHandler() {
        if (INSTANCE != null) throw new IllegalStateException("An instance already exists.");
    }

    @Override
    public void onWorldRendered(@NotNull MatrixStack matrices, @NotNull Camera camera, float tickDelta, long nanoTime) {
        SHADER.setUniformValue("Intensity", EpitaphOverlay.getVignetteIntensity());
        SHADER.setUniformValue("Extend", EpitaphOverlay.getVignetteExtend());
    }

    @Override
    public void renderShaderEffects(float tickDelta) {
        if (EpitaphOverlay.shouldRenderVignette()) SHADER.render(tickDelta);
    }

    @Override
    public void tick(MinecraftClient client) {}

    public void init() {
        PostWorldRenderCallbackV2.EVENT.register(this);
        ClientTickEvent.CLIENT_POST.register(this);
        ShaderEffectRenderCallback.EVENT.register(this);
    }
}
