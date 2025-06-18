package net.arna.jcraft.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.api.spec.SpecType;
import net.arna.jcraft.common.argumenttype.SpecArgumentType;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;

public class SetSpecCommand {
    public static void register(final CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("spec")
                .then(Commands.literal("set")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("players", EntityArgument.players())
                                .then(Commands.argument("spec", SpecArgumentType.spec())
                                        .executes(SetSpecCommand::run)
                                )
                        )
                )
        );
    }

    public static int run(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        try {
            final SpecType specType = context.getArgument("spec", SpecType.class);
            final Collection<? extends Player> targets = EntityArgument.getPlayers(context, "players");

            if (targets.isEmpty()) {
                return 0;
            }
            for (Player player : targets) {
                JComponentPlatformUtils.getSpecData(player).setType(specType);
            }
        } catch (Exception e) {
            JCraft.LOGGER.error("Failed to set spec", e);
            return 0;
        }

        return 1;
    }
}
