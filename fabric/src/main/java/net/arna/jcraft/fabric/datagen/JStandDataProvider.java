package net.arna.jcraft.fabric.datagen;

import com.mojang.datafixers.util.Pair;
import lombok.Getter;
import net.arna.jcraft.api.JRegistries;
import net.arna.jcraft.api.stand.StandData;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Locale;
import java.util.function.BiConsumer;

@Getter
public class JStandDataProvider extends FabricCodecDataProvider<StandData> {
    private final String name = "Stand Data Provider";

    public JStandDataProvider(final FabricDataOutput dataOutput) {
        super(dataOutput, PackOutput.Target.DATA_PACK, "stands", StandData.CODEC);
    }

    @Override
    protected void configure(final BiConsumer<ResourceLocation, StandData> provider) {
        //noinspection DataFlowIssue
        JRegistries.STAND_TYPE_REGISTRY.entrySet().stream() // Get all registered StandTypes
                // Get each type's location and entity type
                .map(entry -> Pair.of(entry.getKey().location(), entry.getValue().getEntityType()))
                // Create a fake entity of each type to get its class.
                .map(p -> p.mapSecond(s -> s.create(null).getClass()))
                .flatMap(p -> Arrays.stream(p.getSecond().getFields())
                        .map(f -> p.mapSecond(s -> f))) // Find stand data fields in each class
                .filter(p -> p.getSecond().getType() == StandData.class &&
                        (p.getSecond().getModifiers() & Modifier.STATIC) != 0)
                .map(p -> p.mapSecond(f -> Pair.of(f.getName(), getOrNull(p.getFirst(), f)))) // Get the StandData instance
                .filter(p -> p.getSecond().getSecond() != null) // Filter out nulls
                .map(p -> p.getSecond().mapFirst(s ->
                        formulateLoc(p.getFirst(), p.getSecond().getFirst()))) // Formulate the location
                .forEach(p -> provider.accept(p.getFirst(), p.getSecond())); // Submit the location and data
    }

    private static StandData getOrNull(ResourceLocation type, Field field) {
        try {
            return (StandData) field.get(null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to access field " + field.getName() + " for type " + type, e);
        }
    }

    private static ResourceLocation formulateLoc(ResourceLocation type, String fieldName) {
        boolean isDefault = "DATA".equals(fieldName);
        if (isDefault) {
            return type.withPath(p -> "stand/" + p + ".json");
        }

        String name = (fieldName.endsWith("_DATA") ? fieldName.substring(0, fieldName.length() - 5) : fieldName)
                .toLowerCase(Locale.ROOT);

        return type.withPath(p -> String.format("stand/%s_%s.json", p, name));
    }
}
