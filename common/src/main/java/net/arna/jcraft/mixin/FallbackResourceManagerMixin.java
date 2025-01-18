package net.arna.jcraft.mixin;

import dev.architectury.platform.Platform;
import net.arna.jcraft.JCraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.FallbackResourceManager;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceMetadata;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Mixin(FallbackResourceManager.class)
public class FallbackResourceManagerMixin {

    // Ensures the /reload command reloads move set changes from the JSON files.
    @Inject(method = "createResource", at = @At("HEAD"), cancellable = true)
    private static void createDirectResource(PackResources source, ResourceLocation location,
                                             IoSupplier<InputStream> streamSupplier, IoSupplier<ResourceMetadata> metadataSupplier,
                                             CallbackInfoReturnable<Resource> cir) {
        if (Platform.isDevelopmentEnvironment() ||
                !JCraft.MOD_ID.equals(location.getNamespace()) || !location.getPath().startsWith("movesets/")) return;

        Path p = Path.of("./../../common/src/main/generated/data/" + location.getNamespace() + "/" + location.getPath());
        if (Files.exists(p)) // Just to be sure.
            cir.setReturnValue(new Resource(source, () -> Files.newInputStream(p), metadataSupplier));
    }
}
