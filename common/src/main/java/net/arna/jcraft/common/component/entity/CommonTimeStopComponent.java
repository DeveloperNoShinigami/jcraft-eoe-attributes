package net.arna.jcraft.common.component.entity;

import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public interface CommonTimeStopComponent {
    int getTicks();
    void setTicks(int ticks);
    void addTotalVelocity(Vec3d vel);
    void tick(CallbackInfo ci);
}
