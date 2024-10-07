package net.arna.jcraft.common.component.living;

import net.arna.jcraft.common.component.JComponent;

public interface CommonVampireComponent extends JComponent {
    float getBlood();

    void setBlood(final float blood);

    boolean isVampire();

    void setVampire(final boolean b);
}
