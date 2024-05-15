package net.arna.jcraft.common.component.impl.living;

import lombok.Getter;
import net.arna.jcraft.common.component.living.CommonHitPropertyComponent;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

public class CommonHitPropertyComponentImpl implements CommonHitPropertyComponent {
    private final Entity entity;
    protected long endHitAnimTime = 0;
    @Getter
    protected CommonHitPropertyComponent.HitAnimation hitAnimation = null;
    @Getter
    protected Vec3d randomRotation = Vec3d.ZERO;

    public CommonHitPropertyComponentImpl(Entity entity) {
        this.entity = entity;
    }

    @Override
    public long endHitAnimTime() {
        return endHitAnimTime - entity.getWorld().getTime();
    }

    @Override
    public void setHitAnimation(CommonHitPropertyComponent.HitAnimation hitAnimation, int duration) {
        this.hitAnimation = hitAnimation;
        this.endHitAnimTime = entity.getWorld().getTime() + duration;
        sync(entity);
    }

    public void tick() {
    }

    public void sync(Entity entity) {

    }

    public boolean shouldSyncWith(ServerPlayerEntity player) {
        return player.squaredDistanceTo(entity) <= 6400;
    }

    public void writeSyncPacket(PacketByteBuf buf, ServerPlayerEntity recipient) {
        buf.writeVarLong(endHitAnimTime);
        if (endHitAnimTime > 0) {
            buf.writeVarInt(hitAnimation.ordinal());
        }
    }

    public void applySyncPacket(PacketByteBuf buf) {
        endHitAnimTime = buf.readVarLong();
        if (endHitAnimTime > 0) {
            hitAnimation = CommonHitPropertyComponent.HitAnimation.values()[buf.readVarInt()];
        }

        Random random = entity.getWorld().getRandom();
        randomRotation = new Vec3d(
                random.nextGaussian(),
                random.nextGaussian(),
                random.nextGaussian()
        );
    }

    public void readFromNbt(NbtCompound tag) {
    }

    public void writeToNbt(NbtCompound tag) {
    }
}
