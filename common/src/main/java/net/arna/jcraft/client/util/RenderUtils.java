package net.arna.jcraft.client.util;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexSorting;
import lombok.experimental.UtilityClass;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.Optional;
import java.util.function.Supplier;

@UtilityClass
public class RenderUtils {

    public static final int FULL_BRIGHT = 15728880;

    public static ShaderInstance getShader(final RenderType type) {
        if (type instanceof RenderType.CompositeRenderType compositeRenderType) {
            Optional<Supplier<ShaderInstance>> shader = compositeRenderType.state.shaderState.shader;
            if (shader.isPresent()) {
                return shader.get().get();
            }
        }
        return null;
    }

    public static void renderGuiQuad(final BufferBuilder buffer, final int x, final int y, final int width, final int height, final int red, final int green, final int blue, final int alpha) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        buffer.vertex(x, y, 0.0).color(red, green, blue, alpha).endVertex();
        buffer.vertex(x, (y + height), 0.0).color(red, green, blue, alpha).endVertex();
        buffer.vertex((x + width), (y + height), 0.0).color(red, green, blue, alpha).endVertex();
        buffer.vertex((x + width), y, 0.0).color(red, green, blue, alpha).endVertex();
        BufferUploader.drawWithShader(buffer.end());
    }

    public static void vertexPos(final VertexConsumer vertexConsumer, final Matrix4f last, final float x, final float y, final float z) {
        vertexConsumer.vertex(last, x, y, z).endVertex();
    }

    public static void vertexPosUV(final VertexConsumer vertexConsumer, final Matrix4f last, final float x, final float y, final float z, final float u, final float v) {
        vertexConsumer.vertex(last, x, y, z).uv(u, v).endVertex();
    }

    public static void vertexPosUVLight(final VertexConsumer vertexConsumer, final Matrix4f last, final float x, final float y, final float z, final float u, final float v, final int light) {
        vertexConsumer.vertex(last, x, y, z).uv(u, v).uv2(light).endVertex();
    }

    public static void vertexPosColor(final VertexConsumer vertexConsumer, final Matrix4f last, final float x, final float y, final float z, final float r, final float g, final float b, final float a) {
        vertexConsumer.vertex(last, x, y, z).color(r, g, b, a).endVertex();
    }

    public static void vertexPosColorUV(final VertexConsumer vertexConsumer, final Matrix4f last, final float x, final float y, final float z, final float r, final float g, final float b, final float a, final float u, final float v) {
        vertexConsumer.vertex(last, x, y, z).color(r, g, b, a).uv(u, v).endVertex();
    }

    public static void vertexPosColorUVLight(final VertexConsumer vertexConsumer, final Matrix4f last, final float x, final float y, final float z, final float r, final float g, final float b, final float a, final float u, final float v, final int light) {
        vertexConsumer.vertex(last, x, y, z).color(r, g, b, a).uv(u, v).uv2(light).endVertex();
    }

    public static float distSqr(final float... a) {
        float d = 0.0F;
        for (float f : a) {
            d += f * f;
        }
        return d;
    }

    public static float distance(final float... a) {
        return Mth.sqrt(distSqr(a));
    }

//TODO    public static void renderBlockAtPosition(WorldRenderContext context, Vec3d pos, Identifier texture, float alpha) {
//        renderBlockAtPosition(context.matrixStack(), context.camera(), pos, texture, alpha, GameRenderer::getPositionColorTexProgram);
//    }

    public static void renderBlockAtPosition(final PoseStack matrixStack, final Camera camera, final Vec3 pos, final ResourceLocation texture, final float alpha) {
        renderBlockAtPosition(matrixStack, camera, pos, texture, alpha, GameRenderer::getPositionColorTexShader);
    }

    public static void renderBlock(final PoseStack matrixStack, final VertexConsumer vertexConsumer) {

        final Matrix4f matrix4f = matrixStack.last().pose();
        renderSide(matrix4f, vertexConsumer, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F);
        renderSide(matrix4f, vertexConsumer, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        renderSide(matrix4f, vertexConsumer, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F);
        renderSide(matrix4f, vertexConsumer, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F);
        renderSide(matrix4f, vertexConsumer, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F);
    }

    private static void renderSide(final Matrix4f model, final VertexConsumer vertices, final float x1, final float x2, final float y1, final float y2, final float z1, final float z2, final float z3, final float z4) {
        vertices.vertex(model, x1, y1, z1).endVertex();
        vertices.vertex(model, x2, y1, z2).endVertex();
        vertices.vertex(model, x2, y2, z3).endVertex();
        vertices.vertex(model, x1, y2, z4).endVertex();
    }

    /**
     * Renders a Block at a pos
     */
    public static void renderBlockAtPosition(final PoseStack matrixStack, final Camera camera, final Vec3 pos, final ResourceLocation texture, final float alpha, final Supplier<ShaderInstance> shader) {
        matrixStack.pushPose();
        final Vec3 transformedPos = pos.subtract(camera.getPosition());
        matrixStack.translate(transformedPos.x, transformedPos.y, transformedPos.z);
        Matrix4f positionMatrix = matrixStack.last().pose();

        final Tesselator tesselator = Tesselator.getInstance();
        final BufferBuilder buffer = tesselator.getBuilder();

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);

        final Color color = new Color(255, 255, 255);
        int intColor = color.getRGB();

        for (Direction direction : Direction.values()) {
            float x1 = direction == Direction.WEST || direction == Direction.DOWN || direction == Direction.NORTH ? 1 : 0;
            float x2 = direction == Direction.EAST || direction == Direction.UP || direction == Direction.SOUTH ? 1 : 0;
            float y1 = direction == Direction.DOWN || direction == Direction.NORTH || direction == Direction.WEST ? 1 : 0;
            float y2 = direction == Direction.UP || direction == Direction.SOUTH || direction == Direction.EAST ? 1 : 0;
            float z1 = direction == Direction.NORTH || direction == Direction.UP || direction == Direction.WEST ? 1 : 0;
            float z2 = direction == Direction.SOUTH || direction == Direction.DOWN || direction == Direction.EAST ? 1 : 0;

            buffer.vertex(positionMatrix, x1, y1, z1).color(intColor).uv(0, 1).endVertex();
            buffer.vertex(positionMatrix, x1, y2, z2).color(intColor).uv(0, 0).endVertex();
            buffer.vertex(positionMatrix, x2, y2, z2).color(intColor).uv(1, 0).endVertex();
            buffer.vertex(positionMatrix, x2, y1, z1).color(intColor).uv(1, 1).endVertex();
        }

        RenderSystem.setShader(shader);
        RenderSystem.setShaderTexture(0, texture);
        tesselator.end();

        matrixStack.popPose();
    }

    public static void startOverlayRender() {
        final Window window = Minecraft.getInstance().getWindow();
        RenderSystem.backupProjectionMatrix();

        // Define the parameters for the projection matrix
        final float fov = (float) Math.toRadians(45.0f); // Field of view in radians
        final float aspectRatio = (float) window.getGuiScaledWidth() / window.getGuiScaledHeight();
        final float zNear = 1000.0f;
        final float zFar = 3000.0f;

        final Matrix4f projectionMatrix = new Matrix4f().perspective(fov, aspectRatio, zNear, zFar);

        RenderSystem.setProjectionMatrix(projectionMatrix, VertexSorting.ORTHOGRAPHIC_Z);

        PoseStack mvStack = RenderSystem.getModelViewStack();
        mvStack.pushPose();
        mvStack.setIdentity();
        mvStack.translate(0.0, 0.0, -2000.0);
        RenderSystem.applyModelViewMatrix();

    }

    public static void endOverlayRender() {
        RenderSystem.restoreProjectionMatrix();
        RenderSystem.getModelViewStack().popPose();
    }
}
