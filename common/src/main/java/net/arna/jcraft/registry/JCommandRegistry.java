package net.arna.jcraft.registry;

import com.mojang.brigadier.CommandDispatcher;
import net.arna.jcraft.common.command.*;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public interface JCommandRegistry {
    static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        InduceAttackCommand.register(dispatcher);
        AboutStandCommand.register(dispatcher);
        AboutSpecCommand.register(dispatcher);
        SetStandCommand.register(dispatcher);
        ClearStandCommand.register(dispatcher);
        SetSpecCommand.register(dispatcher);
        MoveDataCommand.register(dispatcher);
        StandSkinCommand.register(dispatcher);
        StandBlockCommand.register(dispatcher);
        GravityCommand.register(dispatcher);
        JConfigCommand.register(dispatcher);
    }
}
