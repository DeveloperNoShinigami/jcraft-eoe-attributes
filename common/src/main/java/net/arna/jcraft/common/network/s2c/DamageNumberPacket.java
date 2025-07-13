package net.arna.jcraft.common.network.s2c;

import dev.architectury.networking.NetworkManager;
import net.arna.jcraft.client.rendering.DamageIndicatorManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;

public record DamageNumberPacket(int entityId, float damageAmount) { //friggin packet yo :)

    public DamageNumberPacket(FriendlyByteBuf buf) {
        this(buf.readInt(), buf.readFloat());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(this.entityId);
        buf.writeFloat(this.damageAmount);
    }

    public static void handle(FriendlyByteBuf buf, NetworkManager.PacketContext context) {
        int entityId = buf.readInt();
        float damageAmount = buf.readFloat();

        // Execute on client thread
        context.queue(() -> {
            Entity entity = Minecraft.getInstance().level.getEntity(entityId);
            if (entity != null) {
                DamageIndicatorManager.spawnDamageNumber(entity, damageAmount);
            }
        });
    }
}