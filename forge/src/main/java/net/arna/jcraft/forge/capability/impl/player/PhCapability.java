package net.arna.jcraft.forge.capability.impl.player;

import net.arna.jcraft.common.component.impl.player.CommonPhComponentImpl;
import net.arna.jcraft.forge.JNetworkingForge;
import net.arna.jcraft.forge.capability.api.JCapability;
import net.arna.jcraft.forge.capability.impl.living.VampireCapability;
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
import org.jetbrains.annotations.NotNull;

import static net.arna.jcraft.JCraft.MOD_ID;


public class PhCapability extends CommonPhComponentImpl implements JCapability {

    public static Identifier PH_S2C = new Identifier(MOD_ID, "ph_s2c");
    public static Identifier PH_C2S = new Identifier(MOD_ID, "ph_c2s");

    public static Capability<PhCapability> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    public PhCapability(PlayerEntity player) {
        super(player);
    }

    @Override
    public void sync(Entity entity) {
        super.sync(entity);
        PhCapability.syncEntityCapability(entity);
    }

    public static void syncEntityCapability(Entity entity) {
        if (entity instanceof PlayerEntity living) {
            JNetworkingForge.sendPackets(living, PH_S2C, PH_C2S, getCapability(living));
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

    public static @NotNull LazyOptional<PhCapability> getCapabilityOptional(PlayerEntity player) {
        return player.getCapability(CAPABILITY);
    }

    public static PhCapability getCapability(PlayerEntity player) {
        return player.getCapability(CAPABILITY).orElse(new PhCapability(player));
    }

}
