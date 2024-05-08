package net.arna.jcraft.fabric.datagen;

import net.arna.jcraft.registry.JBlockRegistry;
import net.arna.jcraft.registry.JItemRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;

public class JModelProvider extends FabricModelProvider {
    public JModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator generator) {
        generator.registerSimpleCubeAll(JBlockRegistry.METEORITE_BLOCK.get());
        generator.registerSimpleCubeAll(JBlockRegistry.METEORITE_IRON_ORE_BLOCK.get());
    }

    @Override
    public void generateItemModels(ItemModelGenerator generator) {
        generator.register(JItemRegistry.STAND_ARROWHEAD.get(), Models.GENERATED);
        generator.register(JItemRegistry.STELLAR_IRON_INGOT.get(), Models.GENERATED);
    }
}