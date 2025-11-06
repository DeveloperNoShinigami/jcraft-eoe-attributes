package net.arna.jcraft.client.renderer.entity.projectiles;

import lombok.NonNull;
import mod.azure.azurelib.render.entity.AzEntityRendererConfig;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.client.renderer.entity.AbstractEntityRenderer;
import net.arna.jcraft.common.entity.projectile.IceBranchProjectile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.stream.IntStream;

/**
 * The {@link AbstractEntityRenderer} for {@link IceBranchProjectile}.
 */
@Environment(EnvType.CLIENT)
public class IceBranchRenderer extends AbstractEntityRenderer<IceBranchProjectile> {
    protected static final List<ResourceLocation> VARIANTS = IntStream.range(0, 3).mapToObj(
            i -> JCraft.id("textures/entity/ice_branch/ice_branch_" + i + ".png")).toList();

    public static final String ID = "ice_branch";

    public IceBranchRenderer(final @NonNull EntityRendererProvider.Context context) {
        super(AzEntityRendererConfig.<IceBranchProjectile>builder(
                entity -> JCraft.id(AbstractEntityRenderer.MODEL_STR_TEMPLATE.formatted(ID)),
                entity -> VARIANTS.get(entity.getId() % 3))
                .setAnimatorProvider(() -> new EntityAnimator<>(ID))
                .build(),
                context, ID);
    }

}
