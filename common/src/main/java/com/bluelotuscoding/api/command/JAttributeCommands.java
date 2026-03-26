package com.bluelotuscoding.api.command;

import com.bluelotuscoding.api.attribute.StandAttributeManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class JAttributeCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("jcraft_attributes")
            .requires(source -> source.hasPermission(2))
            .then(Commands.literal("reset")
                .then(Commands.argument("player", EntityArgument.player())
                    .executes(JAttributeCommands::resetAttributes))));
    }

    private static int resetAttributes(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        
        StandAttributeManager.resetToBase(player);
        
        context.getSource().sendSuccess(() -> Component.literal("Successfully reset all JCraft attributes for " + player.getScoreboardName()), true);
        return 1;
    }
}
