package net.arna.jcraft.client.rendering.handler;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.architectury.event.events.client.ClientTickEvent;
import ladysnake.satin.api.event.PostWorldRenderCallbackV2;
import ladysnake.satin.api.event.ShaderEffectRenderCallback;
import ladysnake.satin.api.experimental.ReadableDepthFramebuffer;
import ladysnake.satin.api.managed.ManagedShaderEffect;
import ladysnake.satin.api.managed.ShaderEffectManager;
import ladysnake.satin.api.util.GlMatrices;
import net.arna.jcraft.JCraft;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ZaWarudoShaderHandler extends StandShaderHandler {
    public static ZaWarudoShaderHandler INSTANCE = new ZaWarudoShaderHandler();
    public final ResourceLocation SHADER_ID = JCraft.id("shaders/post/za_warudo.json");

    public float prevRadius = 0f;
    public float radius = 0f;
    public long effectLength = 0;

    public @Nullable LivingEntity shaderSourceEntity = null;

    private final ManagedShaderEffect SHADER = ShaderEffectManager.getInstance().manage(SHADER_ID, this::setup);

    private void setup(ManagedShaderEffect managedShaderEffect) {
        Minecraft mc = Minecraft.getInstance();
        SHADER.setSamplerUniform("DepthSampler", ((ReadableDepthFramebuffer) mc.getMainRenderTarget()).getStillDepthMap());
        SHADER.setUniformValue("ViewPort", 0, 0, mc.getWindow().getWidth(), mc.getWindow().getHeight());
    }

    @Override
    public void onWorldRendered(@NotNull PoseStack matrices, @NotNull Camera camera, float tickDelta, long nanoTime) {
        if (renderingEffect) {
            SHADER.setUniformValue("InverseTransformMatrix", GlMatrices.getInverseTransformMatrix(projectionMatrix));
            Vec3 cameraPos = camera.getPosition();
            SHADER.setUniformValue("CameraPosition", (float) cameraPos.x, (float) cameraPos.y, (float) cameraPos.z);
            if (shaderSourceEntity != null) {
                SHADER.setUniformValue(
                        "Center",
                        lerp(shaderSourceEntity.getX(), shaderSourceEntity.xo, tickDelta),
                        lerp(shaderSourceEntity.getY() + shaderSourceEntity.getBbHeight() / 2, shaderSourceEntity.yo + shaderSourceEntity.getBbHeight() / 2, tickDelta),
                        lerp(shaderSourceEntity.getZ(), shaderSourceEntity.zo, tickDelta)
                );
            }
            SHADER.setUniformValue("Radius", Math.max(0f, lerp(radius, prevRadius, tickDelta)));
            //SHADER.render(tickDelta);
        }
    }

    @Override
    public void tick(Minecraft client) {

       if (shouldRender) {
            if (!renderingEffect) {
                SHADER.setUniformValue("OuterSat", 1f);
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
                SHADER.setUniformValue("OuterSat", 0.3f);
            } else if (ticks < 2 * inversion) {
                radius -= 2 * expansionRate;
            }
            if (hasFinishedAnimation()) {
                renderingEffect = false;
                shouldRender = false;
            }
        } else {
            renderingEffect = false;
        }
    }

    private float lerp(double n, double prevN, float tickDelta) {
        return (float) Mth.lerp(tickDelta, prevN, n);
    }

    private boolean hasFinishedAnimation() {
        return ticks > effectLength;
    }

    public void init() {
        PostWorldRenderCallbackV2.EVENT.register(this);
        ClientTickEvent.CLIENT_POST.register(this);
        ShaderEffectRenderCallback.EVENT.register(this);
    }

    @Override
    public void renderShaderEffects(float tickDelta) {
        if (this.renderingEffect) {
            SHADER.render(tickDelta);
        }
    }
}
