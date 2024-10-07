package net.arna.jcraft.client.renderer.effects;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
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


    public static void render(final PoseStack matrices, final Vec3 camPos, final ClientLevel world, final float tickDelta) {
        final JSplatterManager splatterManager = JUtils.getSplatterManager(world);

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

            final Tesselator tess = Tesselator.getInstance();
            final BufferBuilder buf = tess.getBuilder();
            buf.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);
            final float alpha = splatter.getStrength(tickDelta);

            for (SplatterSection section : splatter.getSections()) {
                if (!section.isRemoved()) {
                    renderSection(section, buf, matrices, alpha, splatter.getOffset());
                }
            }

            BufferUploader.drawWithShader(buf.end());
            matrices.popPose();
        });

        RenderSystem.disableBlend();
        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
    }

    @SuppressWarnings("DuplicatedCode") // I do not care how similar the different directions' code are.
    private static void renderSection(final SplatterSection section, final BufferBuilder buf, final PoseStack matrices, final float alpha, final float offset) {
        matrices.pushPose();
        final Vector3f offsetVec = section.getDirection().step();
        offsetVec.mul(offset, offset, offset); // Prevent z-fighting with anchor block and other splatters.
        matrices.translate(offsetVec.x(), offsetVec.y(), offsetVec.z());
        final Matrix4f m = matrices.last().pose();

        final int blockLight = section.getWorld().getBrightness(LightLayer.BLOCK, section.getBlockPos());
        final int skyLight = section.getWorld().getBrightness(LightLayer.SKY, section.getBlockPos());
        final int light = LightTexture.pack(blockLight, skyLight);

        final Vector3f min = section.getMinPos();
        final Vector3f max = section.getMaxPos();
        final Vec2 minUv = section.getMinUv();
        final Vec2 maxUv = section.getMaxUv();

        final float minX = min.x();
        final float minY = min.y();
        final float minZ = min.z();
        final float maxX = max.x();
        final float maxY = max.y();
        final float maxZ = max.z();

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

    private static void vertex(final BufferBuilder buf, final Matrix4f matrix, final float x, final float y, final float z, final float u, final float v, final float alpha, final int light) {
        buf
                .vertex(matrix, x, y, z)
                .color(1f, 1f, 1f, alpha)
                .uv(u, v)
                .uv2(light)
                .endVertex();
    }
}
