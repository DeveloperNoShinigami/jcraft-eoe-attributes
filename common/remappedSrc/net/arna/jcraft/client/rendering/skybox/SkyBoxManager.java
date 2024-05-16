package net.arna.jcraft.client.rendering.skybox;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import org.joml.Matrix4f;

public class SkyBoxManager {
    private static final SkyBoxManager INSTANCE = new SkyBoxManager();

    private JSkyBox currentSkyBox = null;
    private boolean enabled = true;


    public static SkyBoxManager getInstance() {
        return INSTANCE;
    }

    public void clearSkyBox() {
        this.currentSkyBox = null;
    }

    public JSkyBox getCurrentSkybox() {
        return currentSkyBox;
    }

    public void setCurrentSkyBox(JSkyBox skyBox) {
        this.currentSkyBox = skyBox;
    }

    public void renderSkyBox(PoseStack matrices, Matrix4f matrix4f, float tickDelta, Camera camera, boolean thickFog) {
        currentSkyBox.render(matrices, matrix4f, tickDelta, camera, thickFog);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
