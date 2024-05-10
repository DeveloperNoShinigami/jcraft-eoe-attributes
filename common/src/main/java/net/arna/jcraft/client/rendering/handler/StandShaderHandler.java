package net.arna.jcraft.client.rendering.handler;

import dev.architectury.event.events.client.ClientTickEvent;
import ladysnake.satin.api.event.PostWorldRenderCallbackV2;
import ladysnake.satin.api.event.ShaderEffectRenderCallback;
import org.joml.Matrix4f;

public abstract class StandShaderHandler implements PostWorldRenderCallbackV2, ClientTickEvent.Client, ShaderEffectRenderCallback {
    public int ticks = 0;
    public boolean shouldRender, renderingEffect = false;

    public final Matrix4f projectionMatrix = new Matrix4f();

}
