package net.arna.jcraft.fabric.common.component.impl.world;

import lombok.NonNull;
import net.arna.jcraft.common.component.impl.world.CommonShockwaveHandlerComponentImpl;
import net.arna.jcraft.fabric.common.component.JComponents;
import net.arna.jcraft.fabric.common.component.world.ShockwaveHandlerComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public class ShockwaveHandlerComponentImpl extends CommonShockwaveHandlerComponentImpl implements ShockwaveHandlerComponent {

    private final Level world;

    public ShockwaveHandlerComponentImpl(Level world) {
        super(world);
        this.world = world;
    }


    @Override
    public void sync(Shockwave shockwave) {
        JComponents.SHOCKWAVE_HANDLER.sync(world, (buf, player) -> writeSyncPacket(buf, shockwave));
    }

    @Override
    public void readFromNbt(@NonNull CompoundTag tag) {
        super.readFromNbt(tag);
    }

    @Override
    public void writeToNbt(@NonNull CompoundTag tag) {
        super.writeToNbt(tag);
    }

    @Override
    public void writeSyncPacket(FriendlyByteBuf buf, ServerPlayer recipient) {
        super.writeSyncPacket(buf, recipient);
    }

    @Override
    public void applySyncPacket(FriendlyByteBuf buf) {
        super.applySyncPacket(buf);
    }

    @Override
    public void tick() {
        super.tick();
    }
}
