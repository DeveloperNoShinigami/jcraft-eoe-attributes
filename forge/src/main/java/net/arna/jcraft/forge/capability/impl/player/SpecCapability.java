package net.arna.jcraft.forge.capability.impl.player;

import net.arna.jcraft.common.component.impl.player.CommonPhComponentImpl;
import net.arna.jcraft.common.component.impl.player.CommonSpecComponentImpl;
import net.arna.jcraft.forge.JCraftForge;
import net.arna.jcraft.forge.JNetworkingForge;
import net.arna.jcraft.forge.capability.api.JCapability;
import net.arna.jcraft.forge.network.SyncPlayerC2SPacket;
import net.arna.jcraft.forge.network.SyncPlayerS2CPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.network.PacketDistributor;

import static net.arna.jcraft.JCraft.MOD_ID;

public class SpecCapability extends CommonSpecComponentImpl implements JCapability {

    public static Identifier SPEC_S2C = new Identifier(MOD_ID, "spec_s2c");
    public static Identifier SPEC_C2S = new Identifier(MOD_ID, "spec_c2s");

    public static Capability<SpecCapability> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    public SpecCapability(PlayerEntity player) {
        super(player);
    }

    @Override
    public void sync(Entity entity) {
        super.sync(entity);
        SpecCapability.syncEntityCapability(entity);
    }

    public static void syncEntityCapability(Entity entity) {
        if (entity instanceof PlayerEntity living) {
            JNetworkingForge.sendPackets(living, SPEC_S2C, SPEC_C2S, getCapability(living));
        }
    }

    public static void syncEntityCapability(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof LivingEntity livingEntity) {
            if (livingEntity.getWorld() instanceof ServerWorld) {
                syncEntityCapability(livingEntity);
            }
        }
    }

    @Override
    public NbtCompound serializeNBT() {
        NbtCompound tag = new NbtCompound();
        super.writeToNbt(tag);
        return tag;
    }

    @Override
    public void deserializeNBT(NbtCompound tag) {
        super.readFromNbt(tag);
    }


    public static LazyOptional<SpecCapability> getCapabilityOptional(PlayerEntity player) {
        return player.getCapability(CAPABILITY);
    }

    public static SpecCapability getCapability(PlayerEntity player) {
        return player.getCapability(CAPABILITY).orElse(new SpecCapability(player));
    }
}
