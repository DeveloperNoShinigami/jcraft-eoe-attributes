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
import net.arna.jcraft.api.stand.StandType;
import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@NoArgsConstructor(staticName = "stand")
public class StandArgumentType implements ArgumentType<StandType> {
    public static final SimpleCommandExceptionType NOT_FOUND = new SimpleCommandExceptionType(Component.literal("That stand was not found"));
    @Getter // implements ArgumentType#getExamples()
    private final Collection<String> examples = ImmutableList.of("jcraft:made_in_heaven", "magicians_red", "jcraft:d4c");

    @Override
    public StandType parse(final StringReader reader) throws CommandSyntaxException {
        final StandType type = JRegistries.parseRegistryEntry(JRegistries.STAND_TYPE_REGISTRY, reader,
                t -> t.getData().isObtainable());
        if (type == null) {
            throw NOT_FOUND.createWithContext(reader);
        }

        return type;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return JRegistries.listSuggestions(JRegistries.STAND_TYPE_REGISTRY, builder,
                type -> type.getData().isObtainable());
    }
}
