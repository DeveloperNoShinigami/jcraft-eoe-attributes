package net.arna.jcraft.client.gui.screen;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.entity.stand.StandType;
import net.arna.jcraft.common.menu.MainMenu;
import net.arna.jcraft.common.util.JUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

@Environment(EnvType.CLIENT)
public class MainMenuScreen extends AbstractContainerScreen<MainMenu> {

    protected StandEntity<?,?> stand;

    public MainMenuScreen(MainMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        stand = JUtils.getStand(playerInventory.player);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(JCraft.id("textures/gui/menu_screen.png"), leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        drawText(guiGraphics, this.title, 0, 0);
        if (stand == null || stand.getStandType() == StandType.NONE) {
            drawText(guiGraphics, StandType.NONE.getNameText(), 1, 0);
        }
        else {
            drawText(guiGraphics, stand.getStandType().getNameText(), 1, 0);
            final String desc = String.format("entity.%s.%s%s.info.desc", JCraft.MOD_ID, stand.getStandType().getNameKey(), stand.getModeOrdinal() == 0 ? "" : Integer.toString(stand.getModeOrdinal()));
            drawText(guiGraphics, Component.translatable(desc), 2, 0);
        }
    }

    protected void drawText(final GuiGraphics guiGraphics, final Component text, final int row, final int col) {
        drawText(guiGraphics, text, row, col, 4210752);
    }

    protected void drawText(final GuiGraphics guiGraphics, final Component text, final int row, final int col, final int color) {
        guiGraphics.drawString(this.font, text, this.titleLabelX+10*col, this.titleLabelY+10*row, color, false);
    }

}
