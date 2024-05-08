package net.arna.jcraft.client.events;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.client.JClientConfig;
import net.arna.jcraft.client.JCraftClient;
import net.arna.jcraft.client.rendering.RenderHandler;
import net.arna.jcraft.common.attack.core.MoveInputType;
import net.arna.jcraft.common.component.living.CommonCooldownsComponent;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.network.c2s.PlayerInputPacket;
import net.arna.jcraft.common.network.c2s.PredictionTriggerPacket;
import net.arna.jcraft.common.network.c2s.StandBlockPacket;
import net.arna.jcraft.common.util.*;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.arna.jcraft.registry.JPacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.joml.Matrix4f;

import java.util.Iterator;
import java.util.List;

import static net.arna.jcraft.client.JCraftClient.*;
import static net.arna.jcraft.client.gui.hud.JCraftAbilityHud.getHudX;
import static net.arna.jcraft.client.util.JClientUtils.activeTimestops;

public class JClientEvents {

    public static void onLast(MatrixStack matrixStack, Vec3d cameraPos, WorldRenderer worldRenderer) {
        matrixStack.push();
        matrixStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        if (worldRenderer.getTranslucentFramebuffer() != null) {
            MinecraftClient.getInstance().getFramebuffer().beginWrite(false);
        }
        RenderHandler.beginBufferedRendering(matrixStack);

        if (RenderHandler.MATRIX4F != null) {
            RenderSystem.getModelViewMatrix().get(RenderHandler.MATRIX4F);
        }
        RenderHandler.renderBufferedBatches(matrixStack);
        RenderHandler.endBufferedRendering(matrixStack);
        if (worldRenderer.getTranslucentFramebuffer() != null) {
            worldRenderer.getCloudsFramebuffer().beginWrite(false);
        }
        matrixStack.pop();
    }

    public static void afterTranslucent(MatrixStack matrixStack, Vec3d cameraPos, WorldRenderer worldRenderer) {
        matrixStack.push();
        matrixStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        RenderHandler.MATRIX4F = new Matrix4f(RenderSystem.getModelViewMatrix());
        matrixStack.pop();
    }

    public static void clientPlayerJoin(ClientPlayerEntity clientPlayerEntity) {
        if (clientPlayerEntity == null) {
            JCraft.LOGGER.fatal("onPlayReady was called with invalid client player!");
            return;
        }

        // Sync initial prediction option
        NetworkManager.sendToServer(JPacketRegistry.C2S_PREDICTION_TRIGGER,
                PredictionTriggerPacket.write(JClientConfig.getInstance().isClientsidePrediction()));
    }

    public static void renderHud(DrawContext ctx, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if (player == null) {
            JCraft.LOGGER.fatal("Attempted to render hud with no player!");
            return;
        }

        framesSinceCounted++;

        boolean isMid = JClientConfig.getInstance().getUiPosition() == JClientConfig.UIPos.MIDDLE;
        boolean useIcons = JClientConfig.getInstance().isIconHud();
        StandEntity<?, ?> stand = JUtils.getStand(player);

        TextRenderer textRenderer = client.inGameHud.getTextRenderer();

        int selectedX = getHudX(client.getWindow().getScaledWidth(), 128);
        int selectedY = client.getWindow().getScaledHeight();

        switch (JClientConfig.getInstance().getUiPosition()) {
            case LEFT -> selectedY /= 20;
            case MIDDLE -> selectedY /= 3;
            case RIGHT -> selectedY = (int) (selectedY / 2.25f);
        }

        // Draw text HUD
        if (!useIcons) {
            CommonCooldownsComponent cooldowns = JComponentPlatformUtils.getCooldowns(player);

            CooldownType[] values = CooldownType.values();
            for (int i = 0; i < values.length; i++) {
                CooldownType type = values[i];
                int cooldownTicks = cooldowns.getCooldown(type);

                if (cooldownTicks == 0) continue;
                double cooldown = (cooldownTicks - tickDelta) / 20d;

                // These are (mainly) based off of keybindings which are client-only and thus have
                // to be done here and cannot be done in CooldownType.
                String keyBindText = switch (type) {
                    case STAND_LIGHT -> "M1";
                    case HEAVY, STAND_HEAVY -> generateName(heavyKey.getParent());
                    case BARRAGE, STAND_BARRAGE -> generateName(barrageKey.getParent());
                    case ULTIMATE, STAND_ULTIMATE -> generateName(ultKey.getParent());
                    case SPECIAL1, STAND_SP1 -> generateName(special1Key.getParent());
                    case SPECIAL2, STAND_SP2 -> generateName(special2Key.getParent());
                    case SPECIAL3, STAND_SP3 -> generateName(special3Key.getParent());
                    case UTILITY -> generateName(utility.getParent());
                    case COMBO_BREAKER -> "Combo Breaker";
                    case COOLDOWN_CANCEL -> generateName(cooldownCancel.getParent());
                    case DASH -> generateName(dash.getParent());
                };

                CooldownType.Category category = type.getCategory();

                boolean isSpec = category == CooldownType.Category.SPEC;
                boolean isUniversal = category == CooldownType.Category.UNIVERSAL;
                float defaultAlpha = 0.65f;
                int xOffset = 0;

                String finalText = keyBindText + " - " + JCraftClient.decimalFormat.get().format(MathHelper.clamp(cooldown, 0.0, 9999.0)) + "s";

                if (category == CooldownType.Category.STAND || isSpec) {
                    if (!isSpec) finalText = "s." + finalText;

                    if ((isSpec && stand != null) || (!isSpec && stand == null)) {
                        xOffset = 48;
                        defaultAlpha = 0.3f;
                    }
                }

                int offsetIndex = i;
                if (isSpec)
                    offsetIndex -= 7;
                else if (isUniversal)
                    offsetIndex -= 6;
                float offsetY = selectedY * 1.25f + 9f * offsetIndex;

                ctx.drawTextWithShadow(
                        textRenderer,
                        finalText,
                        selectedX + xOffset,
                        (int) offsetY,
                        ColorUtils.HSBAtoRGBA(0.3f - (float) cooldown * 10f / 720f, (cooldown < 1.6) ? 0.0f : 1.0f, 1.0f, (cooldown < 1.6) ? 1.0f : defaultAlpha)
                );

            }
        }

        // Draw Combo Counter
        if (comboCounter > 0 && player.getWorld().getGameRules().getBoolean(JCraft.COMBO_COUNTER) && framesSinceCounted <= 180) {
            String remark = "epic tod free download";
            if (comboCounter < JCraftClient.comboRemarks.size() * 7)
                remark = comboRemarks.get(Math.floorDiv(comboCounter, 7));

            boolean recentHit = framesSinceCounted < 5;

            Random random = player.getRandom();

            if (comboStarted && ++framesSinceComboStarted > 59) comboStarted = false;

            ctx.drawTextWithShadow(
                    textRenderer,
                    comboCounter + " - (" + Math.round(damageScaling * 100f) + "%) - " + remark,
                    (int) (selectedX + (isMid && useIcons ? 54f : 0) + (recentHit ? tickDelta * random.nextFloat() * 5f : 0)),
                    (int) (selectedY * (1.15f) + (recentHit ? tickDelta * random.nextFloat() * 5f : 0)),
                    ColorUtils.HSBAtoRGBA(comboCounter / 360f - 1f, comboStarted ? framesSinceComboStarted / 60f : 1f, 1f, 0.8f)
            );
        }
    }

    public static void tickClient(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null) return;
        StandEntity<?, ?> stand = JUtils.getStand(player);

        // Handle JCraft inputs (stand, spec, universal controls)
        // Regular input (all moves, regular Minecraft movement (WASD and jumping) and dashing)
        if (player.isAlive()) {
            Object2BooleanMap<MovementInputType> movementInput = getChangedInputs(getMovementBindings());
            Object2BooleanMap<MoveInputType> moveInput = getChangedInputs(getBindings());

            if (!movementInput.isEmpty() || !moveInput.isEmpty())
                NetworkManager.sendToServer(JPacketRegistry.C2S_PLAYER_INPUT, PlayerInputPacket.write(movementInput, moveInput));

            Object2BooleanMap<MoveInputType> heldMoves = new Object2BooleanOpenHashMap<>();
            getBindings().forEach((key, value) -> {
                if (key.isDown())
                    heldMoves.put(value, true);
            });

            if (!heldMoves.isEmpty()) {
                NetworkManager.sendToServer(JPacketRegistry.C2S_PLAYER_INPUT_HOLD, PlayerInputPacket.write(null, heldMoves));
                //ClientPlayNetworking.send(JPacketRegistry.C2S_PLAYER_INPUT_HOLD, PlayerInputPacket.write(null, heldMoves));
            }

        }

        // Block
        if (getTrackedUseKey().isChangedThisTick()) {
            boolean pressed = getTrackedUseKey().isPressedThisTick();
            NetworkManager.sendToServer(JPacketRegistry.C2S_STAND_BLOCK, StandBlockPacket.write(pressed));
            if (stand != null && stand.isRemoteAndControllable() && pressed)
                NetworkManager.sendToServer(JPacketRegistry.C2S_REMOTE_STAND_INTERACT, new PacketByteBuf(Unpooled.buffer()));
        }

        // Cooldown Cancel
        if (cooldownCancel.isPressedThisTick())
            NetworkManager.sendToServer(JPacketRegistry.C2S_COOLDOWN_CANCEL, new PacketByteBuf(Unpooled.buffer()));

        if (client.isPaused() && client.isInSingleplayer()) return;

        // Timestop handling (nearly identical to serverside, but toStop is obtained in user.world instead of server world)
        Iterator<DimensionData> iter = activeTimestops.iterator();

        while (iter.hasNext()) {
            DimensionData timestop = iter.next();
            LivingEntity user = timestop.user;

            if (user == null || !user.isAlive() || --timestop.timer <= 0) {
                iter.remove();
                continue;
            }

            Vec3d pos = timestop.pos;

            List<? extends Entity> toStop = user.getWorld().getEntitiesByClass(Entity.class,
                    new Box(pos.add(96.0, 96.0, 96.0), pos.subtract(96.0, 96.0, 96.0)), EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR);

            for (Entity entity : toStop)
                if (!entity.hasVehicle() && entity != user && entity != JUtils.getStand(user) && entity != user.getVehicle())
                    JComponentPlatformUtils.getTimeStopData(entity).setTicks(2);
        }
    }
}
