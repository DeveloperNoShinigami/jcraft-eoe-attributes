package net.arna.jcraft.api.registry;

import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.registry.registries.RegistrySupplier;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.menu.MainMenu;
import net.minecraft.world.inventory.MenuType;

public interface JMenuRegistry {

    RegistrySupplier<MenuType<MainMenu>> MAIN_MENU_TYPE = JCraft.MENU_REGISTRY.register("main_menu", () -> MenuRegistry.ofExtended((id, inv, buf) -> new MainMenu(id, inv.player)));

    static void init() {
        // intentionally left empty
    }
}
