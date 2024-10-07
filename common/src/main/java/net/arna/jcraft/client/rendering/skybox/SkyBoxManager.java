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

    public void setCurrentSkyBox(final JSkyBox skyBox) {
        this.currentSkyBox = skyBox;
    }

    public void renderSkyBox(final PoseStack matrices, final Matrix4f matrix4f, final float tickDelta, final Camera camera, final boolean thickFog) {
        currentSkyBox.render(matrices, matrix4f, tickDelta, camera, thickFog);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }
}
