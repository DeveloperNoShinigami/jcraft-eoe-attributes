package net.arna.jcraft.forge.capability.impl.world;


import net.arna.jcraft.common.component.impl.player.CommonPhComponentImpl;
import net.arna.jcraft.common.component.impl.world.CommonShockwaveHandlerComponentImpl;
import net.arna.jcraft.forge.capability.api.JCapability;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;

import static net.arna.jcraft.JCraft.MOD_ID;

public class ShockwaveHandlerCapability extends CommonShockwaveHandlerComponentImpl implements JCapability {

    public static Identifier SHOCK_S2C = new Identifier(MOD_ID, "shock_s2c");
    public static Identifier SHOCK_C2S = new Identifier(MOD_ID, "shock_c2s");

    public static Capability<ShockwaveHandlerCapability> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    public ShockwaveHandlerCapability(World world) {
        super(world);
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


    public static LazyOptional<ShockwaveHandlerCapability> getCapabilityOptional(World world) {
        return world.getCapability(CAPABILITY);
    }

    public static ShockwaveHandlerCapability getCapability(World world) {
        return world.getCapability(CAPABILITY).orElse(new ShockwaveHandlerCapability(world));
    }
}