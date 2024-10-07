package net.arna.jcraft.common.network.c2s;

import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.menu.MenuRegistry;
import lombok.NonNull;
import net.arna.jcraft.common.menu.MainMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class MenuCallPacket {

    public static void handle(final FriendlyByteBuf buf, final NetworkManager.PacketContext context) {
        MenuRegistry.openExtendedMenu((ServerPlayer)context.getPlayer(), new MenuProvider() {
            @NonNull
            @Override
            public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
                return new MainMenu(id, player);
            }

            @NonNull
            @Override
            public Component getDisplayName() {
                return Component.translatable("container.jcraft.main_menu");
            }
        }, friendlyByteBuf -> {}
        );
    }

}
