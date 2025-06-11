package net.arna.jcraft.fabric.datagen;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.api.JRegistries;
import net.arna.jcraft.api.registry.JBlockRegistry;
import net.arna.jcraft.api.registry.JItemRegistry;
import net.arna.jcraft.api.registry.JStandTypeRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public class JModelProvider extends FabricModelProvider {

    private static final ModelTemplate SPAWN_EGG_MODEL = new ModelTemplate(Optional.of(new ResourceLocation("minecraft", "item/template_spawn_egg")), Optional.empty());

    public JModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators generator) {
        generator.createTrivialCube(JBlockRegistry.FOOLISH_SAND_BLOCK.get());
        generator.createTrivialCube(JBlockRegistry.METEORITE_BLOCK.get());
        generator.createTrivialCube(JBlockRegistry.POLISHED_METEORITE_BLOCK.get());
        generator.createTrivialCube(JBlockRegistry.METEORITE_IRON_ORE_BLOCK.get());
        generator.createTrivialCube(JBlockRegistry.SOUL_BLOCK.get());
        generator.createRotatedVariantBlock(JBlockRegistry.HOT_SAND_BLOCK.get());
        generator.createTrivialCube(JBlockRegistry.STELLAR_IRON_BLOCK.get());
        generator.createTrivialCube(JBlockRegistry.CINDERELLA_GREEN_BLOCK.get());
    }

    @Override
    public void generateItemModels(ItemModelGenerators generator) {
        generator.generateFlatItem(JItemRegistry.BOXING_GLOVES.get(), ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(JItemRegistry.BULLET.get(), ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(JItemRegistry.CINDERELLA_MASK.get(), ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(JItemRegistry.DIO_BOOTS.get(), ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(JItemRegistry.DIO_CAPE.get(), ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(JItemRegistry.DIO_HEADBAND.get(), ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(JItemRegistry.DIO_JACKET.get(), ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(JItemRegistry.DIO_PANTS.get(), ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(JItemRegistry.DIARY_PAGE.get(), ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(JItemRegistry.DIOS_DIARY.get(), ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(JItemRegistry.GREEN_BABY.get(), ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(JItemRegistry.JOTARO_BOOTS.get(), ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(JItemRegistry.JOTARO_CAP.get(), ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(JItemRegistry.JOTARO_JACKET.get(), ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(JItemRegistry.JOTARO_PANTS.get(), ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(JItemRegistry.JOHNNY_BOOTS.get(), ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(JItemRegistry.JOHNNY_CAP.get(), ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(JItemRegistry.JOHNNY_JACKET.get(), ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(JItemRegistry.JOHNNY_PANTS.get(), ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(JItemRegistry.KARS_HEADWRAP.get(), ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(JItemRegistry.KNIFE.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        generator.generateFlatItem(JItemRegistry.SCALPEL.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        generator.generateFlatItem(JItemRegistry.KQ_COIN.get(), ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(JItemRegistry.LIVING_ARROW.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        generator.generateFlatItem(JItemRegistry.RED_HAT.get(), ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(JItemRegistry.PUCCIS_HAT.get(), ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(JItemRegistry.REQUIEM_ARROW.get(), ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(JItemRegistry.REQUIEM_RUBY.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        generator.generateFlatItem(JItemRegistry.SINNERS_SOUL.get(), ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(JItemRegistry.STAND_ARROW.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        generator.generateFlatItem(JItemRegistry.STAND_ARROWHEAD.get(), ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(JItemRegistry.ROAD_ROLLER.get(), ModelTemplates.FLAT_ITEM);

        generateStandDiscModels(generator);

        generator.generateFlatItem(JItemRegistry.STELLAR_IRON_INGOT.get(), ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(JItemRegistry.STONE_MASK.get(), ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(JItemRegistry.PLANKTON_VIAL.get(), ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(JItemRegistry.PRISON_KEY.get(), ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(JItemRegistry.STEEL_BALL.get(), ModelTemplates.FLAT_ITEM);

        generator.generateFlatItem(JItemRegistry.PETSHOP_SPAWN_EGG.get(), SPAWN_EGG_MODEL);
        generator.generateFlatItem(JItemRegistry.AYA_TSUJI_SPAWN_EGG.get(), SPAWN_EGG_MODEL);
        generator.generateFlatItem(JItemRegistry.DARBY_OLDER_SPAWN_EGG.get(), SPAWN_EGG_MODEL);
        generator.generateFlatItem(JItemRegistry.DARBY_YOUNGER_SPAWN_EGG.get(), SPAWN_EGG_MODEL);
    }

    private void generateStandDiscModels(ItemModelGenerators generator) {
        // Generate a model for each stand and each skin.
        for (ResourceLocation id : JRegistries.STAND_TYPE_REGISTRY.getIds()) {
            if (id.equals(JStandTypeRegistry.NONE.getId())) {
                continue;
            }

            // For now, we assume each stand has 3 skins.
            // This will have to be revamped later when we redo the skin system anyway.
            // If there are fewer than 3 skins, the model will never be used regardless.
            for (int i = 0; i < 4; i++) {
                int fi = i;
                generator.generateLayeredItem(id.withPath(p -> "item/stand_disc_" + p + "_" + fi),
                        JCraft.id("item/stand_disc"), id.withPath(p -> "item/stands/" + p + "_" + fi));
            }
        }

        // Generate the default stand disc model.
        generator.generateFlatItem(JItemRegistry.STAND_DISC.get(), ModelTemplates.FLAT_ITEM);
    }
}
