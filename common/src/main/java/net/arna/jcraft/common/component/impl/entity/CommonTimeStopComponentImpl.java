package net.arna.jcraft.common.component.impl.entity;

import lombok.Getter;
import lombok.NonNull;
import net.arna.jcraft.common.component.entity.CommonTimeStopComponent;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.arna.jcraft.common.util.JUtils.addVelocity;
import static net.arna.jcraft.common.util.JUtils.stopTick;

@Getter
public class CommonTimeStopComponentImpl implements CommonTimeStopComponent {
    private final Entity entity;
    private int ticks;
    // Launch buildup implementation, because players are special snowflakes and don't build it in timestop like enemies do
    private Vec3 totalVelocity = Vec3.ZERO;

    public CommonTimeStopComponentImpl(Entity entity) {
        this.entity = entity;
    }

    @Override
    public void setTicks(int ticks) {
        if (this.ticks <= 0) // If just beginning a new Timestop
        {
            totalVelocity = Vec3.ZERO;
        }
        this.ticks = ticks;
        sync(entity);
    }

    public void sync(Entity entity) {
    }

    @Override
    public void addTotalVelocity(Vec3 vel) {
        totalVelocity = totalVelocity.add(vel);
    }

    @Override
    public void tick(CallbackInfo ci) {
        if (ticks <= 0) {
            return;
        }

        stopTick(entity);
        for (Entity passenger : entity.getPassengers()) {
            stopTick(passenger);
        }
        ticks--;

        if (ticks == 0 && totalVelocity.lengthSqr() > 0.01) {
            // Lift off ground to stop friction from cutting the launch short
            Vec3i localUp = GravityChangerAPI.getGravityDirection(entity).getOpposite().getNormal();
            double upX = entity.getX() + localUp.getX() * 0.1;
            double upY = entity.getY() + localUp.getY() * 0.1;
            double upZ = entity.getZ() + localUp.getZ() * 0.1;
            entity.teleportToWithTicket(upX, upY, upZ);
            entity.setOnGround(false);

            addVelocity(entity, totalVelocity.x, totalVelocity.y, totalVelocity.z);
        }

        ci.cancel();
    }

    public void readFromNbt(@NonNull CompoundTag tag) {
    }

    public void writeToNbt(@NonNull CompoundTag tag) {

    }
}
