package net.arna.jcraft.common.menu;

import net.arna.jcraft.registry.JMenuRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class MainMenu extends AbstractContainerMenu {
    public MainMenu(int containerId, Inventory inventory, FriendlyByteBuf buf) {
        super(JMenuRegistry.MAIN_MENU_TYPE.get(), containerId);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        return false;
    }
}
