package net.arna.jcraft.client.gravity.api;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.client.gravity.util.GravityChannelClient;
import net.arna.jcraft.common.gravity.api.RotationParameters;
import net.arna.jcraft.common.gravity.util.EntityTags;
import net.arna.jcraft.common.gravity.util.Gravity;
import net.arna.jcraft.common.gravity.util.packet.DefaultGravityPacket;
import net.arna.jcraft.common.gravity.util.packet.InvertGravityPacket;
import net.arna.jcraft.common.gravity.util.packet.OverwriteGravityPacket;
import net.arna.jcraft.common.gravity.util.packet.UpdateGravityPacket;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import java.util.ArrayList;

@Environment(EnvType.CLIENT)
public class GravityChangerAPIClient {

    public static void addGravityClient(final LocalPlayer entity, final Gravity gravity, final ResourceLocation verifier, final FriendlyByteBuf verifierInfo) {
        if (onWrongSide(entity) || !EntityTags.canChangeGravity(entity)) {
            return;
        }
        JComponentPlatformUtils.getGravity(entity).ifPresent(gc -> {
            gc.addGravity(gravity, false);
            GravityChannelClient.UPDATE_GRAVITY.sendToServer(new UpdateGravityPacket(gravity, false), verifier, verifierInfo);
        });
    }

    public static void setGravityClient(final LocalPlayer entity, final ArrayList<Gravity> gravity, final ResourceLocation verifier, final FriendlyByteBuf verifierInfo) {
        if (onWrongSide(entity) || !EntityTags.canChangeGravity(entity)) {
            return;
        }
        JComponentPlatformUtils.getGravity(entity).ifPresent(gc -> {
            gc.setGravity(gravity, false);
            GravityChannelClient.OVERWRITE_GRAVITY.sendToServer(new OverwriteGravityPacket(gravity, false), verifier, verifierInfo);
        });
    }

    public static void setIsInvertedClient(final LocalPlayer entity, final boolean isInverted, final RotationParameters rotationParameters, final ResourceLocation verifier, final FriendlyByteBuf verifierInfo) {
        if (onWrongSide(entity) || !EntityTags.canChangeGravity(entity)) {
            return;
        }
        JComponentPlatformUtils.getGravity(entity).ifPresent(gc -> {
            gc.invertGravity(isInverted, rotationParameters, false);
            GravityChannelClient.INVERT_GRAVITY.sendToServer(new InvertGravityPacket(isInverted, rotationParameters, false), verifier, verifierInfo);
        });
    }

    public static void clearGravityClient(final LocalPlayer entity, final RotationParameters rotationParameters, final ResourceLocation verifier, final FriendlyByteBuf verifierInfo) {
        if (onWrongSide(entity) || !EntityTags.canChangeGravity(entity)) {
            return;
        }
        JComponentPlatformUtils.getGravity(entity).ifPresent(gc -> {
            gc.clearGravity(rotationParameters, false);
            GravityChannelClient.OVERWRITE_GRAVITY.sendToServer(new OverwriteGravityPacket(new ArrayList<>(), false), verifier, verifierInfo);
        });
    }

    public static void setDefaultGravityDirectionClient(final LocalPlayer entity, final Direction gravityDirection, final RotationParameters rotationParameters, final ResourceLocation verifier, final FriendlyByteBuf verifierInfo) {
        if (onWrongSide(entity) || !EntityTags.canChangeGravity(entity)) {
            return;
        }
        JComponentPlatformUtils.getGravity(entity).ifPresent(gc -> {
            gc.setDefaultGravityDirection(gravityDirection, rotationParameters, false);
            GravityChannelClient.DEFAULT_GRAVITY.sendToServer(new DefaultGravityPacket(gravityDirection, rotationParameters, false), verifier, verifierInfo);
        });
    }

    private static boolean onWrongSide(final Entity entity) {
        if (!entity.level().isClientSide) {
            JCraft.LOGGER.error("GravityChangerAPI function cannot be called from the server, use dedicated server class. ", new Exception());
            return true;
        }
        return false;
    }
}
