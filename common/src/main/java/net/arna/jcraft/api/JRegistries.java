package net.arna.jcraft.api;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.api.spec.SpecType2;
import net.arna.jcraft.api.stand.StandType;
import net.arna.jcraft.common.attack.core.MoveAction;
import net.arna.jcraft.common.attack.core.MoveCondition;
import net.arna.jcraft.api.attack.MoveActionType;
import net.arna.jcraft.api.attack.MoveConditionType;
import net.arna.jcraft.api.attack.MoveType;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * Simple class that holds the registries used by JCraft.
 * <p>
 * Do NOT register your objects to these registries directly, use a DeferredRegister instead.
 * (See example add-on mod and the registries in net.arna.jcraft.registry package).
 */
public class JRegistries {
    private static final RegistrarManager manager = RegistrarManager.get(JCraft.MOD_ID);

    // Registries
    public static final Registrar<StandType> STAND_TYPE_REGISTRY = manager.<StandType>builder(JCraft.id("stand_type"))
            .syncToClients()
            .build();
    public static final Registrar<SpecType2> SPEC_TYPE_REGISTRY = manager.<SpecType2>builder(JCraft.id("spec_type"))
            .syncToClients()
            .build();
    public static final Registrar<MoveType<?>> MOVE_TYPE_REGISTRY = manager.<MoveType<?>>builder(JCraft.id("move_type"))
            .syncToClients()
            .build();
    public static final Registrar<MoveConditionType<?>> MOVE_CONDITION_TYPE_REGISTRY = manager.<MoveConditionType<?>>builder(JCraft.id("move_condition_type"))
            .syncToClients()
            .build();
    public static final Registrar<MoveActionType<?>> MOVE_ACTION_TYPE_REGISTRY = manager.<MoveActionType<?>>builder(JCraft.id("move_action_type"))
            .syncToClients()
            .build();


    // Registry keys
    public static final ResourceKey<Registry<StandType>> STAND_TYPE_REGISTRY_KEY = createKey(STAND_TYPE_REGISTRY);
    public static final ResourceKey<Registry<SpecType2>> SPEC_TYPE_REGISTRY_KEY = createKey(SPEC_TYPE_REGISTRY);
    public static final ResourceKey<Registry<MoveType<?>>> MOVE_TYPE_REGISTRY_KEY = createKey(MOVE_TYPE_REGISTRY);
    public static final ResourceKey<Registry<MoveConditionType<?>>> MOVE_CONDITION_TYPE_REGISTRY_KEY = createKey(MOVE_CONDITION_TYPE_REGISTRY);
    public static final ResourceKey<Registry<MoveActionType<?>>> MOVE_ACTION_TYPE_REGISTRY_KEY = createKey(MOVE_ACTION_TYPE_REGISTRY);

    // Registry codecs
    public static final Codec<StandType> STAND_TYPE_CODEC = createCodec(STAND_TYPE_REGISTRY);
    public static final Codec<SpecType2> SPEC_TYPE_CODEC = createCodec(SPEC_TYPE_REGISTRY);
    public static final Codec<MoveType<?>> MOVE_TYPE_CODEC = createCodec(MOVE_TYPE_REGISTRY);
    public static final Codec<AbstractMove<?, ?>> MOVE_CODEC = MOVE_TYPE_CODEC
            .dispatch("type", AbstractMove::getMoveType, MoveType::getCodec);
    public static final Codec<MoveConditionType<?>> MOVE_CONDITION_TYPE_CODEC = createCodec(MOVE_CONDITION_TYPE_REGISTRY);
    public static final Codec<MoveCondition<?, ?>> MOVE_CONDITION_CODEC = MOVE_CONDITION_TYPE_CODEC
            .dispatch("type", MoveCondition::getType, MoveConditionType::getCodec);
    public static final Codec<MoveActionType<?>> MOVE_ACTION_TYPE_CODEC = createCodec(MOVE_ACTION_TYPE_REGISTRY);
    public static final Codec<MoveAction<?, ?>> MOVE_ACTION_CODEC = MOVE_ACTION_TYPE_CODEC
            .dispatch("type", MoveAction::getType, MoveActionType::getCodec);

    public static void init() {
        // Left empty on purpose. Static initializers will register the registries.
    }

    public static <T> Codec<T> createCodec(Registrar<T> registrar) {
        return ResourceLocation.CODEC.flatXmap(rl -> {
            T t = registrar.get(rl);
            if (t == null) {
                return DataResult.error(() -> "Could not find " + registrar.key().location() + " with id: " + rl);
            }

            return DataResult.success(t);
        }, t -> {
            ResourceLocation rl = registrar.getId(t);
            if (rl == null) {
                return DataResult.error(() -> "Could not find id for " + registrar.key().location() + ": " + t);
            }
            return DataResult.success(rl);
        });
    }

    // For some reason, the Registrar::key() method returns a ResourceKey of
    // something that extends Registry<T>, but not Registry<T> itself, which is problematic.
    private static <T> ResourceKey<Registry<T>> createKey(Registrar<T> registrar) {
        return ResourceKey.createRegistryKey(registrar.key().location());
    }

    /**
     * Utility method to parse a registry entry from a StringReader.
     * @param registry the registry to parse from
     * @param reader the StringReader to parse from
     * @return the parsed registry entry, or null if not found
     * @param <T> the type of the registry entry
     */
    public static <T> @Nullable T parseRegistryEntry(Registrar<T> registry, StringReader reader) {
        int i = reader.getCursor();
        while (reader.canRead() && ResourceLocation.isAllowedInResourceLocation(reader.peek())) {
            reader.skip();
        }
        final String string = reader.getString().substring(i, reader.getCursor());

        String namespace = JCraft.MOD_ID;
        String path = string;
        if (string.contains(":")) {
            final String[] parts = string.split(":", 2);
            namespace = parts[0];
            path = parts[1];
        }

        ResourceLocation id = new ResourceLocation(namespace, path);
        return registry.get(id);
    }

    /**
     * Utility method to list suggestions for a given registry.
     * @param registry the registry to list suggestions for
     * @param builder the SuggestionsBuilder to submit suggestions to
     * @return a CompletableFuture that will complete with the suggestions
     */
    public static CompletableFuture<Suggestions> listSuggestions(Registrar<?> registry, SuggestionsBuilder builder) {
        final String input = builder.getRemainingLowerCase();

        registry.getIds().stream()
                .filter(id -> JCraft.MOD_ID.equals(id.getNamespace()) && id.getPath().startsWith(input) ||
                        id.toString().startsWith(input))
                .map(ResourceLocation::toString)
                .forEach(builder::suggest);

        return builder.buildFuture();
    }
}
