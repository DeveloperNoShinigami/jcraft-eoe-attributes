package net.arna.jcraft.fabric.common.component.world;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import lombok.Data;
import net.arna.jcraft.common.component.world.CommonShockwaveHandlerComponent;
import net.arna.jcraft.common.util.JUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public interface ShockwaveHandlerComponent extends CommonShockwaveHandlerComponent, Component, AutoSyncedComponent, CommonTickingComponent {
}
