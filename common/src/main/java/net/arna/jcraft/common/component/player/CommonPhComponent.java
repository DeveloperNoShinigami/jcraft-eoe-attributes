package net.arna.jcraft.common.component.player;


import net.arna.jcraft.common.component.JComponent;

public interface CommonPhComponent extends JComponent {
    int getLevel();

    void increaseLevel();

    void resetLevel();
}
