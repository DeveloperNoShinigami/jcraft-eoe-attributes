package net.arna.jcraft.client.argumenttype;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import lombok.RequiredArgsConstructor;
import net.arna.jcraft.api.pose.PoseModifiers;
import net.arna.jcraft.api.pose.modifier.IPoseModifier;

@RequiredArgsConstructor(staticName = "pose")
public class PoseModifierArgument implements ArgumentType<PoseModifierArgument.ParsedPose> {
    private static final SimpleCommandExceptionType INVALID_POSE_MODIFIER =
            new SimpleCommandExceptionType(() -> "Invalid pose modifier");

    @Override
    public ParsedPose parse(StringReader reader) throws CommandSyntaxException {
        String input = reader.getRemaining();
        int c = reader.getCursor();
        reader.setCursor(c + input.length());

        try {
            return new ParsedPose(input, PoseModifiers.parse(input));
        } catch (IllegalArgumentException e) {
            reader.setCursor(c);
            throw INVALID_POSE_MODIFIER.createWithContext(reader);
        }
    }

    public record ParsedPose(String raw, IPoseModifier pose) {}
}
