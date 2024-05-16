package net.arna.jcraft.client.registry;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.client.rendering.Phases;
import net.arna.jcraft.client.rendering.RenderHandler;
import net.arna.jcraft.platform.JPlatformUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class JRenderLayerRegistry extends RenderPhase {

    public JRenderLayerRegistry(String name, Runnable beginAction, Runnable endAction) {
        super(name, beginAction, endAction);
    }

    public static final RenderLayer TRANSPARENT_BLOCK =
            createGenericRenderLayer(
                    JCraft.MOD_ID,
                    "transparent_block",
                    VertexFormats.POSITION,
                    VertexFormat.DrawMode.QUADS,
                    new ShaderProgram(() -> JPlatformUtils.getTest()),
                    Phases.NORMAL_TRANSPARENCY,
                    SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE);

    public static final RenderLayer RRRE =
            createGenericRenderLayer(
                    JCraft.MOD_ID,
                    "rrre",
                    VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
                    VertexFormat.DrawMode.QUADS,
                    new ShaderProgram(() -> JPlatformUtils.getRred()),
                    RenderPhase.TRANSLUCENT_TRANSPARENCY
            );

    public static void init() {

    }

    /**
     * Creates a custom render layer with a texture.
     */
    public static RenderLayer createGenericRenderLayer(String modId, String name, VertexFormat format, VertexFormat.DrawMode mode, ShaderProgram shader, Transparency transparency, Identifier texture) {
        return createGenericRenderLayer(modId + ":" + name, format, mode, shader, transparency, new Texture(texture, false, false));
    }

    /**
     * Creates a custom render layer with an empty texture state.
     */
    public static RenderLayer createGenericRenderLayer(String modId, String name, VertexFormat format, VertexFormat.DrawMode mode, ShaderProgram shader, Transparency transparency, TextureBase texture) {
        return createGenericRenderLayer(modId + ":" + name, format, mode, shader, transparency, texture);
    }

    /**
     * Creates a custom render layer with an empty texture.
     */
    public static RenderLayer createGenericRenderLayer(String modId, String name, VertexFormat format, VertexFormat.DrawMode mode, ShaderProgram shader, Transparency transparency) {
        return createGenericRenderLayer(modId + ":" + name, format, mode, shader, transparency, NO_TEXTURE);
    }

    /**
     * Creates a custom render layer and creates a buffer builder for it.
     */
    public static RenderLayer createGenericRenderLayer(String name, VertexFormat format, VertexFormat.DrawMode mode, ShaderProgram shader, Transparency transparency, TextureBase texture) {
        RenderLayer type = RenderLayer.of(//TODO add rubidium etc?
                name, format, mode, JPlatformUtils.isModLoaded("sodium") ? 262144 : 256, false, false, RenderLayer.MultiPhaseParameters.builder()
                        .program(shader)
                        .transparency(transparency)
                        .texture(texture)
                        .cull(new Cull(true))
                        .build(true)
        );
        RenderHandler.addRenderLayer(type);
        return type;
    }
}
