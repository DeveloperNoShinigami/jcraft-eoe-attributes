package net.arna.jcraft.api.pose;

import com.mojang.serialization.Codec;
import net.arna.jcraft.api.pose.modifier.IPoseModifier;
import net.arna.jcraft.api.pose.modifier.LevitationPoseModifier;
import net.arna.jcraft.api.pose.modifier.CustomPoseModifier;
import net.arna.jcraft.api.pose.modifier.PoseModifierGroup;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PoseModifiers {
    private static final Pattern MODIFIER_PATTERN = Pattern.compile(
            "([a-zA-Z_\\d]+)" + // Model part
                    "\\.(x|y|z|x_?Rot|y_?Rot|z_?Rot|x_?Scale|y_?Scale|z_?Scale)" + // Property
                    "\\s*(=|\\+=|-=|\\*=|/=)\\s*" + // Operator
                    "((?:-?|\\+?)\\d+(?:\\.\\d+)?)\\s*(deg)?$", // Integer or decimal number with optional 'deg' suffix
            Pattern.CASE_INSENSITIVE);
    private static final Map<String, Codec<? extends IPoseModifier>> modifiers = new HashMap<>();
    public static Codec<IPoseModifier> CODEC = Codec.STRING.dispatch(IPoseModifier::getId, modifiers::get);

    public static void register(final String id, final Codec<? extends IPoseModifier> codec) {
        if (modifiers.containsKey(id)) {
            throw new IllegalArgumentException("Modifier with id " + id + " is already registered.");
        }
        modifiers.put(id, codec);
    }

    /**
     * Parses a pose modifier from a string.
     * The string is expected to be java-like code that would modify a pose.
     * <p>
     * For examples, check {@link net.arna.jcraft.common.entity.stand.StarPlatinumEntity#POSE StarPlatinumEntity.POSE} or
     * {@link net.arna.jcraft.common.entity.stand.KingCrimsonEntity#POSE KingCrimson.POSE}.
     * @param modifiers The string containing the modifiers.
     * @param conditions Optional conditions to apply to the modifier.
     *                   Will be applied to the whole group if 'modifiers' contains more than one modifier.
     * @return An instance of {@link IPoseModifier} that represents the parsed modifiers.
     */
    public static IPoseModifier parse(final String modifiers, final ModifierCondition... conditions) {
        if (modifiers.isEmpty()) {
            return IPoseModifier.EMPTY;
        }

        // Split by semicolon to get individual modifiers
        String[] parts = modifiers.split(";");

        if (parts.length == 1) {
            // Single modifier, parse it directly
            return parseSingle(parts[0].trim(), conditions);
        }

        // Multiple modifiers, create a group
        List<IPoseModifier> parsedModifiers = Arrays.stream(parts)
                .map(PoseModifiers::parseSingle)
                .filter(Objects::nonNull)
                .toList();
        if (parsedModifiers.isEmpty()) {
            return IPoseModifier.EMPTY; // No valid modifiers found
        }

        // Create a group modifier with the parsed modifiers and conditions
        return PoseModifierGroup.builder()
                .conditions(List.of(conditions))
                .modifiers(parsedModifiers)
                .build();
    }

    private static IPoseModifier parseSingle(String modifier, final ModifierCondition... conditions) {
        modifier = modifier.split("//")[0]; // Remove comments
        if (modifier.trim().isEmpty()) {
            return null; // Ignore empty modifiers
        }

        Matcher matcher = MODIFIER_PATTERN.matcher(modifier.trim());
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid modifier format: " + modifier);
        }

        // Extract parts from the regex groups
        final String rawPart = matcher.group(1); // Model part
        final String rawProp = matcher.group(2); // Property
        final String rawOp = matcher.group(3); // Operator
        final String valueStr = matcher.group(4); // Value
        final boolean isDeg = matcher.group(5) != null; // Is value in degrees

        // Parse components
        final ModelPartGetter part = new ModelPartGetter(rawPart);

        final ModelPartProperty prop = switch (rawProp.toLowerCase(Locale.ROOT)) {
            case "x" -> ModelPartProperty.X;
            case "y" -> ModelPartProperty.Y;
            case "z" -> ModelPartProperty.Z;
            case "xrot", "x_rot" -> ModelPartProperty.X_ROT;
            case "yrot", "y_rot" -> ModelPartProperty.Y_ROT;
            case "zrot", "z_rot" -> ModelPartProperty.Z_ROT;
            case "xscale", "x_scale" -> ModelPartProperty.X_SCALE;
            case "yscale", "y_scale" -> ModelPartProperty.Y_SCALE;
            case "zscale", "z_scale" -> ModelPartProperty.Z_SCALE;
            default -> throw new IllegalArgumentException("Unknown property: " + rawProp);
        };

        final ModifierOperation operation = switch (rawOp) {
            case "=" -> ModifierOperation.SET;
            case "+=", "-=" -> ModifierOperation.ADD;
            case "*=", "/=" -> ModifierOperation.MULTIPLY;
            default -> throw new IllegalArgumentException("Unknown operation: " + rawOp);
        };
        final boolean invertOp = "-=".equals(rawOp) || "/=".equals(rawOp);

        float value = Float.parseFloat(valueStr);
        if (isDeg) {
            value *= (float) Math.PI / 180; // Convert degrees to radians
        }

        if (invertOp) {
            // Invert the value for subtraction or division
            value = operation == ModifierOperation.ADD ? -value : 1 / value;
        }

        return new CustomPoseModifier(List.of(conditions), operation, part, prop, value);
    }

    public static void register() {
        // Register built-in modifiers
        // Add-on mods should call #register to add their own modifiers.
        register("empty", Codec.unit(IPoseModifier.EMPTY));
        register(PoseModifierGroup.ID, PoseModifierGroup.CODEC);
        register(CustomPoseModifier.ID, CustomPoseModifier.CODEC);
        register(LevitationPoseModifier.ID, LevitationPoseModifier.CODEC);
    }
}
