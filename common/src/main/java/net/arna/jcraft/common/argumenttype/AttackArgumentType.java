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
import lombok.RequiredArgsConstructor;
import net.arna.jcraft.api.attack.enums.MoveClass;
import net.minecraft.network.chat.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Getter
@RequiredArgsConstructor(staticName = "attack")
public class AttackArgumentType implements ArgumentType<MoveClass> {
    private static final SimpleCommandExceptionType NOT_FOUND = new SimpleCommandExceptionType(Component.literal("That attack type does not exist"));
    private static final Map<String, MoveClass> suggestions = Arrays.stream(MoveClass.values()).collect(
            ImmutableMap.toImmutableMap(type -> type.name().toLowerCase(Locale.ROOT), type -> type));
    private final Collection<String> examples = ImmutableList.of("RANDOM", "LIGHT", "BARRAGE", "UTILITY");

    @Override
    public MoveClass parse(final StringReader reader) throws CommandSyntaxException {
        final String name = reader.readUnquotedString();

        if ("RANDOM".equals(name.toUpperCase(Locale.ROOT))) {
            return MoveClass.randomMoveType();
        }
        try {
            return MoveClass.valueOf(name.toUpperCase(Locale.ROOT));
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
                .map(MoveClass::name)
                .forEach(builder::suggest);

        if ("random".startsWith(input)) {
            builder.suggest("RANDOM");
        }

        return builder.buildFuture();
    }
}
