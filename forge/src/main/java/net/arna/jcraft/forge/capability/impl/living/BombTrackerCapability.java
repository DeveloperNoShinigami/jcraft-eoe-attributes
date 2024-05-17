package net.arna.jcraft.forge.capability.impl.living;

import dev.architectury.networking.NetworkManager;
import net.arna.jcraft.common.component.impl.living.CommonBombTrackerComponentImpl;
import net.arna.jcraft.forge.JNetworkingForge;
import net.arna.jcraft.forge.capability.api.JCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.PlayerEvent;

import static net.arna.jcraft.JCraft.MOD_ID;

public class BombTrackerCapability extends CommonBombTrackerComponentImpl implements JCapability {

    public static ResourceLocation BOMB_S2C = new ResourceLocation(MOD_ID, "bomb_s2c");
    public static ResourceLocation BOMB_C2S = new ResourceLocation(MOD_ID, "bomb_c2s");

    public static Capability<BombTrackerCapability> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    public BombTrackerCapability(LivingEntity entity) {
        super(entity);
    }

    @Override
    public void sync(Entity entity) {
        super.sync(entity);
        BombTrackerCapability.syncEntityCapability(entity);
    }

    private static void syncEntityCapability(Entity entity) {
        if (entity instanceof LivingEntity living) {
            JNetworkingForge.sendPackets(living, BOMB_S2C, BOMB_C2S, getCapability(living));
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

    public static LazyOptional<BombTrackerCapability> getCapabilityOptional(Entity entity) {
        return entity.getCapability(CAPABILITY);
    }

    public static BombTrackerCapability getCapability(LivingEntity entity) {
        return entity.getCapability(CAPABILITY).orElse(new BombTrackerCapability(entity));
    }

    public static void initNetwork(){
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, BOMB_S2C, (buf, context) -> {

        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, BOMB_C2S, (buf, context) -> {
            int id = buf.readInt();
            CompoundTag nbt = buf.readNbt();
            if (Minecraft.getInstance().level != null) {
                BombTrackerCapability.getCapabilityOptional(Minecraft.getInstance().level.getEntity(id)).ifPresent(c -> c.deserializeNBT(nbt));
            }
        });
    }
}