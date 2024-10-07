package net.arna.jcraft.forge.capability.impl.living;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.arna.jcraft.common.component.impl.living.CommonStandComponentImpl;
import net.arna.jcraft.common.entity.stand.StandType;
import net.arna.jcraft.forge.capability.api.JCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.PlayerEvent;

import static net.arna.jcraft.JCraft.MOD_ID;

public class StandCapability extends CommonStandComponentImpl implements JCapability {
    public static ResourceLocation STAND_S2C = new ResourceLocation(MOD_ID, "stand_s2c");
    //public static ResourceLocation STAND_C2S = new ResourceLocation(MOD_ID, "standard_c2s");

    public static Capability<StandCapability> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    public StandCapability(LivingEntity living) {
        super(living);
    }

    @Override
    public void sync(Entity entity) {
        super.sync(entity);
        if (entity instanceof ServerPlayer serverPlayer) {
            StandCapability.syncEntityCapability(serverPlayer, serverPlayer);
        }
    }

    private static void syncEntityCapability(Player recipient, LivingEntity standUser) {
        if (recipient instanceof ServerPlayer serverPlayer) {
            StandCapability standCapability = getCapability(standUser);
            StandType standType = standCapability.getType();
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            buf.writeInt(standUser.getId());
            buf.writeInt(standType == null ? 0 : standType.ordinal());
            buf.writeInt(standCapability.getSkin());
            standCapability.writeSyncPacket(buf, serverPlayer);
            NetworkManager.sendToPlayer(serverPlayer, STAND_S2C, buf);
        }
    }

    public static void syncEntityCapability(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof LivingEntity livingEntity) {
            if (livingEntity.level() == event.getEntity().level()) {
                syncEntityCapability(event.getEntity(), livingEntity);
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

    public static void initClient(){
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, STAND_S2C, (buf, context) -> {
            int id = buf.readInt();
            int standType = buf.readInt();
            int skin = buf.readInt();

            if (Minecraft.getInstance().level != null && Minecraft.getInstance().level.getEntity(id) instanceof LivingEntity livingEntity) {
                StandCapability.getCapabilityOptional(livingEntity).ifPresent(c -> {
                    c.setTypeAndSkin(StandType.fromOrdinal(standType), skin);
                    c.applySyncPacket(buf);
                });
            }
        });
    }
}