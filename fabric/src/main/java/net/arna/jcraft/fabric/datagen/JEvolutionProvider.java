package net.arna.jcraft.fabric.datagen;

import lombok.Getter;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.util.EvolutionItemHandler;
import net.arna.jcraft.api.registry.JItemRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.function.BiConsumer;

import static net.arna.jcraft.api.registry.JStandTypeRegistry.*;

@Getter
public class JEvolutionProvider extends FabricCodecDataProvider<EvolutionItemHandler.Evolution> {
    private final String name = "Evolutions"; // implements getName()

    public JEvolutionProvider(final FabricDataOutput dataOutput) {
        super(dataOutput, PackOutput.Target.DATA_PACK, "evolutions", EvolutionItemHandler.Evolution.CODEC);
    }

    @Override
    protected void configure(final BiConsumer<ResourceLocation, EvolutionItemHandler.Evolution> provider) {
        List<EvolutionItemHandler.Evolution> evolutions = List.of(
                new EvolutionItemHandler.Evolution(JItemRegistry.DIARY_PAGE.get(), SHADOW_THE_WORLD.get(), THE_WORLD.get()),
                new EvolutionItemHandler.Evolution(JItemRegistry.GREEN_BABY.get(), WHITE_SNAKE.get(), C_MOON.get()),
                new EvolutionItemHandler.Evolution(JItemRegistry.DIOS_DIARY.get(), C_MOON.get(), MADE_IN_HEAVEN.get()),
                new EvolutionItemHandler.Evolution(JItemRegistry.DIOS_DIARY.get(), THE_WORLD.get(), THE_WORLD_OVER_HEAVEN.get()),

                new EvolutionItemHandler.Evolution(JItemRegistry.LIVING_ARROW.get(), KILLER_QUEEN.get(), KILLER_QUEEN_BITES_THE_DUST.get()),
                new EvolutionItemHandler.Evolution(JItemRegistry.LIVING_ARROW.get(), STAR_PLATINUM.get(), STAR_PLATINUM_THE_WORLD.get()),

                new EvolutionItemHandler.Evolution(JItemRegistry.REQUIEM_ARROW.get(), GOLD_EXPERIENCE.get(), GOLD_EXPERIENCE_REQUIEM.get())
        );

        evolutions.forEach(evolution -> provider.accept(JCraft.id(formatName(evolution)), evolution));
    }

    private static String formatName(final EvolutionItemHandler.Evolution evolution) {
        if (evolution.stand() == null) {
            return evolution.target().getData().getInfo().getReducedNameKey();
        }

        return evolution.stand().getId().getPath() + "_to_" + evolution.target().getId().getPath();
    }
}
