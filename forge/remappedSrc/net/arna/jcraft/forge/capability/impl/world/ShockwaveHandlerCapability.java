package net.arna.jcraft.forge.capability.impl.world;


import net.arna.jcraft.common.component.impl.player.CommonPhComponentImpl;
import net.arna.jcraft.common.component.impl.world.CommonShockwaveHandlerComponentImpl;
import net.arna.jcraft.forge.capability.api.JCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;

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
}