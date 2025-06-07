package net.arna.jcraft.fabric.datagen;

import com.mojang.serialization.Codec;
import dev.architectury.registry.registries.Registrar;
import net.arna.jcraft.api.stand.StandData;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;

public abstract class JAttackerDataProvider<T, D> extends FabricCodecDataProvider<D> {
    private final Registrar<T> registry;

    public JAttackerDataProvider(final FabricDataOutput dataOutput, final String directoryName, final Codec<D> codec,
                                 final Registrar<T> typeRegistry) {
        super(dataOutput, PackOutput.Target.DATA_PACK, directoryName, codec);
        this.registry = typeRegistry;
    }

    @Override
    protected void configure(final BiConsumer<ResourceLocation, D> provider) {
        for (final Map.Entry<ResourceKey<T>, T> entry : registry.entrySet()) {
            final ResourceLocation type = entry.getKey().location();

            Class<?> entityClass = getHolderClass(entry.getValue());

            for (final Field field : entityClass.getFields()) {
                if (field.getType() != StandData.class || !Modifier.isStatic(field.getModifiers()))
                    continue; // Skip non-stand data fields or non-static fields

                D data = getOrNull(type, field);
                if (data == null) continue;

                // Formulate the location based on the field name
                ResourceLocation loc = formulateLoc(type, field.getName());
                provider.accept(loc, data);
            }
        }
    }

    protected abstract Class<?> getHolderClass(T type);

    @SuppressWarnings("unchecked")
    private D getOrNull(ResourceLocation type, Field field) {
        try {
            return (D) field.get(null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to access field " + field.getName() + " for type " + type, e);
        }
    }

    private static ResourceLocation formulateLoc(ResourceLocation type, String fieldName) {
        boolean isDefault = "DATA".equals(fieldName);
        if (isDefault) {
            return type.withPath(p -> p + ".json");
        }

        String name = (fieldName.endsWith("_DATA") ? fieldName.substring(0, fieldName.length() - 5) : fieldName)
                .toLowerCase(Locale.ROOT);

        return type.withPath(p -> String.format("%s_%s.json", p, name));
    }
}
