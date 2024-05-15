package net.arna.jcraft.fabric.datagen;

import net.arna.jcraft.registry.JBlockRegistry;
import net.arna.jcraft.registry.JItemRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Model;
import net.minecraft.data.client.Models;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class JModelProvider extends FabricModelProvider {

    private static final Model SPAWN_EGG_MODEL = new Model(Optional.of(new Identifier("minecraft", "item/template_spawn_egg")), Optional.empty());

    public JModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator generator) {
        generator.registerSimpleCubeAll(JBlockRegistry.FOOLISH_SAND_BLOCK.get());
        generator.registerSimpleCubeAll(JBlockRegistry.METEORITE_BLOCK.get());
        generator.registerSimpleCubeAll(JBlockRegistry.METEORITE_IRON_ORE_BLOCK.get());
        generator.registerSimpleCubeAll(JBlockRegistry.SOUL_BLOCK.get());
        generator.registerRotatable(JBlockRegistry.HOT_SAND_BLOCK.get());
        generator.registerSimpleCubeAll(JBlockRegistry.STELLAR_IRON_BLOCK.get());
    }

    @Override
    public void generateItemModels(ItemModelGenerator generator) {
        generator.register(JItemRegistry.BOXING_GLOVES.get(), Models.GENERATED);
        generator.register(JItemRegistry.BULLET.get(), Models.GENERATED);
        generator.register(JItemRegistry.CINDERELLA_MASK.get(), Models.GENERATED);
        generator.register(JItemRegistry.DIO_BOOTS.get(), Models.GENERATED);
        generator.register(JItemRegistry.DIO_CAPE.get(), Models.GENERATED);
        generator.register(JItemRegistry.DIO_HEADBAND.get(), Models.GENERATED);
        generator.register(JItemRegistry.DIO_JACKET.get(), Models.GENERATED);
        generator.register(JItemRegistry.DIO_PANTS.get(), Models.GENERATED);
        generator.register(JItemRegistry.DIOS_DIARY.get(), Models.GENERATED);
        generator.register(JItemRegistry.GREEN_BABY.get(), Models.GENERATED);
        generator.register(JItemRegistry.JOTARO_BOOTS.get(), Models.GENERATED);
        generator.register(JItemRegistry.JOTARO_CAP.get(), Models.GENERATED);
        generator.register(JItemRegistry.JOTARO_JACKET.get(), Models.GENERATED);
        generator.register(JItemRegistry.JOTARO_PANTS.get(), Models.GENERATED);
        generator.register(JItemRegistry.KARS_HEADWRAP.get(), Models.GENERATED);
        generator.register(JItemRegistry.KNIFE.get(), Models.HANDHELD);
        generator.register(JItemRegistry.KQ_COIN.get(), Models.GENERATED);
        generator.register(JItemRegistry.LIVING_ARROW.get(), Models.HANDHELD);
        generator.register(JItemRegistry.RED_HAT.get(), Models.GENERATED);
        generator.register(JItemRegistry.REQUIEM_ARROW.get(), Models.GENERATED);
        generator.register(JItemRegistry.REQUIEM_RUBY.get(), Models.HANDHELD);
        generator.register(JItemRegistry.SINNERS_SOUL.get(), Models.GENERATED);
        generator.register(JItemRegistry.STAND_ARROW.get(), Models.HANDHELD);
        generator.register(JItemRegistry.STAND_ARROWHEAD.get(), Models.GENERATED);
        generator.register(JItemRegistry.STAND_DISC.get(), Models.GENERATED);
        generator.register(JItemRegistry.STELLAR_IRON_INGOT.get(), Models.GENERATED);
        generator.register(JItemRegistry.STONE_MASK.get(), Models.GENERATED);

        generator.register(JItemRegistry.PETSHOP_SPAWN_EGG.get(), SPAWN_EGG_MODEL);
        generator.register(JItemRegistry.AYA_TSUJI_SPAWN_EGG.get(), SPAWN_EGG_MODEL);
        generator.register(JItemRegistry.DARBY_OLDER_SPAWN_EGG.get(), SPAWN_EGG_MODEL);
    }
}