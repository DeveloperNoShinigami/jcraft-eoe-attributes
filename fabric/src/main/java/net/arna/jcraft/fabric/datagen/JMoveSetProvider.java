package net.arna.jcraft.fabric.datagen;

import com.mojang.serialization.Codec;
import lombok.Getter;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.api.attack.MoveSet;
import net.arna.jcraft.api.attack.MoveSetManager;
import net.arna.jcraft.common.attack.core.IAttacker;
import net.arna.jcraft.common.attack.core.MoveMap;
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

public class JMoveSetProvider<A extends IAttacker<A, S>, S extends Enum<S>>
        extends FabricCodecDataProvider<MoveMap.Entry<A, S>> {
    @Getter // implements abstract method
    private final String name;
    private final ResourceLocation type;

    public JMoveSetProvider(FabricDataOutput dataOutput, ResourceLocation type) {
        super(dataOutput, PackOutput.Target.DATA_PACK, "movesets/stand/" + type.getPath(), getCodec(type));
        // Turn the type name into camel case
        name = Arrays.stream(type.getPath().split("_"))
                .map(s -> s.substring(0, 1).toUpperCase() + s.substring(1))
                .collect(Collectors.joining(" ")) + " Moveset(s)";
        this.type = type;
    }

    private static <A extends IAttacker<A, S>, S extends Enum<S>> Codec<MoveMap.Entry<A, S>> getCodec(ResourceLocation type) {
        return MoveMap.Entry.codecFor(Optional.ofNullable(MoveSetManager.<A, S>get(type, "default"))
                .orElseThrow(() -> new IllegalArgumentException("No default moveset found for " + type))
                .getStateClass());
    }

    @Override
    protected void configure(BiConsumer<ResourceLocation, MoveMap.Entry<A, S>> provider) {
        // Generate a JSON file for each entry in each move set.
        Map<String, MoveSet<A, S>> moveSets = MoveSetManager.get(type);
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
