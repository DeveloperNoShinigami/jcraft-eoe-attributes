package net.arna.jcraft.common.component.impl.living;

import lombok.Getter;
import net.arna.jcraft.common.component.living.CommonHitPropertyComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class CommonHitPropertyComponentImpl implements CommonHitPropertyComponent {
    private final Entity entity;
    protected long endHitAnimTime = 0;
    @Getter
    protected CommonHitPropertyComponent.HitAnimation hitAnimation = null;
    @Getter
    protected Vec3 randomRotation = Vec3.ZERO;

    public CommonHitPropertyComponentImpl(Entity entity) {
        this.entity = entity;
    }

    @Override
    public long endHitAnimTime() {
        return endHitAnimTime - entity.level().getGameTime();
    }

    @Override
    public void setHitAnimation(CommonHitPropertyComponent.HitAnimation hitAnimation, int duration) {
        this.hitAnimation = hitAnimation;
        this.endHitAnimTime = entity.level().getGameTime() + duration;
        sync(entity);
    }

    public void tick() {
    }

    public void sync(Entity entity) {

    }

    public boolean shouldSyncWith(ServerPlayer player) {
        return player.distanceToSqr(entity) <= 6400;
    }

    public void writeSyncPacket(FriendlyByteBuf buf, ServerPlayer recipient) {
        buf.writeVarLong(endHitAnimTime);
        if (endHitAnimTime > 0) {
            buf.writeVarInt(hitAnimation.ordinal());
        }
    }

    public void applySyncPacket(FriendlyByteBuf buf) {
        endHitAnimTime = buf.readVarLong();
        if (endHitAnimTime > 0) {
            hitAnimation = CommonHitPropertyComponent.HitAnimation.values()[buf.readVarInt()];
        }

        RandomSource random = entity.level().getRandom();
        randomRotation = new Vec3(
                random.nextGaussian(),
                random.nextGaussian(),
                random.nextGaussian()
        );
    }

    public void readFromNbt(CompoundTag tag) {
    }

    public void writeToNbt(CompoundTag tag) {
    }
}
