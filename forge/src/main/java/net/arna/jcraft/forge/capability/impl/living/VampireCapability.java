package net.arna.jcraft.forge.capability.impl.living;

import net.arna.jcraft.common.component.impl.CommonVampireComponentImpl;
import net.arna.jcraft.common.component.impl.player.CommonPhComponentImpl;
import net.arna.jcraft.forge.JNetworkingForge;
import net.arna.jcraft.forge.capability.api.JCapability;
import net.arna.jcraft.forge.capability.impl.player.PhCapability;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.network.PacketDistributor;

import static net.arna.jcraft.JCraft.MOD_ID;

public class VampireCapability extends CommonVampireComponentImpl implements JCapability {

    public static Identifier VAMP_S2C = new Identifier(MOD_ID, "vamp_s2c");
    public static Identifier VAMP_C2S = new Identifier(MOD_ID, "vamp_c2s");


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

    public static LazyOptional<VampireCapability> getCapabilityOptional(Entity entity) {
        return entity.getCapability(CAPABILITY);
    }

    public static VampireCapability getCapability(LivingEntity entity) {
        return entity.getCapability(CAPABILITY).orElse(new VampireCapability(entity));
    }
}
