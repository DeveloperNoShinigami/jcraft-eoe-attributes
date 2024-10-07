package net.arna.jcraft.forge.capability.impl.living;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.arna.jcraft.common.component.impl.living.CommonHitPropertyComponentImpl;
import net.arna.jcraft.forge.capability.api.JCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;

import static net.arna.jcraft.JCraft.MOD_ID;

public class HitPropertyCapability extends CommonHitPropertyComponentImpl implements JCapability {
    public static ResourceLocation HIT_S2C = new ResourceLocation(MOD_ID, "hit_s2c");

    public static Capability<HitPropertyCapability> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    public HitPropertyCapability(LivingEntity living) {
        super(living);
    }

    @Override
    public void setHitAnimation(HitAnimation hitAnimation, int duration) {
        super.setHitAnimation(hitAnimation, duration);

        if (entity instanceof LivingEntity livingEntity) {
            if (entity.level() instanceof ServerLevel serverWorld) {
                FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
                buf.writeVarInt(livingEntity.getId());
                this.writeSyncPacket(buf, null);
                serverWorld.players().forEach(recipient -> {
                    if (this.shouldSyncWith(recipient)) {
                        NetworkManager.sendToPlayer(recipient, HIT_S2C, buf);
                    }
                });
            }
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        super.writeToNbt(tag);
        return tag;
    }
    @Override
    public void deserializeNBT(CompoundTag tag) {
        super.readFromNbt(tag);
    }

    public static LazyOptional<HitPropertyCapability> getCapabilityOptional(Entity entity) {
        return entity.getCapability(CAPABILITY);
    }
    public static HitPropertyCapability getCapability(LivingEntity entity) {
        return entity.getCapability(CAPABILITY).orElse(new HitPropertyCapability(entity));
    }

    public static void initClient(){
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, HIT_S2C, (buf, context) -> {
            int id = buf.readVarInt();

            if (Minecraft.getInstance().level != null && Minecraft.getInstance().level.getEntity(id) instanceof LivingEntity livingEntity) {
                HitPropertyCapability.getCapabilityOptional(livingEntity).ifPresent(
                        capability -> capability.applySyncPacket(buf)
                );
            }
        });
    }
}