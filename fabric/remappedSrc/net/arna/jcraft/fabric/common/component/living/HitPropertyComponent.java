package net.arna.jcraft.fabric.common.component.living;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import net.arna.jcraft.common.component.living.CommonHitPropertyComponent;

public interface HitPropertyComponent extends CommonHitPropertyComponent, Component, AutoSyncedComponent, CommonTickingComponent {

}
