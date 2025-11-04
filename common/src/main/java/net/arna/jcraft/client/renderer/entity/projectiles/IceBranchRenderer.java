package net.arna.jcraft.client.renderer.entity.projectiles;

import lombok.NonNull;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.client.model.entity.IceBranchModel;
import net.arna.jcraft.client.renderer.entity.AbstractEntityRenderer;
import net.arna.jcraft.common.entity.projectile.IceBranchProjectile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * The {@link AbstractEntityRenderer} for {@link IceBranchProjectile}.
 * @see IceBranchModel
 */
@Environment(EnvType.CLIENT)
public class IceBranchRenderer extends AbstractEntityRenderer<IceBranchProjectile> {

    public static final String ID = "ice_branch";

    // TODO fix skins
    public IceBranchRenderer(final @NonNull EntityRendererProvider.Context context) {
        super(context, () -> new EntityAnimator<>(ID), b -> b
                .setRenderType(RenderType.entityTranslucent(JCraft.id(TEXTURE_STR_TEMPLATE.formatted(ID)))),
                ID);
    }

}
