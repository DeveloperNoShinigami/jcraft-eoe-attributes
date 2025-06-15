package net.arna.jcraft.client.argumenttype;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import lombok.RequiredArgsConstructor;
import net.arna.jcraft.api.pose.ModelType;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor(staticName = "modelType")
public class ModelTypeArgument implements ArgumentType<ModelType<?>> {
    @Override
    public ModelType<?> parse(StringReader reader) throws CommandSyntaxException {
        String s = reader.readString();
        ModelType<?> modelType = ModelType.fromName(s);
        if (modelType == null) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(reader);
        }

        return modelType;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        String input = builder.getRemainingLowerCase();
        for (Map.Entry<String, ModelType<?>> entry : ModelType.getAllModelTypes().entrySet()) {
            String name = entry.getKey();
            if (name.toLowerCase(Locale.ROOT).startsWith(input)) {
                builder.suggest(name);
            }
        }

        return builder.buildFuture();
    }
}
