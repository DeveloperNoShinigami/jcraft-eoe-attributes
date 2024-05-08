package net.arna.jcraft.client.rendering.shader;

import net.arna.jcraft.JCraft;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GlImportProcessor;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Make sure we can have the CORE shaders in our own namespace
 */
public class JGLImportProcessor extends GlImportProcessor {

    @Nullable
    @Override
    public String loadImport(boolean pUseFullPath, String pDirectory) {
        Identifier resourcelocation = new Identifier(pDirectory);
        Identifier resourcelocation1 = new Identifier(resourcelocation.getNamespace(), "shaders/include/" + resourcelocation.getPath());
        try {
            Resource resource1 = MinecraftClient.getInstance().getResourceManager().getResource(resourcelocation1).get();

            return IOUtils.toString(resource1.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException ioexception) {
            JCraft.LOGGER.error("Could not open GLSL import {}: {}", pDirectory, ioexception.getMessage());
            return "#error " + ioexception.getMessage();
        }
    }
}
