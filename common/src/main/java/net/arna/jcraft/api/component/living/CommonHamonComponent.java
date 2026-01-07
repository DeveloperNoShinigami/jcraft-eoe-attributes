package net.arna.jcraft.api.component.living;

import net.arna.jcraft.api.component.JComponent;

public interface CommonHamonComponent extends JComponent {

    float getHamonCharge();

    void setHamonCharge(final float charge);

    boolean isHamonizeReady();

    void setHamonizeReady(final boolean ready);
}
