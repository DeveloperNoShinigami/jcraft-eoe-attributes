package net.arna.jcraft.client.gui.screen;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.menu.MainMenu;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

@Environment(EnvType.CLIENT)
public class MainMenuScreen extends AbstractContainerScreen<MainMenu> {
    public MainMenuScreen(MainMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(JCraft.id("textures/gui/menu_screen.png"), mouseX, mouseY, 0, 0, this.imageWidth, this.imageHeight);
    }

}
