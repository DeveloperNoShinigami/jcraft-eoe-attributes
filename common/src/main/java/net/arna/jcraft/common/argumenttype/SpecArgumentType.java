package net.arna.jcraft.common.argumenttype;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.arna.jcraft.common.spec.SpecType;
import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@NoArgsConstructor(staticName = "spec")
public class SpecArgumentType implements ArgumentType<SpecType> {
    private static final SimpleCommandExceptionType NOT_FOUND = new SimpleCommandExceptionType(Component.literal("That stand was not found"));
    private static final Map<String, SpecType> suggestions = SpecType.getAllSpecTypes().stream()
            .collect(ImmutableMap.toImmutableMap(type -> type.getInternalName().replaceAll("_", ""), type -> type));
    @Getter // implements ArgumentType#getExamples()
    private final Collection<String> examples = ImmutableList.of("BRAWLER", "ANUBIS");

    @Override
    public SpecType parse(final StringReader reader) throws CommandSyntaxException {
        final String name = reader.readUnquotedString();
        try {
            return SpecType.valueOf(name.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw NOT_FOUND.createWithContext(reader);
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        final String input = builder.getRemainingLowerCase().replaceAll("_", "");
        suggestions.entrySet().stream()
                .filter(e -> e.getKey().startsWith(input))
                .map(Map.Entry::getValue)
                .map(SpecType::name)
                .forEach(builder::suggest);
        return builder.buildFuture();
    }
}
