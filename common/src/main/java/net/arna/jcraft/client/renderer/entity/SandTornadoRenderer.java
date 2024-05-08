package net.arna.jcraft.client.renderer.entity;

import net.arna.jcraft.client.model.entity.SandTornadoModel;
import net.arna.jcraft.common.entity.projectile.SandTornadoEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SandTornadoRenderer extends GeoEntityRenderer<SandTornadoEntity> {

    @Override
    public RenderLayer getRenderType(SandTornadoEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return RenderLayer.getEntityTranslucent(getTextureLocation(animatable));
    }

    public SandTornadoRenderer(EntityRendererFactory.Context renderManagerIn) {
        super(renderManagerIn, new SandTornadoModel());
        this.shadowRadius = 1.1f;
    }
}
