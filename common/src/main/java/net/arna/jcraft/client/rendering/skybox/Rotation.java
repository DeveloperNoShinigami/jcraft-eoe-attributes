package net.arna.jcraft.client.rendering.skybox;

import org.joml.Vector3f;

public record Rotation(Vector3f staticRot, Vector3f axisRot, float rotationSpeed) {
    public static final Rotation DEFAULT = new Rotation(new Vector3f(0F, 0F, 0F), new Vector3f(0F, 0F, 0F), 1);
}
