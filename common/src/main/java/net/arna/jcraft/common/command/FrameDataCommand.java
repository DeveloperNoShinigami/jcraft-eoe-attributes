package net.arna.jcraft.common.command;

import com.mojang.brigadier.CommandDispatcher;
import net.arna.jcraft.common.tickable.FrameDataRequests;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class FrameDataCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("framedata")
                .then(Commands.literal("stand")
                        .executes(
                                context -> run(context.getSource(), true)
                        )
                )
                .then(Commands.literal("spec")
                        .executes(
                                context -> run(context.getSource(), false)
                        )
                )
        );
    }

    public static int run(CommandSourceStack source, boolean stand) {
        ServerPlayer player = source.getPlayer();
        if (player == null) return 0;
        FrameDataRequests.add(player, stand ? FrameDataRequests.FrameDataType.STAND : FrameDataRequests.FrameDataType.SPEC);
        return 1;
    }
}
