package net.arna.jcraft.client.renderer.entity.projectiles;

import net.arna.jcraft.client.model.entity.RapierModel;
import net.arna.jcraft.common.entity.projectile.RapierProjectile;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RapierRenderer extends GeoEntityRenderer<RapierProjectile> {

    public RapierRenderer(EntityRendererFactory.Context renderManagerIn) {
        super(renderManagerIn, new RapierModel());
    }

    @Override
    public RenderLayer getRenderType(RapierProjectile animatable, Identifier texture, VertexConsumerProvider bufferSource, float partialTick) {
        return RenderLayer.getEntityTranslucent(texture);
    }
}
