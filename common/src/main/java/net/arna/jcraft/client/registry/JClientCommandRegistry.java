package net.arna.jcraft.client.registry;

import com.mojang.brigadier.CommandDispatcher;
import dev.architectury.event.events.client.ClientCommandRegistrationEvent;
import net.arna.jcraft.client.command.JPoseCommand;
import net.minecraft.commands.CommandBuildContext;

public interface JClientCommandRegistry {

    static void registerCommands(CommandDispatcher<ClientCommandRegistrationEvent.ClientCommandSourceStack> dispatcher,
                                 CommandBuildContext ctx) {
        JPoseCommand.register(dispatcher);
    }
}
