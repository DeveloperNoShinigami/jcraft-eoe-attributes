package net.arna.jcraft.forge.capability.impl.living;

import dev.architectury.networking.NetworkManager;
import net.arna.jcraft.common.component.impl.living.CommonBombTrackerComponentImpl;
import net.arna.jcraft.common.component.impl.living.CommonCooldownsComponentImpl;
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

public class CooldownsCapability extends CommonCooldownsComponentImpl implements JCapability {

    public static ResourceLocation CD_S2C = new ResourceLocation(MOD_ID, "cd_s2c");
    public static ResourceLocation CD_C2S = new ResourceLocation(MOD_ID, "cd_c2s");

    public static Capability<CooldownsCapability> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    public CooldownsCapability(LivingEntity entity) {
        super(entity);
    }

    @Override
    public void sync(Entity entity) {
        super.sync(entity);
        CooldownsCapability.syncEntityCapability(entity);
    }

    private static void syncEntityCapability(Entity entity) {
        if (entity instanceof LivingEntity living) {
            JNetworkingForge.sendPackets(living, CD_S2C, CD_C2S, getCapability(living));
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

    public static LazyOptional<CooldownsCapability> getCapabilityOptional(Entity entity) {
        return entity.getCapability(CAPABILITY);
    }

    public static CooldownsCapability getCapability(LivingEntity entity) {
        return entity.getCapability(CAPABILITY).orElse(new CooldownsCapability(entity));
    }

    public static void initNetwork(){
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, CD_S2C, (buf, context) -> {
            UUID uuid = buf.readUUID();
            CompoundTag nbt = buf.readNbt();
            Player player = null;
            if (Minecraft.getInstance().level != null) {
                player = Minecraft.getInstance().level.getPlayerByUUID(uuid);
            }
            if (player != null) {
                StandCapability.getCapabilityOptional(player).ifPresent(c -> c.deserializeNBT(nbt));
            }
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, CD_C2S, (buf, context) -> {
            UUID uuid = buf.readUUID();
            CompoundTag nbt = buf.readNbt();
            StandCapability.getCapabilityOptional(Minecraft.getInstance().level.getPlayerByUUID(uuid)).ifPresent(c -> c.deserializeNBT(nbt));
        });
    }
}