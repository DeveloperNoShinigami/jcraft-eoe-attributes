package net.arna.jcraft.client.renderer.effects;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.arna.jcraft.common.splatter.JSplatterManager;
import net.arna.jcraft.common.splatter.SplatterSection;
import net.arna.jcraft.common.util.JUtils;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class SplatterEffectRenderer {


    public static void render(PoseStack matrices, Vec3 camPos, ClientLevel world, float tickDelta) {
        JSplatterManager splatterManager = JUtils.getSplatterManager(world);

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionColorTexLightmapShader);


        splatterManager.iterateSplatters(splatter -> {
            if (splatter.isRemoved()) {
                return;
            }

            RenderSystem.setShaderTexture(0, splatter.getType().getTexture());

            matrices.pushPose();
            matrices.translate(-camPos.x, -camPos.y, -camPos.z);

            Tesselator tess = Tesselator.getInstance();
            BufferBuilder buf = tess.getBuilder();
            buf.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);
            float alpha = splatter.getStrength(tickDelta);

            for (SplatterSection section : splatter.getSections()) {
                if (!section.isRemoved()) {
                    renderSection(section, buf, matrices, alpha, splatter.getOffset());
                }
            }

            tess.end();
            matrices.popPose();
        });

        RenderSystem.disableBlend();
        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
    }

    @SuppressWarnings("DuplicatedCode") // I do not care how similar the different directions' code are.
    private static void renderSection(SplatterSection section, BufferBuilder buf, PoseStack matrices, float alpha, float offset) {
        matrices.pushPose();
        Vector3f offsetVec = section.getDirection().step();
        offsetVec.mul(offset, offset, offset); // Prevent z-fighting with anchor block and other splatters.
        matrices.translate(offsetVec.x(), offsetVec.y(), offsetVec.z());
        Matrix4f m = matrices.last().pose();

        int blockLight = section.getWorld().getBrightness(LightLayer.BLOCK, section.getBlockPos());
        int skyLight = section.getWorld().getBrightness(LightLayer.SKY, section.getBlockPos());
        int light = LightTexture.pack(blockLight, skyLight);

        Vector3f min = section.getMinPos();
        Vector3f max = section.getMaxPos();
        Vec2 minUv = section.getMinUv();
        Vec2 maxUv = section.getMaxUv();

        float minX = min.x();
        float minY = min.y();
        float minZ = min.z();
        float maxX = max.x();
        float maxY = max.y();
        float maxZ = max.z();

        switch (section.getDirection()) {
            case UP -> {
                vertex(buf, m, minX, minY, minZ, minUv.x, minUv.y, alpha, light);
                vertex(buf, m, maxX, minY, minZ, maxUv.x, minUv.y, alpha, light);
                vertex(buf, m, maxX, minY, maxZ, maxUv.x, maxUv.y, alpha, light);
                vertex(buf, m, minX, minY, maxZ, minUv.x, maxUv.y, alpha, light);
            }
            case DOWN -> {
                vertex(buf, m, maxX, minY, minZ, maxUv.x, minUv.y, alpha, light);
                vertex(buf, m, minX, minY, minZ, minUv.x, minUv.y, alpha, light);
                vertex(buf, m, minX, minY, maxZ, minUv.x, maxUv.y, alpha, light);
                vertex(buf, m, maxX, minY, maxZ, maxUv.x, maxUv.y, alpha, light);
            }
            case NORTH -> {
                vertex(buf, m, minX, minY, minZ, minUv.x, minUv.y, alpha, light);
                vertex(buf, m, minX, maxY, minZ, minUv.x, maxUv.y, alpha, light);
                vertex(buf, m, maxX, maxY, minZ, maxUv.x, maxUv.y, alpha, light);
                vertex(buf, m, maxX, minY, minZ, maxUv.x, minUv.y, alpha, light);
            }
            case EAST -> {
                vertex(buf, m, maxX, minY, minZ, maxUv.x, minUv.y, alpha, light);
                vertex(buf, m, maxX, maxY, minZ, minUv.x, minUv.y, alpha, light);
                vertex(buf, m, maxX, maxY, maxZ, minUv.x, maxUv.y, alpha, light);
                vertex(buf, m, maxX, minY, maxZ, maxUv.x, maxUv.y, alpha, light);
            }
            case SOUTH -> {
                vertex(buf, m, minX, minY, maxZ, minUv.x, maxUv.y, alpha, light);
                vertex(buf, m, maxX, minY, maxZ, maxUv.x, maxUv.y, alpha, light);
                vertex(buf, m, maxX, maxY, maxZ, maxUv.x, minUv.y, alpha, light);
                vertex(buf, m, minX, maxY, maxZ, minUv.x, minUv.y, alpha, light);
            }
            case WEST -> {
                vertex(buf, m, minX, minY, minZ, minUv.x, minUv.y, alpha, light);
                vertex(buf, m, minX, minY, maxZ, minUv.x, maxUv.y, alpha, light);
                vertex(buf, m, minX, maxY, maxZ, maxUv.x, maxUv.y, alpha, light);
                vertex(buf, m, minX, maxY, minZ, maxUv.x, minUv.y, alpha, light);
            }
        }

        matrices.popPose();
    }

    private static void vertex(BufferBuilder buf, Matrix4f matrix, float x, float y, float z, float u, float v, float alpha, int light) {
        buf
                .vertex(matrix, x, y, z)
                .color(1f, 1f, 1f, alpha)
                .uv(u, v)
                .uv2(light)
                .endVertex();
    }
}
