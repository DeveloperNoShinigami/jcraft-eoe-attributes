package net.arna.jcraft.common.component.entity;

import net.arna.jcraft.common.component.JComponent;
import net.minecraft.world.entity.Entity;

public interface CommonGrabComponent extends JComponent {
    void startGrab(Entity e, int duration, double distance, double verticalOffset);

    void startGrab(Entity e, int duration, double distance);

    void endGrab();

    int getDuration();

    Entity getAttacker();

    Entity getGrabbed();
}
