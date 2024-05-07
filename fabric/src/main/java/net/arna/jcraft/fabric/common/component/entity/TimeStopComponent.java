package net.arna.jcraft.fabric.common.component.entity;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.arna.jcraft.common.component.entity.CommonTimeStopComponent;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public interface TimeStopComponent extends CommonTimeStopComponent, Component, AutoSyncedComponent {

}
