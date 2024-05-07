package net.arna.jcraft.fabric.common.component.entity;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import net.arna.jcraft.common.component.entity.CommonGrabComponent;
import net.minecraft.entity.Entity;

public interface GrabComponent extends CommonGrabComponent, Component, AutoSyncedComponent, CommonTickingComponent {

}
