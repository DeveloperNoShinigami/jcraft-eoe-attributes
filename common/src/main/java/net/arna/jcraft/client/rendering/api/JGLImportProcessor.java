package net.arna.jcraft.client.rendering.api;

import net.arna.jcraft.JCraft;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;
import com.mojang.blaze3d.preprocessor.GlslPreprocessor;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Make sure we can have the CORE shaders in our own namespace
 */
public class JGLImportProcessor extends GlslPreprocessor {

    @Nullable
    @Override
    public String applyImport(final boolean pUseFullPath, final String pDirectory) {
        final ResourceLocation resourcelocation = new ResourceLocation(pDirectory);
        final ResourceLocation resourcelocation1 = new ResourceLocation(resourcelocation.getNamespace(), "shaders/include/" + resourcelocation.getPath());
        try {
            final Resource resource1 = Minecraft.getInstance().getResourceManager().getResource(resourcelocation1).get();

            return IOUtils.toString(resource1.open(), StandardCharsets.UTF_8);
        } catch (IOException ioexception) {
            JCraft.LOGGER.error("Could not open GLSL import {}: {}", pDirectory, ioexception.getMessage());
            return "#error " + ioexception.getMessage();
        }
    }
}
