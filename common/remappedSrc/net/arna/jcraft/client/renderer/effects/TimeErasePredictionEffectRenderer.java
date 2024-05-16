package net.arna.jcraft.client.renderer.effects;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.architectury.event.events.client.ClientTickEvent;
import net.arna.jcraft.client.JCraftClient;
import net.arna.jcraft.client.util.RenderUtils;
import net.arna.jcraft.common.attack.moves.kingcrimson.PredictionMove;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.render.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import java.util.*;

public class TimeErasePredictionEffectRenderer {
    private static int ticksLeft = 0;
    private static final Map<Entity, Vec3> predictions = new WeakHashMap<>();
    private static RenderTarget predictionsBuffer = Minecraft.getInstance().getMainRenderTarget();

    public static void init() {
        ClientTickEvent.CLIENT_POST.register(client -> {
            if (ticksLeft < 0) {
                predictions.clear();
                return;
            }

            if (!Minecraft.getInstance().isPaused()) {
                ticksLeft--;
            }

            synchronized (predictions) {
                updatePredictions();
            }
        });

        RenderSystem.recordRenderCall(() -> {
            Window window = Minecraft.getInstance().getWindow();
            predictionsBuffer = new TextureTarget(window.getWidth(), window.getHeight(), true, true);
        });
    }

    public static void startEffect(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be at least 1.");
        }
        ticksLeft = length;

        Minecraft client = Minecraft.getInstance();
        for (Entity entity : PredictionMove.getEntitiesToCatch(client.level, JCraftClient.getStandEntity(), client.player)) {
            predictions.put(entity, entity.position());
        }
    }

    public static void stopEffect() {
        ticksLeft = -1;
        predictions.clear();
    }

    @SuppressWarnings("deprecation") // Minecraft does this too.
    public static void render(PoseStack stack, Vec3 camPos, ClientLevel world, float tickDelta, MultiBufferSource pConsumers) {
        if (ticksLeft < 0) {
            if (ticksLeft == -1) {
                predictionsBuffer.clear(false);
                ticksLeft--;
            }
            return;
        }

        // Ensure these are drawn and empty
        MultiBufferSource.BufferSource consumers = (MultiBufferSource.BufferSource) Objects.requireNonNull(pConsumers);
        consumers.endBatch(Sheets.solidBlockSheet());
        consumers.endBatch(Sheets.cutoutBlockSheet());

        // Acquire the predictions
        Set<Map.Entry<Entity, Vec3>> predictionsSet;
        synchronized (predictions) {
            predictionsSet = new HashSet<>(predictions.entrySet());
        }

        // Init frame-buffer
        predictionsBuffer.clear(false);
        predictionsBuffer.bindWrite(true);

        // Render entities
        EntityRenderDispatcher entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();

        for (Map.Entry<Entity, Vec3> prediction : predictionsSet) {
            Entity entity = prediction.getKey();
            if (entity == null || !entity.isAlive()) {
                continue;
            }

            Vec3 pos = prediction.getValue().subtract(camPos);
            BlockPos bPos = BlockPos.containing(prediction.getValue());

            int blockLight = Math.max(entity.isOnFire() ? 15 : entity.level().getBrightness(LightLayer.BLOCK, bPos), 7);
            int skyLight = Math.max(entity.level().getBrightness(LightLayer.SKY, bPos), 7);
            entityRenderDispatcher.render(entity, pos.x, pos.y - 0.1, pos.z, entity.getYRot(), tickDelta, stack,
                    consumers, LightTexture.pack(blockLight, skyLight));
        }

        // Draw entities to predictions buffer
        consumers.endLastBatch();
        consumers.endBatch(RenderType.entitySolid(TextureAtlas.LOCATION_BLOCKS));
        consumers.endBatch(RenderType.entityCutout(TextureAtlas.LOCATION_BLOCKS));
        consumers.endBatch(RenderType.entityCutoutNoCull(TextureAtlas.LOCATION_BLOCKS));
        consumers.endBatch(RenderType.entitySmoothCutout(TextureAtlas.LOCATION_BLOCKS));
        consumers.endBatch(Sheets.solidBlockSheet());
        consumers.endBatch(Sheets.cutoutBlockSheet());

        // Restore framebuffer
        Minecraft.getInstance().getMainRenderTarget().bindWrite(true);

        // Draw predictions buffer on top of main buffer
        Window window = Minecraft.getInstance().getWindow();
        RenderSystem.enableBlend();
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        RenderSystem.setShaderTexture(0, predictionsBuffer.getColorTextureId());
        RenderUtils.startOverlayRender();

        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buffer = tess.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);

        final float r = 1, g = 0, b = 0, a = 0.33f;
        buffer.vertex(0, 0, 0).color(r, g, b, a).uv(0, 1).endVertex();
        buffer.vertex(window.getGuiScaledWidth(), 0, 0).color(r, g, b, a).uv(1, 1).endVertex();
        buffer.vertex(window.getGuiScaledWidth(), window.getGuiScaledHeight(), 0).color(r, g, b, a).uv(1, 0).endVertex();
        buffer.vertex(0, window.getGuiScaledHeight(), 0).color(r, g, b, a).uv(0, 0).endVertex();
        BufferUploader.drawWithShader(buffer.end());

        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
        RenderUtils.endOverlayRender();
    }

    private static void updatePredictions() {
        Set<Map.Entry<Entity, Vec3>> predictionsSet;
        synchronized (predictions) {
            predictionsSet = new HashSet<>(predictions.entrySet());
        }

        PredictionMove.updatePredictions(predictionsSet, ticksLeft);
    }
}
