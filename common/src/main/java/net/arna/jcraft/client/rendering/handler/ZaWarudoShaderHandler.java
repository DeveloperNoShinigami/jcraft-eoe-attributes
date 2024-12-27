package net.arna.jcraft.client.rendering.handler;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import lombok.NonNull;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.client.rendering.api.PostEffect;
import net.arna.jcraft.mixin_logic.StillDepthHolder;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

public class ZaWarudoShaderHandler extends StandShaderHandler {
    public static ZaWarudoShaderHandler INSTANCE = new ZaWarudoShaderHandler();
    public final ResourceLocation SHADER_ID = JCraft.id("shaders/post/za_warudo.json");

    public float prevRadius = 0f;
    public float radius = 0f;
    public long effectLength = 0;

    public @Nullable LivingEntity shaderSourceEntity = null;

    private final PostEffect EFFECT = new PostEffect(SHADER_ID, this::setup);

    private void setup(final PostEffect effect) {
        Minecraft mc = Minecraft.getInstance();
        effect.setSampler("DepthSampler", ((StillDepthHolder) mc.getMainRenderTarget()).jcraft$getDepthTexture());
        effect.getUniform("ViewPort").set(0, 0, mc.getWindow().getWidth(), mc.getWindow().getHeight());
    }

    @Override
    public void onWorldRendered(final @NonNull PoseStack matrices, final @NonNull Camera camera, final float tickDelta, final long nanoTime) {
        if (renderingEffect) {
            EFFECT.getUniform("InverseTransformMatrix").set(getInverseTransformMatrix(projectionMatrix));
            Vec3 cameraPos = camera.getPosition();
            EFFECT.getUniform("CameraPosition").set((float) cameraPos.x, (float) cameraPos.y, (float) cameraPos.z);
            if (shaderSourceEntity != null) {
                EFFECT.getUniform("Center").set(
                        lerp(shaderSourceEntity.getX(), shaderSourceEntity.xo, tickDelta),
                        lerp(shaderSourceEntity.getY() + shaderSourceEntity.getBbHeight() / 2, shaderSourceEntity.yo + shaderSourceEntity.getBbHeight() / 2, tickDelta),
                        lerp(shaderSourceEntity.getZ(), shaderSourceEntity.zo, tickDelta)
                );
            }
            EFFECT.getUniform("Radius").set(Math.max(0f, lerp(radius, prevRadius, tickDelta)));
        }
    }

    @Override
    public void tick(final Minecraft client) {
        if (!shouldRender) {
            renderingEffect = false;
            return;
        }

        if (!renderingEffect) {
            EFFECT.getUniform("OuterSat").set(1f);
            ticks = 0;
            radius = 0f;
            renderingEffect = true;
        }

        ticks++;
        prevRadius = radius;
        float expansionRate = 4f;
        int inversion = 100 / (int) expansionRate;

        if (ticks < inversion) {
            radius += expansionRate;
        } else if (ticks == inversion) {
            EFFECT.getUniform("OuterSat").set(0.3f);
        } else if (ticks < 2 * inversion) {
            radius -= 2 * expansionRate;
        }

        if (ticks > effectLength) { // effect is done
            renderingEffect = false;
            shouldRender = false;
        }
    }

    @Override
    public void renderEffect(final float tickDelta) {
        if (!this.renderingEffect) return;

        EFFECT.render(tickDelta);
    }

    private static Matrix4f getInverseTransformMatrix(Matrix4f outMat) {
        Matrix4f projection = RenderSystem.getProjectionMatrix();
        Matrix4f modelView = RenderSystem.getModelViewMatrix();
        outMat.identity();
        outMat.mul(projection);
        outMat.mul(modelView);
        outMat.invert();
        return outMat;
    }

    private static float lerp(final double n, final double prevN, final float tickDelta) {
        return (float) Mth.lerp(tickDelta, prevN, n);
    }
}
