package net.arna.jcraft.mixin;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import net.arna.jcraft.common.recipes.StandSkinSmithingRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {

    @Shadow
    private Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> recipes;

    @Shadow
    private Map<ResourceLocation, Recipe<?>> byName;

    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("RETURN"))
    private void addStandSkinSmithingRecipe(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci) {
        recipes = new HashMap<>(recipes);
        Map<ResourceLocation, Recipe<?>> smithingRecipes = new HashMap<>(recipes.getOrDefault(RecipeType.SMITHING, Map.of()));
        smithingRecipes.put(StandSkinSmithingRecipe.ID, StandSkinSmithingRecipe.INSTANCE);
        recipes.put(RecipeType.SMITHING, ImmutableMap.copyOf(smithingRecipes));
        recipes = ImmutableMap.copyOf(recipes);

        byName = new HashMap<>(byName);
        byName.put(StandSkinSmithingRecipe.ID, StandSkinSmithingRecipe.INSTANCE);
        byName = ImmutableMap.copyOf(byName);
    }
}
