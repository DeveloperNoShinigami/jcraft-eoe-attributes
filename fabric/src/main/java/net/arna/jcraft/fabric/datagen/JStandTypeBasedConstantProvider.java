package net.arna.jcraft.fabric.datagen;

import com.mojang.serialization.Codec;
import dev.architectury.registry.registries.Registrar;
import lombok.Getter;
import lombok.NonNull;
import net.arna.jcraft.api.stand.StandType;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.mixin.EntityTypeAccessor;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.lang.reflect.Modifier;

@Getter
public abstract class JStandTypeBasedConstantProvider<T> extends JAttackerDataProvider<StandType, T> {
    public JStandTypeBasedConstantProvider(FabricDataOutput dataOutput, PackOutput.Target outputType, String directoryName,
                                           Codec<T> codec, Registrar<StandType> typeRegistry, Class<? extends T> clazz,
                                           @NonNull String fieldName) {
        super(dataOutput, outputType, directoryName, codec, typeRegistry, clazz, fieldName);
    }

    @Override
    protected Class<?> getHolderClass(StandType type) {
        return getEntityClass(type.getEntityType());
    }

    @SuppressWarnings({"DataFlowIssue", "unchecked"})
    private <T extends Entity> Class<? extends Entity> getEntityClass(EntityType<T> type) {
        try {
            return ((EntityTypeAccessor<T>) type).getFactory().create(type, null).getClass();
        } catch (Exception e) {
            // Entity type did not like the level being null, so we're going to do a disgusting hack
            // to figure out the type from the error's stack trace.

            // The general idea is that the first class in the stack trace after the StandType.<init> method
            // that is not abstract, must be the stand entity class.
            StackTraceElement[] stackTrace = e.getStackTrace();
            boolean foundStandEntityClass = false;
            for (StackTraceElement element : stackTrace) {
                if (StandEntity.class.getName().equals(element.getClassName())) {
                    foundStandEntityClass = true;
                    continue; // Skip the StandEntity class itself
                }

                if (!foundStandEntityClass) {
                    continue; // Skip until we find the StandEntity class
                }

                // Return first non-abstract class found
                try {
                    Class<?> clazz = Class.forName(element.getClassName());
                    if (!Modifier.isAbstract(clazz.getModifiers()) && Entity.class.isAssignableFrom(clazz)) {
                        return (Class<? extends Entity>) clazz;
                    }
                } catch (ClassNotFoundException ignored) {
                    // Ignore, continue searching
                }
            }

            throw new IllegalStateException("Could not determine entity class for " + type, e);
        }
    }
}
