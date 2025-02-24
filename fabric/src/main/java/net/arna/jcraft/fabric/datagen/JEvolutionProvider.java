package net.arna.jcraft.fabric.datagen;

import lombok.Getter;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.util.EvolutionItemHandler;
import net.arna.jcraft.registry.JItemRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.function.BiConsumer;

import static net.arna.jcraft.common.entity.stand.StandType.*;

@Getter
public class JEvolutionProvider extends FabricCodecDataProvider<EvolutionItemHandler.Evolution> {
    private final String name = "Evolutions"; // implements getName()

    public JEvolutionProvider(final FabricDataOutput dataOutput) {
        super(dataOutput, PackOutput.Target.DATA_PACK, "evolutions", EvolutionItemHandler.Evolution.CODEC);
    }

    @Override
    protected void configure(final BiConsumer<ResourceLocation, EvolutionItemHandler.Evolution> provider) {
        List<EvolutionItemHandler.Evolution> evolutions = List.of(
                new EvolutionItemHandler.Evolution(JItemRegistry.DIARY_PAGE.get(), SHADOW_THE_WORLD, THE_WORLD),
                new EvolutionItemHandler.Evolution(JItemRegistry.GREEN_BABY.get(), WHITE_SNAKE, C_MOON),
                new EvolutionItemHandler.Evolution(JItemRegistry.DIOS_DIARY.get(), C_MOON, MADE_IN_HEAVEN),
                new EvolutionItemHandler.Evolution(JItemRegistry.DIOS_DIARY.get(), THE_WORLD, THE_WORLD_OVER_HEAVEN),

                new EvolutionItemHandler.Evolution(JItemRegistry.LIVING_ARROW.get(), KILLER_QUEEN, KILLER_QUEEN_BITES_THE_DUST),
                new EvolutionItemHandler.Evolution(JItemRegistry.LIVING_ARROW.get(), STAR_PLATINUM, STAR_PLATINUM_THE_WORLD),

                new EvolutionItemHandler.Evolution(JItemRegistry.REQUIEM_ARROW.get(), GOLD_EXPERIENCE, GOLD_EXPERIENCE_REQUIEM)
        );

        evolutions.forEach(evolution -> provider.accept(JCraft.id(formatName(evolution)), evolution));
    }

    private static String formatName(final EvolutionItemHandler.Evolution evolution) {
        if (evolution.stand() == null) {
            return evolution.target().getNameKey();
        }

        return evolution.stand().getNameKey() + "_to_" + evolution.target().getNameKey();
    }
}
