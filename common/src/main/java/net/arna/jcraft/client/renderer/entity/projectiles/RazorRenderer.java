package net.arna.jcraft.client.renderer.entity.projectiles;

import lombok.NonNull;
import mod.azure.azurelib.render.entity.AzEntityRendererConfig;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.projectile.RazorProjectile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

/**
 * The {@link ProjectileRenderer} for {@link RazorProjectile}.
 */
@Environment(EnvType.CLIENT)
public class RazorRenderer extends ProjectileRenderer<RazorProjectile> {

    protected static final Map<Integer, ResourceLocation> SKINS = Map.of(
            0, JCraft.id("textures/entity/projectiles/razor.png"),
            1, JCraft.id("textures/entity/projectiles/nail.png"),
            2, JCraft.id("textures/entity/projectiles/scissors.png"));

    public static final String ID = "razor";

    public RazorRenderer(final @NonNull EntityRendererProvider.Context context) {
        super(AzEntityRendererConfig.<RazorProjectile>builder(
                entity -> JCraft.id(MODEL_STR_TEMPLATE.formatted(ID)),
                entity -> SKINS.get(entity.getId() % 3)
                )
                .setRenderType(RenderType.entityTranslucent(JCraft.id(TEXTURE_STR_TEMPLATE.formatted(ID))))
                .build(),
                context, ID);
    }

}