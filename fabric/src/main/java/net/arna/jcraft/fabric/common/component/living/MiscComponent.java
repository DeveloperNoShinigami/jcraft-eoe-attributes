package net.arna.jcraft.fabric.common.component.living;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import net.arna.jcraft.common.component.living.CommonMiscComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public interface MiscComponent extends CommonMiscComponent, Component, AutoSyncedComponent, CommonTickingComponent {

}
