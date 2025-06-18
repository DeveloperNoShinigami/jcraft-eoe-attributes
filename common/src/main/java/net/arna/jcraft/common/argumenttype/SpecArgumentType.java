package net.arna.jcraft.common.argumenttype;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.arna.jcraft.api.JRegistries;
import net.arna.jcraft.api.registry.JSpecTypeRegistry;
import net.arna.jcraft.api.spec.SpecType;
import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@NoArgsConstructor(staticName = "spec")
public class SpecArgumentType implements ArgumentType<SpecType> {
    private static final SimpleCommandExceptionType NOT_FOUND = new SimpleCommandExceptionType(Component.literal("That stand was not found"));
    @Getter // implements ArgumentType#getExamples()
    private final Collection<String> examples = ImmutableList.of("jcraft:brawler", "anubis");

    @Override
    public SpecType parse(final StringReader reader) throws CommandSyntaxException {
        SpecType specType = JRegistries.parseRegistryEntry(JRegistries.SPEC_TYPE_REGISTRY, reader, s -> true);
        if (specType == null) {
            throw NOT_FOUND.createWithContext(reader);
        }
        return specType;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return JRegistries.listSuggestions(JRegistries.SPEC_TYPE_REGISTRY, builder, spec -> spec != JSpecTypeRegistry.NONE.get());
    }
}
