package net.arna.jcraft.fabric.datagen;

import lombok.Getter;
import net.arna.jcraft.api.JRegistries;
import net.arna.jcraft.api.stand.StandData;
import net.arna.jcraft.api.stand.StandType;
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

@Getter
public class JStandDataProvider extends FabricCodecDataProvider<StandData> {
    private final String name = "Stand Data Provider";

    public JStandDataProvider(final FabricDataOutput dataOutput) {
        super(dataOutput, PackOutput.Target.DATA_PACK, "stands", StandData.CODEC);
    }

    @Override
    protected void configure(final BiConsumer<ResourceLocation, StandData> provider) {
        for (final Map.Entry<ResourceKey<StandType>, StandType> entry : JRegistries.STAND_TYPE_REGISTRY.entrySet()) {
            final ResourceLocation type = entry.getKey().location();

            //noinspection DataFlowIssue // We're passing a null level here, but that should be fine.
            Class<?> entityClass = entry.getValue().getEntityType().create(null).getClass();

            for (final Field field : entityClass.getFields()) {
                if (field.getType() != StandData.class || !Modifier.isStatic(field.getModifiers()))
                    continue; // Skip non-stand data fields or non-static fields

                StandData standData = getOrNull(type, field);
                if (standData == null) continue;

                // Formulate the location based on the field name
                ResourceLocation loc = formulateLoc(type, field.getName());
                provider.accept(loc, standData);
            }
        }
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
