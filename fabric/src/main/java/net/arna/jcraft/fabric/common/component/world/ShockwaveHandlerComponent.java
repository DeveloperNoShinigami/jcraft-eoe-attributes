package net.arna.jcraft.fabric.common.component.world;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import net.arna.jcraft.common.component.world.CommonShockwaveHandlerComponent;

public interface ShockwaveHandlerComponent extends CommonShockwaveHandlerComponent, Component, AutoSyncedComponent, CommonTickingComponent {
}
