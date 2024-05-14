package net.arna.jcraft.forge.capability.api;

import net.arna.jcraft.forge.JCraftForge;
import net.arna.jcraft.forge.network.SyncPlayerC2SPacket;
import net.arna.jcraft.forge.network.SyncPlayerS2CPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.network.PacketDistributor;

import java.util.Optional;

public interface JCapability extends INBTSerializable<NbtCompound> {

}