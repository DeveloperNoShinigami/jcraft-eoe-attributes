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
import net.arna.jcraft.api.JRegistries;
import net.arna.jcraft.api.stand.StandType;
import net.minecraft.network.chat.Component;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@NoArgsConstructor(staticName = "stand")
public class StandArgumentType implements ArgumentType<StandType> {
    public static final SimpleCommandExceptionType NOT_FOUND = new SimpleCommandExceptionType(Component.literal("That stand was not found"));
    private static final Map<String, StandType> suggestions = JRegistries.STAND_TYPE_REGISTRY.entrySet().stream()
            .collect(ImmutableMap.toImmutableMap(entry -> entry.getKey().location().getPath().toLowerCase(Locale.ROOT).replaceAll("_", ""), Map.Entry::getValue));
    @Getter // implements ArgumentType#getExamples()
    private final Collection<String> examples = ImmutableList.of("MADE_IN_HEAVEN", "C_MOON", "GER");

    @Override
    public StandType parse(final StringReader reader) throws CommandSyntaxException {
        final String name = reader.readUnquotedString();
        final StandType type = suggestions.get(name);
        if (type == null) {
            throw NOT_FOUND.createWithContext(reader);
        }
        return type;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        final String input = builder.getRemainingLowerCase().replaceAll("_", "");
        suggestions.keySet().stream()
                .filter(standType -> standType.startsWith(input))
                .forEach(builder::suggest);
        return builder.buildFuture();
    }
}
