package net.arna.jcraft.forge.capability.impl.living;

import dev.architectury.networking.NetworkManager;
import net.arna.jcraft.common.component.impl.living.CommonHitPropertyComponentImpl;
import net.arna.jcraft.common.component.impl.living.CommonMiscComponentImpl;
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
import org.stringtemplate.v4.misc.Misc;

import java.util.UUID;

import static net.arna.jcraft.JCraft.MOD_ID;

public class MiscCapability extends CommonMiscComponentImpl implements JCapability {

    public static ResourceLocation MISC_S2C = new ResourceLocation(MOD_ID, "misc_s2c");
    public static ResourceLocation MISC_C2S = new ResourceLocation(MOD_ID, "misc_c2s");

    public static Capability<MiscCapability> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    public MiscCapability(LivingEntity living) {
        super(living);
    }

    @Override
    public void sync(Entity entity) {
        super.sync(entity);
        MiscCapability.syncEntityCapability(entity);
    }

    private static void syncEntityCapability(Entity entity) {
        if (entity instanceof LivingEntity living) {
            JNetworkingForge.sendPackets(living, MISC_S2C, MISC_C2S, getCapability(living));
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

    public static LazyOptional<MiscCapability> getCapabilityOptional(Entity entity) {
        return entity.getCapability(CAPABILITY);
    }

    public static MiscCapability getCapability(LivingEntity entity) {
        return entity.getCapability(CAPABILITY).orElse(new MiscCapability(entity));
    }

    public static void initNetwork(){
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, MISC_S2C, (buf, context) -> {
            UUID uuid = buf.readUUID();
            CompoundTag nbt = buf.readNbt();
            Player player = null;
            if (Minecraft.getInstance().level != null) {
                player = Minecraft.getInstance().level.getPlayerByUUID(uuid);
            }
            if (player != null) {
                MiscCapability.getCapabilityOptional(player).ifPresent(c -> c.deserializeNBT(nbt));
            }
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, MISC_C2S, (buf, context) -> {
            UUID uuid = buf.readUUID();
            CompoundTag nbt = buf.readNbt();
            MiscCapability.getCapabilityOptional(Minecraft.getInstance().level.getPlayerByUUID(uuid)).ifPresent(c -> c.deserializeNBT(nbt));
        });
    }
}