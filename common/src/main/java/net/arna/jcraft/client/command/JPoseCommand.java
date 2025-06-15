package net.arna.jcraft.client.command;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.architectury.event.events.client.ClientCommandRegistrationEvent;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.api.pose.ModelType;
import net.arna.jcraft.api.pose.PoseModifiers;
import net.arna.jcraft.api.pose.modifier.IPoseModifier;
import net.arna.jcraft.api.pose.modifier.PoseModifierGroup;
import net.arna.jcraft.client.argumenttype.ModelTypeArgument;
import net.arna.jcraft.client.argumenttype.PoseModifierArgument;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.List;

public class JPoseCommand {
    private static final List<PoseModifierArgument.ParsedPose> POSES = new ArrayList<>();
    private static IPoseModifier builtPose = IPoseModifier.EMPTY;
    private static ModelType<?> currentType = ModelType.HUMANOID;
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    static {
        ClientLifecycleEvent.CLIENT_LEVEL_LOAD.register(level -> {
            POSES.clear();
            builtPose = IPoseModifier.EMPTY;
        });
    }

    public static void register(CommandDispatcher<ClientCommandRegistrationEvent.ClientCommandSourceStack> dispatcher) {
        dispatcher.register(literal("jpose")
                .then(literal("help")
                        .executes(JPoseCommand::runHelp))
                .then(literal("add")
                        .then(argument("pose", PoseModifierArgument.pose())
                                .executes(JPoseCommand::runAdd)))
                .then(literal("insert")
                        .then(argument("index", IntegerArgumentType.integer(1))
                                .executes(JPoseCommand::runInsert)))
                .then(literal("remove")
                        .then(argument("index", IntegerArgumentType.integer(1))
                                .executes(JPoseCommand::runRemove)))
                .then(literal("replace")
                        .then(argument("pose", PoseModifierArgument.pose())
                                .executes(JPoseCommand::runReplace)))
                .then(literal("list")
                        .executes(JPoseCommand::runList))
                .then(literal("model")
                        .then(literal("set")
                                .then(argument("model", ModelTypeArgument.modelType())
                                        .executes(JPoseCommand::runSetModel)))
                        .then(literal("get")
                                .executes(JPoseCommand::runGetModel)))
                .then(literal("clear")
                        .executes(JPoseCommand::runClear))
                .then(literal("export")
                        .then(literal("raw")
                                .executes(ctx -> runExport(ctx, true)))
                        .then(literal("json")
                                .executes(ctx -> runExport(ctx, false)))));
    }

    private static int runHelp(CommandContext<ClientCommandRegistrationEvent.ClientCommandSourceStack> ctx) {
        final String help = """
                /jpose: an in-game pose designer for JCraft.
                Definitions:
                - Pose: A set of transformations applied to a model.
                  A pose consists of the following components:
                  - A model part (usually "head", "leftArm", "rightLeg", etc.)
                  - A property (x, y, z, xRot, yRot, zRot, xScale, yScale, zScale)
                  - An operator (one of =, +=, -=, *=, /=)
                    = sets the property to a constant value while the others modify the current value.
                  - A value (a (floating-point) number, optionally followed by "deg" for degrees)
                    Make sure to add "deg" when adding degrees to rotation properties as rotations are in radians by default.
                  - Optionally a comment ('//' followed by any text)
                
                  Examples:
                  - leftArm.zRot += 110deg // Adds 110 degrees to the z rotation of the left arm.
                  - rightLeg.xScale = 1.5
                  - leftLeg.x = 5 // dismembered leg
                - Model type: the type of model the pose will be applied to.
                  'humanoid' by default, this is currently also the only supported type.
                
                Commands:
                - /jpose add <pose>: Adds a pose modifier to the end of the list.
                - /jpose insert <index> <pose>: Inserts a pose modifier at the specified index.
                - /jpose remove <index>: Removes the pose modifier at the specified index.
                - /jpose replace <pose>: Replaces the last pose modifier with the new one.
                - /jpose list: Lists all current pose modifiers.
                - /jpose model set <model>: Sets the current model type for the pose modifiers.
                  All entities using this model type will be affected by the pose modifiers.
                - /jpose model get: Gets the current model type.
                - /jpose clear: Clears all pose modifiers.
                - /jpose export raw: Exports the current pose modifiers as raw code to the clipboard.
                - /jpose export json: Exports the current pose modifiers as a JSON asset to the clipboard.
                """;

        ctx.getSource().arch$sendSuccess(() -> Component.literal(help), false);
        return 1;
    }

    private static int runAdd(CommandContext<ClientCommandRegistrationEvent.ClientCommandSourceStack> ctx) {
        if (checkNotSingleplayer(ctx.getSource())) {
            return 0;
        }

        ClientCommandRegistrationEvent.ClientCommandSourceStack source = ctx.getSource();
        PoseModifierArgument.ParsedPose pose = ctx.getArgument("pose", PoseModifierArgument.ParsedPose.class);
        return tryInsert(source, pose, POSES.size());
    }

    private static int runInsert(CommandContext<ClientCommandRegistrationEvent.ClientCommandSourceStack> ctx) {
        if (checkNotSingleplayer(ctx.getSource())) {
            return 0;
        }

        ClientCommandRegistrationEvent.ClientCommandSourceStack source = ctx.getSource();
        PoseModifierArgument.ParsedPose pose = ctx.getArgument("pose", PoseModifierArgument.ParsedPose.class);
        int index = ctx.getArgument("index", Integer.class) - 1; // Convert to zero-based index

        if (index < 0 || index > POSES.size()) {
            source.arch$sendFailure(Component.literal("Index out of bounds: " + index)
                    .withStyle(ChatFormatting.RED));
            return 0;
        }

        return tryInsert(source, pose, index);
    }

    private static int tryInsert(ClientCommandRegistrationEvent.ClientCommandSourceStack source, PoseModifierArgument.ParsedPose pose,
                                     int index) {
        if (currentType == null) {
            source.arch$sendFailure(Component.literal("No model type set. Use '/jpose model set <model>' to set one.")
                    .withStyle(ChatFormatting.RED));
            return 0;
        }

        if (!pose.pose().isModelSupported(currentType)) {
            source.arch$sendFailure(Component.literal("Pose modifier is not compatible with the current model type: " + currentType.getName())
                    .withStyle(ChatFormatting.RED));
            return 0;
        }

        POSES.add(index, pose);
        rebuildModifier();

        source.arch$sendSuccess(() -> Component.literal("Pose modifier added: " + pose.raw()), false);
        return 1;
    }

    private static int runRemove(CommandContext<ClientCommandRegistrationEvent.ClientCommandSourceStack> ctx) {
        if (checkNotSingleplayer(ctx.getSource())) {
            return 0;
        }

        ClientCommandRegistrationEvent.ClientCommandSourceStack source = ctx.getSource();
        int index = ctx.getArgument("index", Integer.class) - 1; // Convert to zero-based index

        if (index < 0 || index >= POSES.size()) {
            source.arch$sendFailure(Component.literal("Index out of bounds: " + index)
                    .withStyle(ChatFormatting.RED));
            return 0;
        }

        PoseModifierArgument.ParsedPose removedPose = POSES.remove(index);
        rebuildModifier();

        source.arch$sendSuccess(() -> Component.literal("Pose modifier removed: " + removedPose.raw()), false);
        return 1;
    }

    private static int runReplace(CommandContext<ClientCommandRegistrationEvent.ClientCommandSourceStack> ctx) {
        if (checkNotSingleplayer(ctx.getSource())) {
            return 0;
        }

        ClientCommandRegistrationEvent.ClientCommandSourceStack source = ctx.getSource();
        PoseModifierArgument.ParsedPose pose = ctx.getArgument("pose", PoseModifierArgument.ParsedPose.class);

        int res = tryInsert(source, pose, POSES.size());
        if (res == 0) {
            return 0; // Failed to insert, likely due to model type issues
        }

        // Remove second to last pose if it exists
        PoseModifierArgument.ParsedPose removed = null;
        int index = POSES.size() - 2; // Last added pose is at index size - 1
        if (index >= 0) {
            removed = POSES.remove(index);
        }

        rebuildModifier();

        Component msg = Component.literal(removed == null ?
                "Pose modifier added: " + pose.raw() :
                "Replaced pose modifier '" + removed.raw() + "' with '" + pose.raw() + "'.");
        source.arch$sendSuccess(() -> msg, false);
        return 1;
    }

    private static int runList(CommandContext<ClientCommandRegistrationEvent.ClientCommandSourceStack> ctx) {
        if (checkNotSingleplayer(ctx.getSource())) {
            return 0;
        }

        ClientCommandRegistrationEvent.ClientCommandSourceStack source = ctx.getSource();

        if (POSES.isEmpty()) {
            source.arch$sendFailure(Component.literal("No pose modifiers set."));
            return 0;
        }

        MutableComponent comp = Component.empty()
                .append("Current pose modifiers:");
        for (int i = 0; i < POSES.size(); i++) {
            PoseModifierArgument.ParsedPose pose = POSES.get(i);
            comp.append(Component.empty()
                    .append("\n")
                    .append((i + 1) + ". ")
                    .append(Component.literal(pose.raw()).withStyle(pose.pose().isModelSupported(currentType) ?
                            Style.EMPTY : Style.EMPTY.withColor(ChatFormatting.RED))));
        }
        source.arch$sendSuccess(() -> comp, false);
        return 1;
    }

    private static int runSetModel(CommandContext<ClientCommandRegistrationEvent.ClientCommandSourceStack> ctx) {
        if (checkNotSingleplayer(ctx.getSource())) {
            return 0;
        }

        ClientCommandRegistrationEvent.ClientCommandSourceStack source = ctx.getSource();
        ModelType<?> type = ctx.getArgument("model", ModelType.class);

        currentType = type;
        rebuildModifier();

        if (!POSES.stream().allMatch(pose -> pose.pose().isModelSupported(currentType))) {
            source.arch$sendFailure(Component.literal("Some pose modifiers are not compatible with the selected model type. " +
                    "Check the problematic poses with '/jpose list'.").withStyle(ChatFormatting.RED));
        }

        source.arch$sendSuccess(() -> Component.literal("Model type set to " + type.getName()), false);
        return 1;
    }

    private static int runGetModel(CommandContext<ClientCommandRegistrationEvent.ClientCommandSourceStack> ctx) {
        if (checkNotSingleplayer(ctx.getSource())) {
            return 0;
        }

        ClientCommandRegistrationEvent.ClientCommandSourceStack source = ctx.getSource();

        if (currentType == null) {
            source.arch$sendFailure(Component.literal("No model type set."));
            return 0;
        }

        source.arch$sendSuccess(() -> Component.literal("Current model type: " + currentType.getName()), false);
        return 1;
    }

    private static int runClear(CommandContext<ClientCommandRegistrationEvent.ClientCommandSourceStack> ctx) {
        if (checkNotSingleplayer(ctx.getSource())) {
            return 0;
        }

        ClientCommandRegistrationEvent.ClientCommandSourceStack source = ctx.getSource();

        if (POSES.isEmpty()) {
            source.arch$sendFailure(Component.literal("No pose modifier set."));
            return 0;
        }

        builtPose = IPoseModifier.EMPTY;
        POSES.clear();

        source.arch$sendSuccess(() -> Component.literal("Pose modifier reset."), false);
        return 1;
    }

    private static int runExport(CommandContext<ClientCommandRegistrationEvent.ClientCommandSourceStack> ctx, boolean raw) {
        if (checkNotSingleplayer(ctx.getSource())) {
            return 0;
        }

        ClientCommandRegistrationEvent.ClientCommandSourceStack source = ctx.getSource();

        if (POSES.isEmpty()) {
            source.arch$sendFailure(Component.literal("No pose modifier set."));
            return 0;
        }

        String output;

        // Raw is for when you're making an addon in which case you need the raw code.
        if (raw) {
            output = POSES.stream()
                    .map(PoseModifierArgument.ParsedPose::raw)
                    .reduce("", (a, b) -> a + ";\n" + b)
                    .substring(2); // Remove leading ";\n"
        } else {
            // JSON asset export is for resource pack makers.
            DataResult<JsonElement> res = PoseModifiers.CODEC.encodeStart(JsonOps.INSTANCE, builtPose);
            if (res.error().isPresent()) {
                source.arch$sendFailure(Component.literal("Failed to export pose modifier: " + res.error().get().message())
                        .withStyle(ChatFormatting.RED));
                return 0;
            }

            JsonElement json = res.result().orElseThrow();
            output = GSON.toJson(json);
        }

        try {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(output), null);
        } catch (Exception e) {
            JCraft.LOGGER.error("Failed to copy pose modifier to clipboard", e);
            source.arch$sendFailure(Component.literal("Failed to copy pose modifier to clipboard: " + e.getMessage())
                    .withStyle(ChatFormatting.RED));
            return 0;
        }

        source.arch$sendSuccess(() -> Component.literal("Pose modifier code has been copied to your clipboard."), false);
        return 1;
    }

    public static boolean hasPose(ModelType<?> type) {
        return type == currentType && !POSES.isEmpty();
    }

    public static IPoseModifier getPose() {
        return builtPose;
    }

    private static void rebuildModifier() {
        if (currentType == null) {
            builtPose = IPoseModifier.EMPTY;
            return;
        }

        List<IPoseModifier> filteredMods = POSES.stream()
                .map(PoseModifierArgument.ParsedPose::pose)
                .filter(pose -> pose.isModelSupported(currentType))
                .toList();
        builtPose = PoseModifierGroup.builder()
                .modifiers(filteredMods)
                .build();
    }

    private static boolean checkNotSingleplayer(ClientCommandRegistrationEvent.ClientCommandSourceStack source) {
        if (!Minecraft.getInstance().isSingleplayer()) {
            source.arch$sendFailure(Component.literal("This command can only be used in singleplayer.")
                    .withStyle(ChatFormatting.RED));
            return true;
        }

        return false;
    }

    private static @NotNull LiteralArgumentBuilder<ClientCommandRegistrationEvent.ClientCommandSourceStack> literal(String literal) {
        return LiteralArgumentBuilder.literal(literal);
    }

    private static <T> @NotNull RequiredArgumentBuilder<ClientCommandRegistrationEvent.ClientCommandSourceStack, T> argument(
            String name, ArgumentType<T> argumentType) {
        return RequiredArgumentBuilder.argument(name, argumentType);
    }
}
