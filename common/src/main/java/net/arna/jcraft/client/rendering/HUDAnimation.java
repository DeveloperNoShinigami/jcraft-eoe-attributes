package net.arna.jcraft.client.rendering;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.phys.Vec2;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.regex.Pattern;
import java.util.stream.StreamSupport;

/**
 * Parses an animation packed using <a href="https://free-tex-packer.com/">Free Tex Packer</a>
 * into an atlas and a set of framesData.
 * <br/>
 * Settings used in Free Tex Packer:
 * <ul>
 * <li><b>Remove file ext</b>: enabled</li>
 * <li><b>Format</b>: JSON (array)</li>
 * <li><b>Allow rotation</b>: disabled</li>
 * <li><b>Allow trim</b>: disabled</li>
 * <li><b>Packer</b>: optimal packer (optional, can also be any other value)</li>
 * </ul>
 * Rest of the settings left as-is.<br/>
 * The filenames should be in the format 'frame&lt;i&gt;' where &lt;i&gt; is the index of the frame starting with 1.
 */
@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class HUDAnimation {
    private static final Pattern FILE_NAME_PATTERN = Pattern.compile("frame(\\d*)");
    private final ResourceLocation atlas;
    private final List<Frame> frames;
    @Getter(lazy = true)
    private final int frameCount = frames.size();

    public static HUDAnimation create(final ResourceLocation atlas, final ResourceLocation atlasData) {
        final Optional<Resource> dataRes = Minecraft.getInstance().getResourceManager().getResource(atlasData);
        if (dataRes.isEmpty()) {
            throw new IllegalArgumentException("Atlas data not found.");
        }

        final JsonObject data;
        try (BufferedReader reader = dataRes.get().openAsReader()) {
            data = new Gson().fromJson(reader, JsonObject.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not read atlas data.");
        }

        final JsonArray framesData = data.getAsJsonArray("frames");
        final JsonObject sizeData = data.getAsJsonObject("meta").getAsJsonObject("size");

        final int atlasWidth = sizeData.get("w").getAsInt();
        final int atlasHeight = sizeData.get("h").getAsInt();
        final List<Frame> frames = StreamSupport.stream(framesData.spliterator(), false)
                .peek(HUDAnimation::validateFrame)
                .map(frame -> Frame.parse(frame.getAsJsonObject(), atlas, atlasWidth, atlasHeight))
                .sorted(Comparator.comparingInt(Frame::getIndex))
                .toList();

        return new HUDAnimation(atlas, frames);
    }

    private static void validateFrame(JsonElement frame) {
        if (!frame.isJsonObject()) {
            throw new IllegalArgumentException("Frame in atlas data is not an object");
        }

        final JsonObject frameObj = frame.getAsJsonObject();
        if (!FILE_NAME_PATTERN.asMatchPredicate().test(frameObj.get("filename").getAsString())) {
            throw new IllegalArgumentException("Frame in atlas data has invalid filename (must be of format 'frame<i>' where <i> is a 1-based index.");
        }

        if (frameObj.get("rotated").getAsBoolean()) {
            throw new IllegalArgumentException("Frames may not be rotated");
        }
        if (frameObj.get("trimmed").getAsBoolean()) {
            throw new IllegalArgumentException("Frames may not be trimmed");
        }
    }

    /**
     * Preloads the texture atlas with the given texture manager and executor.
     *
     * @param textureManager The texture manager that will load the atlas.
     * @param executor       The executor used to load the atlas.
     */
    public void preload(TextureManager textureManager, Executor executor) {
        textureManager.preload(getAtlas(), executor);
    }

    public Frame getFrame(int i) {
        return frames.get(i);
    }

    /**
     * Creates a new frame-counter that fits this animation.
     *
     * @param framerate  The framerate of this animation. Probably 60 fps.
     * @param shouldLoop Whether the frame-counter should wrap around
     *                   when it has reached the end of the animation.
     * @return A new frame-counter for this animation.
     * @see FrameCounter
     */
    public FrameCounter createFrameCounter(float framerate, boolean shouldLoop) {
        return new FrameCounter(framerate, getFrameCount(), shouldLoop);
    }

    @Data
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Frame {
        private final ResourceLocation atlas;
        private final int index;
        private final int width, height;
        private final int xOffset, yOffset;
        private final Vec2 uvMin, uvMax;

        private static Frame parse(JsonObject frame, ResourceLocation atlas, int atlasWidth, int atlasHeight) {
            JsonObject frameData = frame.getAsJsonObject("frame");

            int index = Integer.parseInt(frame.get("filename").getAsString().substring(5));
            int width = frameData.get("w").getAsInt();
            int height = frameData.get("h").getAsInt();
            int xOffset = frameData.get("x").getAsInt();
            int yOffset = frameData.get("y").getAsInt();
            float uMin = (float) xOffset / atlasWidth;
            float vMin = (float) yOffset / atlasHeight;
            float uMax = (float) (xOffset + width) / atlasWidth;
            float vMax = (float) (yOffset + height) / atlasHeight;

            return new Frame(atlas, index, width, height, xOffset, yOffset, new Vec2(uMin, vMin), new Vec2(uMax, vMax));
        }

        public void render() {
            Window window = Minecraft.getInstance().getWindow();

            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.setShaderTexture(0, getAtlas());

            Tesselator tessellator = Tesselator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuilder();
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

            bufferBuilder
                    .vertex(0.0, window.getGuiScaledHeight(), -90.0)
                    .uv(getUvMin().x, getUvMax().y)
                    .endVertex();
            bufferBuilder
                    .vertex(window.getGuiScaledWidth(), window.getGuiScaledHeight(), -90.0)
                    .uv(getUvMax().x, getUvMax().y)
                    .endVertex();
            bufferBuilder
                    .vertex(window.getGuiScaledWidth(), 0.0, -90.0)
                    .uv(getUvMax().x, getUvMin().y)
                    .endVertex();
            bufferBuilder
                    .vertex(0.0, 0.0, -90.0)
                    .uv(getUvMin().x, getUvMin().y)
                    .endVertex();

            tessellator.end();
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
        }
    }
}
