package net.arna.jcraft.common.network.c2s;

import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.menu.ExtendedMenuProvider;
import dev.architectury.registry.menu.MenuRegistry;
import net.arna.jcraft.registry.JMenuRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.NotNull;

public class MenuCallPacket {

    public static void handle(final FriendlyByteBuf buf, final NetworkManager.PacketContext context) {
        context.getPlayer().displayClientMessage(Component.literal("Menu Call Packet was perceived."), false);
        MenuRegistry.openExtendedMenu((ServerPlayer)context.getPlayer(), new ExtendedMenuProvider() {
            @NotNull
            @Override
            public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
                return JMenuRegistry.MAIN_MENU_TYPE.get().create(id, inventory);
            }

            @NotNull
            @Override
            public Component getDisplayName() {
                return Component.translatable("container.jcraft.main_menu");
            }

            @Override
            public void saveExtraData(FriendlyByteBuf buf) {

            }
        });
    }

}
