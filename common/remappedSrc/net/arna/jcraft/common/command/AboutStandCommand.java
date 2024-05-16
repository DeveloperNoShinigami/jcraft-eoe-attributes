package net.arna.jcraft.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.attack.core.MoveMap;
import net.arna.jcraft.common.attack.core.MoveType;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.util.JUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;

//todo: replace with a Patchouli in-game wikipedia
@Deprecated(forRemoval = true)
public class AboutStandCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("stand")
                .then(Commands.literal("about").executes(AboutStandCommand::run)));
    }

    public static int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        StandEntity<?, ?> stand = JUtils.getStand(player);

        if (stand == null) {
            context.getSource().sendSuccess(() -> Component.translatable("jcraft.commands.error.nostand"), true);
            return 0;
        }

        MutableComponent resp = Component.empty();

        // Name
        resp.append(Component.empty()
                .append(Component.literal("Name: "))
                .append(stand.getName().copy().withStyle(ChatFormatting.YELLOW))
                .append(Component.literal("\n")));

        // Description
        resp.append(Component.translatable(String.format("entity.%s.%s%s.info.desc", JCraft.MOD_ID, stand.getStandType().getNameKey(), stand.getModeOrdinal() == 0 ? "" : Integer.toString(stand.getModeOrdinal())))
                        .withStyle(ChatFormatting.GREEN))
                .append(Component.literal("\n"));

        // Pros & Cons
        MutableComponent pros = Component.empty()
                .append(Component.literal("PROS:").withStyle(ChatFormatting.DARK_AQUA))
                .append(Component.literal("\n"));
        for (int p = 1; p <= stand.getProCount(); p++) {
            pros
                    .append(Component.literal("● ").withStyle(ChatFormatting.DARK_AQUA))
                    .append(Component.translatable(String.format("entity.%s.%s.info.pro%d", JCraft.MOD_ID, stand.getStandType().getNameKey(), p)))
                    .append(Component.literal("\n"));
        }
        resp.append(pros);

        MutableComponent cons = Component.empty()
                .append(Component.literal("CONS:").withStyle(ChatFormatting.DARK_RED))
                .append(Component.literal("\n"));
        for (int c = 1; c <= stand.getConCount(); c++) {
            cons
                    .append(Component.literal("● ").withStyle(ChatFormatting.DARK_RED))
                    .append(Component.translatable(String.format("entity.%s.%s.info.con%d", JCraft.MOD_ID, stand.getStandType().getNameKey(), c)))
                    .append(Component.literal("\n"));
        }
        resp.append(cons);

        resp.append(Component.literal("\n"));

        // Moves
        MutableComponent moves = Component.empty()
                .append(Component.literal("MOVES:").withStyle(ChatFormatting.DARK_GREEN))
                .append(Component.literal("\n"));

        MoveMap<?, ?> moveMap = stand.getMoveMap();
        for (MoveType type : MoveType.values()) {
            for (MoveMap.Entry<?, ?> entry : moveMap.getEntries(type)) {
                // Move itself
                appendMove(entry, moves, Component.literal("● ").withStyle(ChatFormatting.GREEN), false);

                // Crouching variant
                if (entry.getCrouchingVariant() != null) {
                    appendMove(entry.getCrouchingVariant(), moves, Component.literal("  ● CROUCHING ").withStyle(ChatFormatting.DARK_AQUA), true);
                }

                // Aerial variant
                if (entry.getAerialVariant() != null) {
                    appendMove(entry.getAerialVariant(), moves, Component.literal("  ● AERIAL ").withStyle(ChatFormatting.GOLD), true);
                }
            }
        }
        resp.append(moves);

        // Free Space
        if (stand.freespace != null) {
            resp.append(Component.literal("\n"));
            resp.append(Component.literal(stand.freespace));
        }

        context.getSource().sendSuccess(() -> resp, false);
        return 1;
    }

    public static void appendMove(MoveMap.Entry<?, ?> entry, MutableComponent moves, MutableComponent base, boolean isVariant) {
        moves
                .append(base
                        .append(isVariant ? Component.empty() : Component.empty()
                                .append(entry.getType().getFriendlyName())
                                .append(Component.empty()
                                        .append(Component.literal(" ("))
                                        .append(entry.getType().getKey().copy().withStyle(ChatFormatting.AQUA))
                                        .append(Component.literal(")")))))
                .append(Component.literal(" - "))
                .append(entry.getMove().getName().copy().withStyle(ChatFormatting.DARK_PURPLE))
                .append(Component.literal(" - "))
                .append(entry.getMove().getDescription())
                .append(Component.literal("\n"));
    }
}
