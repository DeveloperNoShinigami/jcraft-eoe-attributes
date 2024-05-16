package net.arna.jcraft.client.rendering.skybox;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import org.joml.Matrix4f;

public interface JSkyBox {
    float getAlpha();

    void render(PoseStack matrices, Matrix4f matrix4f, float tickDelta, Camera camera, boolean thickFog);
}