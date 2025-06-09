package net.arna.jcraft.registry;

import net.arna.jcraft.common.command.JCraftChangesCommand;
import net.arna.jcraft.common.data.AttackerDataLoader;
import net.arna.jcraft.common.data.MoveSetLoader;
import net.arna.jcraft.common.util.EvolutionItemHandler;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.function.Consumer;

public interface JServerReloadListenerRegistry {
    static void register(Consumer<PreparableReloadListener> registrar) {
        registrar.accept(MoveSetLoader::onReload);
        registrar.accept(JCraftChangesCommand::onReload);
        registrar.accept(EvolutionItemHandler::onReload);
        registrar.accept(AttackerDataLoader::onReload);
    }
}
