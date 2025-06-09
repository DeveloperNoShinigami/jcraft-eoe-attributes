package net.arna.jcraft.mixin.client;

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {
    @Unique
    private static final String PATH_PREFIX = "models/item/stand_disc_";

    @Shadow protected abstract void loadTopLevel(ModelResourceLocation location);

    @Shadow @Final private Map<ResourceLocation, UnbakedModel> topLevelModels;

    @Shadow public abstract UnbakedModel getModel(ResourceLocation modelLocation);

    // Adds stand disc models to the top-level models in ModelBakery to ensure they can be used in item rendering.
    @Inject(method = "<init>", at = @At("RETURN"))
    private void injectStandDiscsIntoTopLevel(BlockColors blockColors, ProfilerFiller profilerFiller,
                                              Map<ResourceLocation, BlockModel> modelResources,
                                              Map<ResourceLocation, List<ModelBakery.LoadedJson>> blockStateResources,
                                              CallbackInfo ci) {
        profilerFiller.push("jcraft_stand_discs");
        modelResources.entrySet().stream()
                .filter(entry -> entry.getKey().getPath().startsWith(PATH_PREFIX))
                .forEach(entry -> {
                    ResourceLocation modelId = entry.getKey().withPath(p ->
                            p.substring("models/item/".length(), p.length() - 5)); // Remove path and ".json" suffix
                    // Add the model to the top-level models, so it can be used in item rendering.
                    ModelResourceLocation modelVariantId = new ModelResourceLocation(modelId, "inventory");
                    this.loadTopLevel(modelVariantId);
                    this.topLevelModels.get(modelVariantId).resolveParents(this::getModel);
                });

        profilerFiller.pop();
    }
}
