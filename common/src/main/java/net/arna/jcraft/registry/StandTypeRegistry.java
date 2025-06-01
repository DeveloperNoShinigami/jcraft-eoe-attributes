package net.arna.jcraft.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.api.StandType2;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import java.util.function.Supplier;

public interface StandTypeRegistry {
    ResourceKey<Registry<StandType2>> REGISTRY_KEY = ResourceKey.createRegistryKey(JCraft.id("stand_type"));
    DeferredRegister<StandType2> STAND_TYPE_REGISTRY = DeferredRegister.create(JCraft.MOD_ID, REGISTRY_KEY);

    RegistrySupplier<StandType2> NONE = register("none", () -> null);
    RegistrySupplier<StandType2> STAR_PLATINUM = register("star_platinum", JEntityTypeRegistry.STAR_PLATINUM);
    RegistrySupplier<StandType2> THE_WORLD = register("theworld", JEntityTypeRegistry.THE_WORLD);
    RegistrySupplier<StandType2> KING_CRIMSON = register("kingcrimson", JEntityTypeRegistry.KING_CRIMSON);
    RegistrySupplier<StandType2> D4C = register("d4c", JEntityTypeRegistry.D4C);
    RegistrySupplier<StandType2> CREAM = register("cream", JEntityTypeRegistry.CREAM);
    RegistrySupplier<StandType2> KILLER_QUEEN = register("killerqueen", JEntityTypeRegistry.KILLER_QUEEN);
    RegistrySupplier<StandType2> WHITE_SNAKE = register("whitesnake", JEntityTypeRegistry.WHITE_SNAKE);
    RegistrySupplier<StandType2> SILVER_CHARIOT = register("silverchariot", JEntityTypeRegistry.SILVER_CHARIOT);
    RegistrySupplier<StandType2> MAGICIANS_RED = register("mr", JEntityTypeRegistry.MAGICIANS_RED);
    RegistrySupplier<StandType2> THE_FOOL = register("thefool", JEntityTypeRegistry.THE_FOOL);
    RegistrySupplier<StandType2> GOLD_EXPERIENCE = register("goldexperience", JEntityTypeRegistry.GOLD_EXPERIENCE);
    RegistrySupplier<StandType2> HIEROPHANT_GREEN = register("hierophantgreen", JEntityTypeRegistry.HIEROPHANT_GREEN);
    RegistrySupplier<StandType2> THE_SUN = register("the_sun", JEntityTypeRegistry.THE_SUN);
    RegistrySupplier<StandType2> PURPLE_HAZE = register("purple_haze", JEntityTypeRegistry.PURPLE_HAZE);
    RegistrySupplier<StandType2> C_MOON = register("cmoon", JEntityTypeRegistry.C_MOON);
    RegistrySupplier<StandType2> MADE_IN_HEAVEN = register("mih", JEntityTypeRegistry.MADE_IN_HEAVEN);
    RegistrySupplier<StandType2> THE_WORLD_OVER_HEAVEN = register("twoh", JEntityTypeRegistry.THE_WORLD_OVER_HEAVEN);
    RegistrySupplier<StandType2> KILLER_QUEEN_BITES_THE_DUST = register("kqbtd", JEntityTypeRegistry.KILLER_QUEEN_BITES_THE_DUST);
    RegistrySupplier<StandType2> GOLD_EXPERIENCE_REQUIEM = register("ger", JEntityTypeRegistry.GER);
    RegistrySupplier<StandType2> STAR_PLATINUM_THE_WORLD = register("sptw", JEntityTypeRegistry.SPTW);
    RegistrySupplier<StandType2> PURPLE_HAZE_DISTORTION = register("purple_haze_distortion", JEntityTypeRegistry.PURPLE_HAZE_DISTORTION);
    RegistrySupplier<StandType2> HORUS = register("horus", JEntityTypeRegistry.HORUS);
    RegistrySupplier<StandType2> CINDERELLA = register("cinderella", JEntityTypeRegistry.CINDERELLA);
    RegistrySupplier<StandType2> METALLICA = register("metallica", JEntityTypeRegistry.METALLICA);
    RegistrySupplier<StandType2> THE_HAND = register("the_hand", JEntityTypeRegistry.THE_HAND);

    /**
     * Internal use only.
     * Registers a new StandType with the given name and entity type supplier.
     * <p>
     * Add-on mods should make their own DeferredRegister and register their own StandTypes
     * with their own namespace using that instead of this method.
     * @param name The name (path part of the ResourceLocation) of the StandType.
     * @param entityTypeSupplier The supplier of the EntityType for the StandEntity.
     *                   Gotten by registering the stand entity type separately.
     * @return A Supplier that provides the registered StandType instance.
     */
    static <E extends StandEntity<?, ?>> RegistrySupplier<StandType2> register(String name, Supplier<EntityType<E>> entityTypeSupplier) {
        ResourceLocation id = JCraft.id(name);
        // The '::get' part is to satisfy the compiler (generic type parameter issue).
        StandType2 standType = StandType2.of(id, entityTypeSupplier::get);
        return STAND_TYPE_REGISTRY.register(name, () -> standType);
    }
}
