package net.arna.jcraft.datagen;

import net.arna.jcraft.api.JRegistries;
import net.arna.jcraft.api.spec.SpecType;
import net.arna.jcraft.api.stand.StandEntity;
import net.arna.jcraft.datagen.providers.data.*;
import net.arna.jcraft.datagen.providers.resources.JLangProvider;
import net.arna.jcraft.datagen.providers.resources.JModelProvider;
import net.arna.jcraft.datagen.providers.resources.JPoseProvider;
import net.arna.jcraft.mixin.EntityTypeAccessor;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class JDataGen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        final FabricDataGenerator.Pack pack = generator.createPack();
        pack.addProvider(JModelProvider::new);
        //pack.addProvider(JLanguageProvider::new);
        pack.addProvider(JLootTableProviders.BlockLoot::new);
        pack.addProvider(JLootTableProviders.EntityLoot::new);
        pack.addProvider(JTagProviders.JBlockTags::new);
        pack.addProvider(JTagProviders.JItemTags::new);
        pack.addProvider(JTagProviders.JEntityTypeTags::new);
        pack.addProvider(JAdvancementProvider::new);
        pack.addProvider(JRecipeProvider::new);
        pack.addProvider(JWorldProvider::new);
        pack.addProvider(JEvolutionProvider::new);
        pack.addProvider(JStandDataProvider::new);
        pack.addProvider(JSpecDataProvider::new);
        pack.addProvider(JPoseProvider::new);
        pack.addProvider(JLangProvider::new);

        // Each type needs its own MoveSetProvider as they have different state classes
        // and thus different codecs.
        Set<ResourceLocation> ids = new HashSet<>(JRegistries.STAND_TYPE_REGISTRY.getIds());
        ids.addAll(JRegistries.SPEC_TYPE_REGISTRY.getIds());
        for (final ResourceLocation type : ids) {
            if ("none".equals(type.getPath())) continue;
            pack.addProvider((FabricDataOutput output) -> new JMoveSetProvider<>(output, type));
        }
    }

    @Override
    public void buildRegistry(RegistrySetBuilder registryBuilder) {
        registryBuilder.add(Registries.CONFIGURED_FEATURE, JConfiguredFeatureProvider::bootstrap);
        registryBuilder.add(Registries.PLACED_FEATURE, JPlacedFeatureProvider::bootstrap);
        registryBuilder.add(Registries.BIOME, JBiomeProvider::bootstrap);
    }

    @SuppressWarnings("DataFlowIssue")
    public static Class<?> getSpecClass(SpecType type) {
        // Create fake LivingEntity to get a fake spec instance.
        return type.createSpec(new LivingEntity(EntityType.PIG, null) {
            @Override
            public @NotNull Iterable<ItemStack> getArmorSlots() {
                return List.of();
            }

            @Override
            public @NotNull ItemStack getItemBySlot(@NotNull EquipmentSlot slot) {
                return ItemStack.EMPTY;
            }

            @Override
            public void setItemSlot(@NotNull EquipmentSlot slot, @NotNull ItemStack stack) {

            }

            @Override
            public @NotNull HumanoidArm getMainArm() {
                return HumanoidArm.RIGHT;
            }
        }).getClass();
    }

    @SuppressWarnings({"DataFlowIssue", "unchecked"})
    public static <E extends Entity> Class<? extends Entity> getEntityClass(EntityType<E> type) {
        try {
            return ((EntityTypeAccessor<E>) type).getFactory().create(type, null).getClass();
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
