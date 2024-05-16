package net.arna.jcraft.mixin.client;

import net.minecraft.client.Camera;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Camera.class)
public interface CameraInvoker {
    @Invoker("clipToSpace")
    double invokeClipToSpace(double desiredCameraDistance);

    @Invoker("moveBy")
    void invokeMoveBy(double x, double y, double z);

    @Invoker("setPos")
    void invokeSetPos(Vec3 pos);

}
