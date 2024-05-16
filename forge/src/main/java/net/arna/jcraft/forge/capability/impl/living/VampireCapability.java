package net.arna.jcraft.forge.capability.impl.living;

import dev.architectury.networking.NetworkManager;
import net.arna.jcraft.common.component.impl.CommonVampireComponentImpl;
import net.arna.jcraft.common.component.impl.player.CommonPhComponentImpl;
import net.arna.jcraft.forge.JNetworkingForge;
import net.arna.jcraft.forge.capability.api.JCapability;
import net.arna.jcraft.forge.capability.impl.entity.TimeStopCapability;
import net.arna.jcraft.forge.capability.impl.player.PhCapability;
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
import net.minecraftforge.network.PacketDistributor;

import java.util.UUID;

import static net.arna.jcraft.JCraft.MOD_ID;

public class VampireCapability extends CommonVampireComponentImpl implements JCapability {

    public static ResourceLocation VAMP_S2C = new ResourceLocation(MOD_ID, "vamp_s2c");
    public static ResourceLocation VAMP_C2S = new ResourceLocation(MOD_ID, "vamp_c2s");


    public static Capability<VampireCapability> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    public VampireCapability(LivingEntity living) {
        super(living);
    }

    @Override
    public void sync(Entity entity) {
        super.sync(entity);
        VampireCapability.syncEntityCapability(entity);
    }

    private static void syncEntityCapability(Entity entity) {
        if (entity instanceof LivingEntity living) {
            JNetworkingForge.sendPackets(living, VAMP_S2C, VAMP_C2S, getCapability(living));
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

    public static LazyOptional<VampireCapability> getCapabilityOptional(Entity entity) {
        return entity.getCapability(CAPABILITY);
    }

    public static VampireCapability getCapability(LivingEntity entity) {
        return entity.getCapability(CAPABILITY).orElse(new VampireCapability(entity));
    }

    public static void initNetwork(){
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, VAMP_S2C, (buf, context) -> {

        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, VAMP_C2S, (buf, context) -> {
            int id = buf.readInt();
            CompoundTag nbt = buf.readNbt();
            if (Minecraft.getInstance().level != null) {
                VampireCapability.getCapabilityOptional(Minecraft.getInstance().level.getEntity(id)).ifPresent(c -> c.deserializeNBT(nbt));
            }
        });
    }
}
