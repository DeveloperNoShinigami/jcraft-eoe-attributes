package net.arna.jcraft.forge.capability.impl.player;

import dev.architectury.networking.NetworkManager;
import net.arna.jcraft.common.component.impl.player.CommonSpecComponentImpl;
import net.arna.jcraft.forge.JNetworkingForge;
import net.arna.jcraft.forge.capability.api.JCapability;
import net.arna.jcraft.forge.capability.impl.living.CooldownsCapability;
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

public class SpecCapability extends CommonSpecComponentImpl implements JCapability {

    public static ResourceLocation SPEC_S2C = new ResourceLocation(MOD_ID, "spec_s2c");
    public static ResourceLocation SPEC_C2S = new ResourceLocation(MOD_ID, "spec_c2s");

    public static Capability<SpecCapability> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    public SpecCapability(Player player) {
        super(player);
    }

    @Override
    public void sync(Entity entity) {
        super.sync(entity);
        if (entity != null) {
            SpecCapability.syncEntityCapability(entity);
        }
    }

    public static void syncEntityCapability(Entity entity) {
        if (entity instanceof Player living) {
            JNetworkingForge.sendPlayerPackets(living, SPEC_S2C, SPEC_C2S, getCapability(living));
        }
    }

    public static void syncEntityCapability(PlayerEvent.StartTracking event) {
        if (event.getEntity() instanceof Player) {
            if (event.getEntity().level() instanceof ServerLevel) {
                syncEntityCapability(event.getEntity());
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


    public static LazyOptional<SpecCapability> getCapabilityOptional(Player player) {
        return player.getCapability(CAPABILITY);
    }

    public static SpecCapability getCapability(Player player) {
        return player.getCapability(CAPABILITY).orElse(new SpecCapability(player));
    }

    public static void initNetwork(){
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, SPEC_S2C, (buf, context) -> {
            int id = buf.readInt();
            CompoundTag nbt = buf.readNbt();

            if (Minecraft.getInstance().level.getEntity(id) instanceof Player player) {
                SpecCapability.getCapabilityOptional(player).ifPresent(c -> c.deserializeNBT(nbt));
            }

        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, SPEC_C2S, (buf, context) -> {
            int id = buf.readInt();
            CompoundTag nbt = buf.readNbt();

            if (context.getPlayer().level() != null && context.getPlayer().level().getEntity(id) instanceof Player player) {
                SpecCapability.getCapabilityOptional(player).ifPresent(c -> c.deserializeNBT(nbt));
            }
        });
    }
}
