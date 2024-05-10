package net.arna.jcraft.client.rendering.post;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.client.registry.JShaderRegistry;
import net.arna.jcraft.client.rendering.api.MultiInstancePostProcessor;
import net.arna.jcraft.client.rendering.api.PostProcessor;
import net.arna.jcraft.client.rendering.shader.JShader;
import net.arna.jcraft.client.util.Easing;
import net.minecraft.client.gl.JsonEffectShaderProgram;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class TimestopShaderPostProcessor extends MultiInstancePostProcessor<TimestopShaderFX> {

    private JsonEffectShaderProgram timeStopEffect;
    private float duration = 1F;
    private Vector3f centerWorldPos = new Vector3f(0.5F, 70F, 0.5F);

    @Override
    protected int getMaxInstances() {
        return 4;
    }

    @Override
    protected int getDataSizePerInstance() {
        return 4;
    }

    @Override
    public void init() {

        if (shaderEffect != null)
            timeStopEffect = effects[0];
    }

    @Override
    public Identifier getShaderEffectId() {
        return JCraft.id("za_warudo");
    }

    @Override
    public void beforeProcess(MatrixStack viewModelStack) {
        float progress = (float) (time / duration);


        if (progress > 1F) {
            setActive(false);
            return;
        }


        timeStopEffect.getUniformByName("Center").set(centerWorldPos);
    }

    @Override
    public void afterProcess() {
        setDataBufferUniform(timeStopEffect, "Data", "instanceCount");
    }

    public static void playEffect(Vector3f center) {
        Runnable timeStop = () -> {
            JShaderRegistry.ZA_WARUDO.addFxInstance(new TimestopShaderFX(center) {
                @Override
                public void update(double deltaTime) {
                    super.update(deltaTime);

                    float t = getTime() / 7.5F;
                    if (t < 1) {
                        t = Easing.CIRC_OUT.ease(t, 0F, 1F, 1F);
                    }

                    virtualRadius = t * 300F;
                    if (virtualRadius > 1300F) {
                        remove();
                    }
                }
            });
        };
    }
}
