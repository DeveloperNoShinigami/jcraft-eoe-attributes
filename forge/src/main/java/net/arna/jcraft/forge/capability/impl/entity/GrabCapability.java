package net.arna.jcraft.forge.capability.impl.entity;

import dev.architectury.networking.NetworkManager;
import net.arna.jcraft.common.component.impl.entity.CommonGrabComponentImpl;
import net.arna.jcraft.forge.JNetworkingForge;
import net.arna.jcraft.forge.capability.api.JCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.PlayerEvent;

import java.util.UUID;

import static net.arna.jcraft.JCraft.MOD_ID;

public class GrabCapability extends CommonGrabComponentImpl implements JCapability {

    public static ResourceLocation GRAB_S2C = new ResourceLocation(MOD_ID, "grab_s2c");
    public static ResourceLocation GRAB_C2S = new ResourceLocation(MOD_ID, "grab_c2s");

    public static Capability<GrabCapability> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    public GrabCapability(Entity entity) {
        super(entity);
    }

    @Override
    public void sync(Entity entity) {
        super.sync(entity);
        GrabCapability.syncEntityCapability(entity);
    }

    private static void syncEntityCapability(Entity entity) {
        if (entity instanceof LivingEntity living) {
            JNetworkingForge.sendPackets(living, GRAB_S2C, GRAB_C2S, getCapability(living));
        }
    }

    public static void syncEntityCapability(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof LivingEntity livingEntity) {
            if (livingEntity.level() instanceof ServerLevel) {
                syncEntityCapability(livingEntity);
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

    public static LazyOptional<GrabCapability> getCapabilityOptional(Entity entity) {
        return entity.getCapability(CAPABILITY);
    }

    public static GrabCapability getCapability(Entity entity) {
        return entity.getCapability(CAPABILITY).orElse(new GrabCapability(entity));
    }

    public static void initNetwork(){
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, GRAB_S2C, (buf, context) -> {
            UUID uuid = buf.readUUID();
            CompoundTag nbt = buf.readNbt();
            Player player = null;
            if (Minecraft.getInstance().level != null) {
                player = Minecraft.getInstance().level.getPlayerByUUID(uuid);
            }
            if (player != null) {
                GrabCapability.getCapabilityOptional(player).ifPresent(c -> c.deserializeNBT(nbt));
            }
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, GRAB_C2S, (buf, context) -> {
            UUID uuid = buf.readUUID();
            CompoundTag nbt = buf.readNbt();
            GrabCapability.getCapabilityOptional(Minecraft.getInstance().level.getPlayerByUUID(uuid)).ifPresent(c -> c.deserializeNBT(nbt));
        });
    }
}