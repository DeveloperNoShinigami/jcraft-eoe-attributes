package net.arna.jcraft.common.gravity.util.packet;

import net.arna.jcraft.common.component.entity.CommonGravityComponent;
import net.arna.jcraft.common.gravity.api.RotationParameters;
import net.minecraft.network.PacketByteBuf;

public abstract class GravityPacket {
    public abstract void write(PacketByteBuf buf);

    public abstract void run(CommonGravityComponent gc);

    public abstract RotationParameters getRotationParameters();
}
