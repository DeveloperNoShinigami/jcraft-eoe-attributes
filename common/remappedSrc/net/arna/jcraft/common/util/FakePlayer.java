package net.arna.jcraft.common.util;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.stats.Stat;
import net.minecraft.world.damagesource.DamageSource;

public class FakePlayer extends ServerPlayer {
    private static final GameProfile FAKE_PROFILE = new GameProfile(UUID.nameUUIDFromBytes("jcraft".getBytes()), "[JCraft]");

    public FakePlayer(ServerLevel world) {
        super(world.getServer(), world, FAKE_PROFILE);
        this.connection = new ServerGamePacketListenerImpl(world.getServer(), new Connection(PacketFlow.CLIENTBOUND), this);
    }

    @Override
    public void tick() {
    }

    @Override
    public void awardStat(Stat<?> stat, int incrementer) {
    }

    @Override
    public void awardStat(Stat<?> stat) {
    }

    @Override
    public void displayClientMessage(Component message, boolean actionBar) {
        super.displayClientMessage(message, actionBar);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        return true;
    }
}
