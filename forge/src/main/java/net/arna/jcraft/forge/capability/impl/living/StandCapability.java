package net.arna.jcraft.forge.capability.impl.living;

import dev.architectury.networking.NetworkManager;
import net.arna.jcraft.common.component.impl.living.CommonStandComponentImpl;
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

public class StandCapability extends CommonStandComponentImpl implements JCapability {

    public static ResourceLocation STAND_S2C = new ResourceLocation(MOD_ID, "standard_s2c");
    public static ResourceLocation STAND_C2S = new ResourceLocation(MOD_ID, "standard_c2s");

    public static Capability<StandCapability> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    public StandCapability(LivingEntity living) {
        super(living);
    }

    @Override
    public void sync(Entity entity) {
        super.sync(entity);
        StandCapability.syncEntityCapability(entity);
    }

    private static void syncEntityCapability(Entity entity) {
        if (entity instanceof LivingEntity living) {

            JNetworkingForge.sendPackets(living, STAND_S2C, STAND_C2S, getCapability(living));
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

    public static LazyOptional<StandCapability> getCapabilityOptional(Entity entity) {
        return entity.getCapability(CAPABILITY);
    }

    public static StandCapability getCapability(LivingEntity entity) {
        return entity.getCapability(CAPABILITY).orElse(new StandCapability(entity));
    }

    public static void initNetwork(){
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, STAND_S2C, (buf, context) -> {

        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, STAND_C2S, (buf, context) -> {
            int id = buf.readInt();
            CompoundTag nbt = buf.readNbt();
            if (Minecraft.getInstance().level != null) {
                StandCapability.getCapabilityOptional(Minecraft.getInstance().level.getEntity(id)).ifPresent(c -> c.deserializeNBT(nbt));
            }
        });
    }
}