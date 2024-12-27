package net.arna.jcraft.client.rendering.api;

import lombok.Getter;

import java.util.function.BiConsumer;

@Getter
public abstract class DynamicShaderFxInstance {
    /**
     * The time since update was called for the first time.
     */
    private float time = 0F;
    private boolean removed;

    /**
     * Called every frame (before the effect is rendered)
     */
    public void update(final double deltaTime) {
        time += (float) (deltaTime / 20F);
    }

    /**
     * Write this fx instance's data to the texture buffer to upload them to the shader
     *
     * @param writer for writing data to the texture buffer
     */
    public abstract void writeDataToBuffer(final BiConsumer<Integer, Float> writer);

    public final void remove() {
        removed = true;
    }
}
