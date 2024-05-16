package net.arna.jcraft.forge.capability.impl.world;


import dev.architectury.networking.NetworkManager;
import net.arna.jcraft.common.component.impl.player.CommonPhComponentImpl;
import net.arna.jcraft.common.component.impl.world.CommonShockwaveHandlerComponentImpl;
import net.arna.jcraft.forge.capability.api.JCapability;
import net.arna.jcraft.forge.capability.impl.living.StandCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;

import java.util.UUID;

import static net.arna.jcraft.JCraft.MOD_ID;

public class ShockwaveHandlerCapability extends CommonShockwaveHandlerComponentImpl implements JCapability {

    public static ResourceLocation SHOCK_S2C = new ResourceLocation(MOD_ID, "shock_s2c");
    public static ResourceLocation SHOCK_C2S = new ResourceLocation(MOD_ID, "shock_c2s");

    public static Capability<ShockwaveHandlerCapability> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    public ShockwaveHandlerCapability(Level world) {
        super(world);
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


    public static LazyOptional<ShockwaveHandlerCapability> getCapabilityOptional(Level world) {
        return world.getCapability(CAPABILITY);
    }

    public static ShockwaveHandlerCapability getCapability(Level world) {
        return world.getCapability(CAPABILITY).orElse(new ShockwaveHandlerCapability(world));
    }

    public static void initNetwork(){
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, SHOCK_S2C, (buf, context) -> {
            UUID uuid = buf.readUUID();
            CompoundTag nbt = buf.readNbt();
            Player player = null;
            if (Minecraft.getInstance().level != null) {
                player = Minecraft.getInstance().level.getPlayerByUUID(uuid);
            }
            if (player != null) {
                StandCapability.getCapabilityOptional(player).ifPresent(c -> c.deserializeNBT(nbt));
            }
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, SHOCK_C2S, (buf, context) -> {
            UUID uuid = buf.readUUID();
            CompoundTag nbt = buf.readNbt();
            StandCapability.getCapabilityOptional(Minecraft.getInstance().level.getPlayerByUUID(uuid)).ifPresent(c -> c.deserializeNBT(nbt));
        });
    }
}