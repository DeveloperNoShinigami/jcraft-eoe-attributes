package net.arna.jcraft.forge.capability.impl.living;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import lombok.NonNull;
import net.arna.jcraft.common.component.impl.CommonGravityShiftComponentImpl;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.forge.capability.api.JCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;

import static net.arna.jcraft.JCraft.MOD_ID;

public class GravityShiftCapability extends CommonGravityShiftComponentImpl implements JCapability {

    public static ResourceLocation GS_S2C = new ResourceLocation(MOD_ID, "gs_s2c");

    public static Capability<GravityShiftCapability> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    public GravityShiftCapability(LivingEntity user) {
        super(user);
    }

    private static final int RANGE = (int) Math.sqrt(RANGE_SQR);
    @Override
    public void sync(Entity entity) {
        super.sync(entity);
        if (entity.level() instanceof ServerLevel serverWorld) {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            buf.writeInt(entity.getId());
            CompoundTag nbt = new CompoundTag();
            writeToNbt(nbt);
            buf.writeNbt(nbt);
            JUtils.around(serverWorld, entity.position(), RANGE).forEach(
                    recipient -> NetworkManager.sendToPlayer(recipient, GS_S2C, buf)
            );
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

    public static @NonNull LazyOptional<GravityShiftCapability> getCapabilityOptional(LivingEntity user) {
        return user.getCapability(CAPABILITY);
    }
    public static GravityShiftCapability getCapability(LivingEntity user) {
        return user.getCapability(CAPABILITY).orElse(new GravityShiftCapability(user));
    }
    public static void initClient(){
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, GS_S2C, (buf, context) -> {
            int id = buf.readInt();
            CompoundTag nbt = buf.readNbt();

            if (Minecraft.getInstance().level != null && Minecraft.getInstance().level.getEntity(id) instanceof LivingEntity living) {
                GravityShiftCapability.getCapability(living).deserializeNBT(nbt);
            }
        });
    }
}
