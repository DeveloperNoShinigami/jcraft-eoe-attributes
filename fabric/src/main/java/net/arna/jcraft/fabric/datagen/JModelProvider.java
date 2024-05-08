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
        generator.registerSimpleCubeAll(JBlockRegistry.FOOLISH_SAND_BLOCK.get());
        generator.registerSimpleCubeAll(JBlockRegistry.METEORITE_BLOCK.get());
        generator.registerSimpleCubeAll(JBlockRegistry.METEORITE_IRON_ORE_BLOCK.get());
        generator.registerSimpleCubeAll(JBlockRegistry.SOUL_BLOCK.get());
    }

    @Override
    public void generateItemModels(ItemModelGenerator generator) {
        generator.register(JItemRegistry.BOXINGGLOVES.get(), Models.GENERATED);
        generator.register(JItemRegistry.BULLET.get(), Models.GENERATED);
        generator.register(JItemRegistry.CINDERELLA_MASK.get(), Models.GENERATED);
        generator.register(JItemRegistry.DIOBOOTS.get(), Models.GENERATED);
        generator.register(JItemRegistry.DIOCAPE.get(), Models.GENERATED);
        generator.register(JItemRegistry.DIOHEADBAND.get(), Models.GENERATED);
        generator.register(JItemRegistry.DIOJACKET.get(), Models.GENERATED);
        generator.register(JItemRegistry.DIOPANTS.get(), Models.GENERATED);
        generator.register(JItemRegistry.DIOSDIARY.get(), Models.GENERATED);
        generator.register(JItemRegistry.GREENBABY.get(), Models.GENERATED);
        generator.register(JItemRegistry.JOTAROBOOTS.get(), Models.GENERATED);
        generator.register(JItemRegistry.JOTAROCAP.get(), Models.GENERATED);
        generator.register(JItemRegistry.JOTAROJACKET.get(), Models.GENERATED);
        generator.register(JItemRegistry.JOTAROPANTS.get(), Models.GENERATED);
        generator.register(JItemRegistry.KARSHEADWRAP.get(), Models.GENERATED);
        generator.register(JItemRegistry.KNIFE.get(), Models.HANDHELD);
        generator.register(JItemRegistry.KQ_COIN.get(), Models.GENERATED);
        generator.register(JItemRegistry.LIVINGARROW.get(), Models.HANDHELD);
        generator.register(JItemRegistry.RED_HAT.get(), Models.GENERATED);
        generator.register(JItemRegistry.REQUIEMARROW.get(), Models.GENERATED);
        generator.register(JItemRegistry.REQUIEMRUBY.get(), Models.HANDHELD);
        generator.register(JItemRegistry.SINNERSSOUL.get(), Models.GENERATED);
        generator.register(JItemRegistry.STANDARROW.get(), Models.HANDHELD);
        generator.register(JItemRegistry.STAND_ARROWHEAD.get(), Models.GENERATED);
        generator.register(JItemRegistry.STAND_DISC.get(), Models.GENERATED);
        generator.register(JItemRegistry.STELLAR_IRON_INGOT.get(), Models.GENERATED);
        generator.register(JItemRegistry.STONE_MASK.get(), Models.GENERATED);
    }
}