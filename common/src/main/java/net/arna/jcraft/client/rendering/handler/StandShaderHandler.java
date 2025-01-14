package net.arna.jcraft.client.rendering.handler;

import dev.architectury.event.events.client.ClientTickEvent;
import net.arna.jcraft.client.rendering.api.callbacks.PostShaderRenderCallback;
import net.arna.jcraft.client.rendering.api.callbacks.PostWorldRenderCallback;
import org.joml.Matrix4f;

public abstract class StandShaderHandler implements PostWorldRenderCallback, PostShaderRenderCallback, ClientTickEvent.Client {
    public int ticks = 0;
    public boolean shouldRender, renderingEffect = false;

    public final Matrix4f projectionMatrix = new Matrix4f();

    public void init() {
        PostWorldRenderCallback.EVENT.register(this);
        PostShaderRenderCallback.EVENT.register(this);
        ClientTickEvent.CLIENT_POST.register(this);
    }
}
