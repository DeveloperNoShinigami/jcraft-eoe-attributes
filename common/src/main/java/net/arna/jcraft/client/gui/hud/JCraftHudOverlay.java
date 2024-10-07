package net.arna.jcraft.client.gui.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Setter;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.stand.MadeInHeavenEntity;
import net.arna.jcraft.common.entity.stand.MetallicaEntity;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.entity.stand.TheSunEntity;
import net.arna.jcraft.common.spec.AnubisSpec;
import net.arna.jcraft.common.spec.JSpec;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class JCraftHudOverlay {
    private static final ResourceLocation EMPTY_GAUGE = JCraft.id("textures/gui/empty_gauge.png");
    private static final ResourceLocation FULL_GAUGE = JCraft.id("textures/gui/full_gauge.png");
    private static final int gaugeWidth = 42;
    private static int gaugeHeightOffset;
    private static final int gaugeHeightOffsetMax = -65;
    private static final Gauge BLOCK_GAUGE = new Gauge(0.5f, 0.5f, 1.0f, 90);
    private static final Gauge SUN_SIZE_GAUGE = new Gauge(1.0f, 0.7f, 0.4f, 30);
    private static final Gauge TIME_ACCEL_GAUGE = new Gauge(1.0f, 0.8f, 0.0f, MadeInHeavenEntity.MAXIMUM_SPEEDOMETER);
    private static final Gauge BLOODLUST_GAUGE = new Gauge(0.8f, 0.1f, 0.2f, 5);
    private static final Gauge IRON_GAUGE = new Gauge(0.7f, 0.7f, 0.9f, (int) MetallicaEntity.IRON_MAX);

    public static void render(final GuiGraphics ctx) {
        final Minecraft client = Minecraft.getInstance();

        final int width = client.getWindow().getGuiScaledWidth();
        final int height = client.getWindow().getGuiScaledHeight();
        final int x = width / 2;

        final LocalPlayer player = client.player;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        gaugeHeightOffset = gaugeHeightOffsetMax;
        final int gaugeX = x - gaugeWidth / 2;

        if (client.gui.overlayMessageTime > 0) gaugeHeightOffset -= 12;

        final StandEntity<?, ?> stand = JUtils.getStand(player);
        if (stand != null) {
            if (stand instanceof TheSunEntity theSun) {
                final float darken = (theSun.isPassive() ? 0.4f : 0.0f);
                SUN_SIZE_GAUGE.render(ctx,
                        SUN_SIZE_GAUGE.red() - darken,
                        SUN_SIZE_GAUGE.green() - darken,
                        SUN_SIZE_GAUGE.blue() - darken,
                        gaugeX,
                        height + gaugeHeightOffset,
                        (int) (theSun.getRawScale() * 10.0F));
            } else {
                BLOCK_GAUGE.render(ctx, gaugeX, height + gaugeHeightOffset, (int) stand.getStandGauge());
            }
            if (stand instanceof MadeInHeavenEntity madeInHeaven && madeInHeaven.getAccelTime() > 0) {
                TIME_ACCEL_GAUGE.render(ctx, gaugeX, height + gaugeHeightOffset, madeInHeaven.getSpeedometer());
            }
            if (stand instanceof MetallicaEntity metallica) {
                IRON_GAUGE.render(ctx, gaugeX, height + gaugeHeightOffset, (int) metallica.getIron());
            }
        }

        final JSpec<?, ?> spec = JUtils.getSpec(player);
        if (spec instanceof AnubisSpec) {
            final int displayBloodlust = (int) ((JComponentPlatformUtils.getMiscData(player).getAttackSpeedMult() - 1.0f) * 5);
            if (displayBloodlust > 0) {
                BLOODLUST_GAUGE.render(ctx, gaugeX, height + gaugeHeightOffset, displayBloodlust);
            }
        }
    }

    protected record Gauge(float red, float green, float blue, int max) {
        public Gauge(Vector3f color, int max) {
            this(color.x(), color.y(), color.z(), max);
        }

        public void render(GuiGraphics ctx, int x, int y, int value) {
            render(ctx, red, green, blue, x, y, value);
        }

        public void render(GuiGraphics ctx, float r, float g, float b, int x, int y, int value) {
            RenderSystem.setShaderColor(r, g, b, 1);
            //RenderSystem.setShaderTexture(0, EMPTY_GAUGE);
            ctx.blit(EMPTY_GAUGE, x, y, 0, 0, gaugeWidth, 5, gaugeWidth, 5);
            //RenderSystem.setShaderTexture(0, FULL_GAUGE);
            ctx.blit(FULL_GAUGE, x, y, 0, 0, value * gaugeWidth / max, 5, gaugeWidth, 5);
            gaugeHeightOffset -= 6;
            RenderSystem.setShaderColor(1, 1, 1, 1f);
        }
    }
}
