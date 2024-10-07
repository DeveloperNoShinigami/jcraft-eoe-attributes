package net.arna.jcraft.common.component.entity;

import net.arna.jcraft.common.component.JComponent;
import net.minecraft.world.entity.Entity;

public interface CommonGrabComponent extends JComponent {
    void startGrab(final Entity e, final int duration, final double distance, final double verticalOffset);

    void startGrab(final Entity e, final int duration, final double distance);

    void endGrab();

    int getDuration();

    Entity getAttacker();

    Entity getGrabbed();
}
