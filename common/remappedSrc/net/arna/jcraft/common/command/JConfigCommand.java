package net.arna.jcraft.common.command;

import com.mojang.brigadier.CommandDispatcher;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.arna.jcraft.registry.JPacketRegistry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class JConfigCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("jconfig")
                .executes(ctx -> {
                    ServerPlayer player = ctx.getSource().getPlayerOrException();

                    FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
                    buf.writeBoolean(ctx.getSource().hasPermission(2)); // editable
                    buf.writeBoolean(true); // show
                    // No need to write any options. Client should already have all of them.

                    NetworkManager.sendToPlayer(player, JPacketRegistry.S2C_SERVER_CONFIG, buf);
                    return 1;
                }));
    }
}
