package net.arna.jcraft.api.component.player;


import net.arna.jcraft.api.component.JComponent;

public interface CommonPhComponent extends JComponent {
    int getLevel();

    void increaseLevel();

    void resetLevel();
}
