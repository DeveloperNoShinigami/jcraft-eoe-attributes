package net.arna.jcraft.fabric.common.component.living;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import net.arna.jcraft.common.component.living.CommonCooldownsComponent;
import net.arna.jcraft.common.util.CooldownType;

public interface CooldownsComponent extends CommonCooldownsComponent, Component, AutoSyncedComponent, CommonTickingComponent {

}
