package net.arna.jcraft.api.component.entity;

import net.arna.jcraft.api.component.JComponent;
import net.minecraft.world.entity.Entity;

public interface CommonGrabComponent extends JComponent {
    void startGrab(final Entity e, final int duration, final double distance, final double verticalOffset);

    void startGrab(final Entity e, final int duration, final double distance);

    void endGrab();

    int getDuration();

    Entity getAttacker();

    Entity getGrabbed();
}
