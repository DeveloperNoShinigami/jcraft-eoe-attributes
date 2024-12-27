package net.arna.jcraft.client.rendering.handler;

import com.mojang.blaze3d.pipeline.RenderTarget;
import lombok.Getter;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.client.rendering.api.PostEffect;
import net.arna.jcraft.client.rendering.api.callbacks.PostShaderRenderCallback;

public class InversionShaderHandler implements PostShaderRenderCallback {
    public static final InversionShaderHandler INSTANCE = new InversionShaderHandler();
    private static final PostEffect SHADER = new PostEffect(JCraft.id("shaders/post/inversion.json"), InversionShaderHandler::setup);
    @Getter
    private static RenderTarget toInvertBuffer;

    private InversionShaderHandler() {}

    private static void setup(final PostEffect managedShaderEffect) {
        toInvertBuffer = SHADER.getRenderTarget("to_invert");
    }

    @Override
    public void renderEffect(final float tickDelta) {
        SHADER.render(tickDelta);
        toInvertBuffer.clear(true); // Clear for the next round.
    }

    public void init() {
        PostShaderRenderCallback.EVENT.register(this);
    }
}
