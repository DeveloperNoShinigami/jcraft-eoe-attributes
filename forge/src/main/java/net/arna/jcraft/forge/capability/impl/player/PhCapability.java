package net.arna.jcraft.forge.capability.impl.player;

import dev.architectury.networking.NetworkManager;
import net.arna.jcraft.common.component.impl.player.CommonPhComponentImpl;
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
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static net.arna.jcraft.JCraft.MOD_ID;


public class PhCapability extends CommonPhComponentImpl implements JCapability {

    public static ResourceLocation PH_S2C = new ResourceLocation(MOD_ID, "ph_s2c");
    public static ResourceLocation PH_C2S = new ResourceLocation(MOD_ID, "ph_c2s");

    public static Capability<PhCapability> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    public PhCapability(Player player) {
        super(player);
    }

    @Override
    public void sync(Entity entity) {
        super.sync(entity);
        PhCapability.syncEntityCapability(entity);
    }

    public static void syncEntityCapability(Entity entity) {
        if (entity instanceof Player living) {
            JNetworkingForge.sendPlayerPackets(living, PH_S2C, PH_C2S, getCapability(living));
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

    public static @NotNull LazyOptional<PhCapability> getCapabilityOptional(Player player) {
        return player.getCapability(CAPABILITY);
    }

    public static PhCapability getCapability(Player player) {
        return player.getCapability(CAPABILITY).orElse(new PhCapability(player));
    }
    public static void initNetwork(){
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, PH_S2C, (buf, context) -> {

        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, PH_C2S, (buf, context) -> {
            UUID id = buf.readUUID();
            CompoundTag nbt = buf.readNbt();
            if (Minecraft.getInstance().level != null) {
                PhCapability.getCapabilityOptional(Minecraft.getInstance().level.getPlayerByUUID(id)).ifPresent(c -> c.deserializeNBT(nbt));
            }
        });
    }
}
