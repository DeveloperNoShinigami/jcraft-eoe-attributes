package net.arna.jcraft.fabric.datagen;

import com.mojang.serialization.Codec;
import lombok.Getter;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.attack.core.MoveMap;
import net.arna.jcraft.common.attack.core.data.MoveSet;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.entity.stand.StandType;
import net.arna.jcraft.common.util.StandAnimationState;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class JStandMoveSetProvider<A extends StandEntity<A, S>, S extends Enum<S> & StandAnimationState<A>>
        extends FabricCodecDataProvider<MoveMap.Entry<A, S>> {
    @Getter // implements abstract method
    private final String name;
    private final StandType type;

    public JStandMoveSetProvider(FabricDataOutput dataOutput, StandType type) {
        super(dataOutput, PackOutput.Target.DATA_PACK, "movesets/stand/" + type.name().toLowerCase(Locale.ROOT),
                getCodec(type));
        // Turn the type name into camel case
        name = Arrays.stream(type.name().toLowerCase().split("_"))
                .map(s -> s.substring(0, 1).toUpperCase() + s.substring(1))
                .collect(Collectors.joining(" ")) + " Moveset(s)";
        this.type = type;
    }

    private static <A extends StandEntity<A, S>, S extends Enum<S> & StandAnimationState<A>> Codec<MoveMap.Entry<A, S>> getCodec(StandType type) {
        return MoveMap.Entry.codecFor(Optional.ofNullable(MoveSet.<A, S>get(type, "default"))
                .orElseThrow(() -> new IllegalArgumentException("No default moveset found for " + type))
                .getStateClass());
    }

    @Override
    protected void configure(BiConsumer<ResourceLocation, MoveMap.Entry<A, S>> provider) {
        // Generate a JSON file for each entry in each move set.
        Map<String, MoveSet<A, S>> moveSets = MoveSet.get(type);
        moveSets.forEach((name, moveSet) ->
                moveSet.save().getEntries().entries().forEach(e ->
                        provider.accept(JCraft.id(String.format("%s/%s/%s", name, e.getKey().getName(), getMoveName(e.getValue()))),
                                e.getValue())));
    }

    private static String getMoveName(MoveMap.Entry<?, ?> entry) {
        String name = entry.getMove().getName().getString();
        return name.toLowerCase(Locale.ROOT)
                .replace(" ", "_")
                .replaceAll("[^a-z0-9/._-]", "");
    }
}
