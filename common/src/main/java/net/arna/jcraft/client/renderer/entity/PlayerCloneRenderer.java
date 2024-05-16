package net.arna.jcraft.client.renderer.entity;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.vertex.PoseStack;
import net.arna.jcraft.client.rendering.CloneSkinTracker;
import net.arna.jcraft.client.util.PlayerCloneClientPlayerEntity;
import net.arna.jcraft.common.entity.PlayerCloneEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import java.util.UUID;

public class PlayerCloneRenderer extends HumanoidMobRenderer<PlayerCloneEntity, HumanoidModel<PlayerCloneEntity>> {
    private final PlayerRenderer parent;

    public PlayerCloneRenderer(EntityRendererProvider.Context ctx, boolean slim) {
        super(ctx, new PlayerModel<>(ctx.bakeLayer(slim ? ModelLayers.PLAYER_SLIM : ModelLayers.PLAYER), slim), 0.5f);
        parent = new PlayerRenderer(ctx, slim);
    }

    @Override
    public boolean shouldRender(PlayerCloneEntity clone, Frustum frustum, double d, double e, double f) {
        boolean s = super.shouldRender(clone, frustum, d, e, f);

        if (clone.shouldRenderForMaster()) {
            return s;
        }

        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            UUID masterId = clone.getMasterId();
            if (masterId == null) {
                return s;
            }
            return !masterId.equals(player.getUUID());
        }
        return s;
    }

    @Override
    public void render(PlayerCloneEntity clone, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i) {
        PlayerCloneClientPlayerEntity clonePlayer = CloneSkinTracker.toPlayer(clone);
        if (clonePlayer == null) {
            return;
        }
        parent.render(clonePlayer, f, g, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public ResourceLocation getTextureLocation(PlayerCloneEntity entity) {
        return CloneSkinTracker.getSkinFor(entity, MinecraftProfileTexture.Type.SKIN);
    }
}

