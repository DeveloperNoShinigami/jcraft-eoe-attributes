package net.arna.jcraft.client.rendering.post;

import net.arna.jcraft.client.rendering.api.DynamicShaderFxInstance;
import org.joml.Vector3f;

import java.util.function.BiConsumer;

public class TimestopShaderFX extends DynamicShaderFxInstance {

    public Vector3f center;
    public float virtualRadius = 0f;

    public TimestopShaderFX(final Vector3f center){
        this.center = center;
    }

    @Override
    public void writeDataToBuffer(final BiConsumer<Integer, Float> writer) {
        writer.accept(0, center.x());
        writer.accept(1, center.y());
        writer.accept(2, center.z());
        writer.accept(3, virtualRadius);
    }
}
