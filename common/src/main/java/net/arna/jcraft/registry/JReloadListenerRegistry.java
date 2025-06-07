package net.arna.jcraft.registry;

import dev.architectury.registry.ReloadListenerRegistry;
import net.arna.jcraft.common.data.MoveSetLoader;
import net.arna.jcraft.common.command.JCraftChangesCommand;
import net.arna.jcraft.common.data.AttackerDataLoader;
import net.arna.jcraft.common.util.EvolutionItemHandler;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;

public interface JReloadListenerRegistry {
    static void register() {
        register(MoveSetLoader::onReload);
        register(JCraftChangesCommand::onReload);
        register(EvolutionItemHandler::onReload);
        register(AttackerDataLoader::onReload);
    }

    private static void register(PreparableReloadListener listener) {
        ReloadListenerRegistry.register(PackType.SERVER_DATA, listener);
    }
}
