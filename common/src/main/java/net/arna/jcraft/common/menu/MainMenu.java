package net.arna.jcraft.common.menu;

import net.arna.jcraft.api.registry.JMenuRegistry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class MainMenu extends AbstractContainerMenu {

    public MainMenu(final int id, final Player player) {
        super(JMenuRegistry.MAIN_MENU_TYPE.get(), id);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
