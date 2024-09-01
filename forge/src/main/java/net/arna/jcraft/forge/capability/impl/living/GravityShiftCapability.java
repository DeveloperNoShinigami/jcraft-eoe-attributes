package net.arna.jcraft.forge.capability.impl.living;

import dev.architectury.networking.NetworkManager;
import net.arna.jcraft.common.component.impl.CommonGravityShiftComponentImpl;
import net.arna.jcraft.forge.JNetworkingForge;
import net.arna.jcraft.forge.capability.api.JCapability;
import net.arna.jcraft.forge.capability.impl.player.PhCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
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

import static net.arna.jcraft.JCraft.MOD_ID;

public class GravityShiftCapability extends CommonGravityShiftComponentImpl implements JCapability {

    public static ResourceLocation GS_S2C = new ResourceLocation(MOD_ID, "gs_s2c");
    public static ResourceLocation GS_C2S = new ResourceLocation(MOD_ID, "gs_c2s");

    public static Capability<GravityShiftCapability> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    public GravityShiftCapability(LivingEntity user) {
        super(user);
    }

    @Override
    public void sync(Entity entity) {
        super.sync(entity);
        if (entity != null) {
            GravityShiftCapability.syncEntityCapability(entity);
        }
    }

    public static void syncEntityCapability(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof LivingEntity livingEntity) {
            if (livingEntity.level() instanceof ServerLevel) {
                syncEntityCapability(livingEntity);
            }
        }
        if (event.getEntity() instanceof Player) {
            if (event.getEntity().level() instanceof ServerLevel) {
                syncEntityCapability(event.getEntity());
            }
        }
    }

    private static void syncEntityCapability(Entity entity) {
        if (entity instanceof LivingEntity living) {
            JNetworkingForge.sendPackets(living, GS_S2C, GS_C2S, getCapability(living));
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

    public static @NotNull LazyOptional<GravityShiftCapability> getCapabilityOptional(LivingEntity user) {
        return user.getCapability(CAPABILITY);
    }
    public static GravityShiftCapability getCapability(LivingEntity user) {
        return user.getCapability(CAPABILITY).orElse(new GravityShiftCapability(user));
    }
    public static void initNetwork(){
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, GS_S2C, (buf, context) -> {
            int id = buf.readInt();
            CompoundTag nbt = buf.readNbt();

            if (Minecraft.getInstance().level != null && Minecraft.getInstance().level.getEntity(id) instanceof LocalPlayer player) {
                PhCapability.getCapabilityOptional(player).ifPresent(c -> c.deserializeNBT(nbt));
            }
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, GS_C2S, (buf, context) -> {
            int id = buf.readInt();
            CompoundTag nbt = buf.readNbt();

            if (context.getPlayer().level().getEntity(id) instanceof Player player) {
                PhCapability.getCapabilityOptional(player).ifPresent(c -> c.deserializeNBT(nbt));
            }
        });
    }
}
