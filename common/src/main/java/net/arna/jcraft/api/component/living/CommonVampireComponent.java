package net.arna.jcraft.api.component.living;

import net.arna.jcraft.api.component.JComponent;

public interface CommonVampireComponent extends JComponent {
    float getBlood();

    void setBlood(final float blood);

    boolean isVampire();

    void setVampire(final boolean b);
}
