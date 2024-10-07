package net.arna.jcraft.client.rendering;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import lombok.experimental.UtilityClass;
import net.arna.jcraft.client.util.PlayerCloneClientPlayerEntity;
import net.arna.jcraft.common.entity.PlayerCloneEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import java.util.*;

@UtilityClass
public class CloneSkinTracker {
    private static final Map<PlayerCloneEntity, Map<MinecraftProfileTexture.Type, ResourceLocation>> skinCache = new WeakHashMap<>();
    private static final Map<PlayerCloneEntity, String> modelCache = new WeakHashMap<>();
    private static final Map<PlayerCloneEntity, PlayerCloneClientPlayerEntity> playerCache = new WeakHashMap<>();
    private static final Set<PlayerCloneEntity> loading = Collections.newSetFromMap(new WeakHashMap<>());

    public static ResourceLocation getSkinFor(final PlayerCloneEntity clone, final MinecraftProfileTexture.Type type) {
        if (!skinCache.containsKey(clone)) {
            load(clone);
        }
        final ResourceLocation skin = skinCache.getOrDefault(clone, Collections.emptyMap()).get(type);
        return skin == null && type == MinecraftProfileTexture.Type.SKIN ? DefaultPlayerSkin.getDefaultSkin(clone.getMasterId()) : skin;
    }

    public static String getModelFor(final PlayerCloneEntity clone) {
        if (!skinCache.containsKey(clone)) {
            load(clone);
        }
        return modelCache.getOrDefault(clone, clone.getMasterId() == null ? "default" : DefaultPlayerSkin.getSkinModelName(clone.getMasterId()));
    }

    public static PlayerCloneClientPlayerEntity toPlayer(final PlayerCloneEntity clone) {
        if (clone.getGameProfile() == null) {
            return null;
        }
        final PlayerCloneClientPlayerEntity clonePlayer = playerCache.computeIfAbsent(clone, PlayerCloneClientPlayerEntity::new);
        clonePlayer.updateData();
        return clonePlayer;
    }

    private static void load(final PlayerCloneEntity clone) {
        GameProfile profile = clone.getGameProfile();
        if (profile == null) {
            return;
        }

        synchronized (loading) {
            if (loading.contains(clone)) {
                return;
            }
            loading.add(clone);
        }

        Minecraft.getInstance().getSkinManager().registerSkins(profile, (type, id, texture) -> {
            skinCache.computeIfAbsent(clone, c -> new HashMap<>()).put(type, id);

            synchronized (loading) {
                loading.remove(clone);
            }

            String model;
            if (type != MinecraftProfileTexture.Type.SKIN || (model = texture.getMetadata("model")) == null) {
                return;
            }
            modelCache.put(clone, model);
        }, true);
    }
}
