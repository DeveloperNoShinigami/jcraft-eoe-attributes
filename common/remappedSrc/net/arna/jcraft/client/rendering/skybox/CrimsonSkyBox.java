package net.arna.jcraft.client.rendering.skybox;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.arna.jcraft.platform.JPlatformUtils;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.*;
import net.minecraft.client.renderer.ShaderInstance;
import org.joml.Matrix4f;

import java.util.function.Supplier;

public class CrimsonSkyBox implements JSkyBox {
    public transient float alpha = 1;

    @Override
    public float getAlpha() {
        return alpha;
    }

    @Override
    public void render(PoseStack matrices, Matrix4f projectionMatrix, float tickDelta, Camera camera, boolean thickFog) {
        Minecraft client = Minecraft.getInstance();
        assert client.level != null;
        this.alpha = 1;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);
        Matrix4f mat = matrices.last().pose();
        Matrix4f invMat = new Matrix4f(mat);
        invMat.invert();
        var jShader =  JPlatformUtils.getTest();
        jShader.safeGetUniform("InverseTransformMatrix").set(invMat);
        jShader.safeGetUniform("Time").set((client.level.getGameTime() + tickDelta) / 20);
        Supplier<ShaderInstance> shaderInstanceSupplier = () -> jShader;
        RenderSystem.setShader(shaderInstanceSupplier);

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuilder();

        for (int i = 0; i < 6; ++i) {
            matrices.pushPose();
            if (i == 1) {
                matrices.mulPose(Axis.XP.rotationDegrees(90.0F));
            }

            if (i == 2) {
                matrices.mulPose(Axis.XP.rotationDegrees(-90.0F));
            }

            if (i == 3) {
                matrices.mulPose(Axis.XP.rotationDegrees(180.0F));
            }

            if (i == 4) {
                matrices.mulPose(Axis.ZP.rotationDegrees(90.0F));
            }

            if (i == 5) {
                matrices.mulPose(Axis.ZP.rotationDegrees(-90.0F));
            }

            projectionMatrix = matrices.last().pose();
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            bufferBuilder.vertex(projectionMatrix, -110.0F, -110.0F, -110.0F).uv(0.0F, 0.0F).color(40, 40, 40, 255 * getAlpha()).endVertex();
            bufferBuilder.vertex(projectionMatrix, -110.0F, -110.0F, 110.0F).uv(0.0F, 16.0F).color(40, 40, 40, 255 * getAlpha()).endVertex();
            bufferBuilder.vertex(projectionMatrix, 110.0F, -110.0F, 110.0F).uv(16.0F, 16.0F).color(40, 40, 40, 255 * getAlpha()).endVertex();
            bufferBuilder.vertex(projectionMatrix, 110.0F, -110.0F, -110.0F).uv(16.0F, 0.0F).color(40, 40, 40, 255 * getAlpha()).endVertex();
            tessellator.end();
            matrices.popPose();
        }

        RenderSystem.depthMask(true);
        //RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }
}
