package net.arna.jcraft.fabric.datagen;

import com.mojang.serialization.Codec;
import dev.architectury.registry.registries.Registrar;
import lombok.NonNull;
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
    private final Class<? extends D> clazz;
    private final String fieldName;

    public JAttackerDataProvider(final FabricDataOutput dataOutput, PackOutput.Target outputType,
                                 final String directoryName, final Codec<D> codec, final Registrar<T> typeRegistry,
                                 Class<? extends D> clazz, @NonNull final String fieldName) {
        super(dataOutput, outputType, directoryName, codec);
        this.registry = typeRegistry;
        this.clazz = clazz;
        this.fieldName = fieldName;
    }

    @Override
    protected final void configure(final BiConsumer<ResourceLocation, D> provider) {
        for (final Map.Entry<ResourceKey<T>, T> entry : registry.entrySet()) {
            if ("none".equals(entry.getKey().location().getPath())) continue;

            final ResourceLocation type = entry.getKey().location();

            Class<?> entityClass = getHolderClass(entry.getValue());

            for (final Field field : entityClass.getDeclaredFields()) {
                if (field.getType() != clazz || !Modifier.isStatic(field.getModifiers()))
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

    private ResourceLocation formulateLoc(ResourceLocation type, String fieldName) {
        boolean isDefault = this.fieldName.equals(fieldName);
        if (isDefault) {
            return type;
        }

        String name = (fieldName.endsWith("_" + this.fieldName) ? fieldName.substring(0, fieldName.length() - this.fieldName.length() - 1)
                : fieldName.startsWith(this.fieldName + "_") ? fieldName.substring(this.fieldName.length() + 1) : fieldName)
                .toLowerCase(Locale.ROOT);

        return type.withPath(p -> String.format("%s_%s", p, name));
    }
}
