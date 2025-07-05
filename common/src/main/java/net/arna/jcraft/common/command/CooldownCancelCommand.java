package net.arna.jcraft.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;

public class CooldownCancelCommand {
    public static void register(final CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("cooldown")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("cancel")
                                .then(Commands.argument("players", EntityArgument.players())
                                        .executes(CooldownCancelCommand::run)
                                )
                        )
                        .then(Commands.literal("reset")
                                .then(Commands.argument("players", EntityArgument.players())
                                        .executes(CooldownCancelCommand::run)
                                )
                        )
        );

        dispatcher.register(
                Commands.literal("cdc")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("players", EntityArgument.players())
                                .executes(CooldownCancelCommand::run)
                        )
        );
    }

    public static int run(final CommandContext<CommandSourceStack> ctx) {
        try {
            final Collection<? extends Player> targets = EntityArgument.getPlayers(ctx, "players");

            for (Player player : targets) {
                JComponentPlatformUtils.getCooldowns(player).clear();
            }

            return 1;
        } catch (Exception e) {
            return 0;
        }
    }
}
