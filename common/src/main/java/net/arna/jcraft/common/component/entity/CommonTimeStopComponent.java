package net.arna.jcraft.common.component.entity;

import net.arna.jcraft.common.component.JComponent;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public interface CommonTimeStopComponent extends JComponent {
    int getTicks();

    void setTicks(int ticks);

    void addTotalVelocity(Vec3 vel);

    void tick(CallbackInfo ci);
}
