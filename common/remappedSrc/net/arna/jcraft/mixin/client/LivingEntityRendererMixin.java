package net.arna.jcraft.mixin.client;

import net.arna.jcraft.client.registry.JRenderLayerRegistry;
import net.arna.jcraft.client.renderer.features.ArmoredMoveFeatureRenderer;
import net.arna.jcraft.client.renderer.features.StuckKnivesFeatureRenderer;
import net.arna.jcraft.client.util.PlayerCloneClientPlayerEntity;
import net.arna.jcraft.common.entity.stand.KingCrimsonEntity;
import net.arna.jcraft.common.util.JUtils;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements RenderLayerParent<T, M> {

    @Shadow
    protected M model;

    @Shadow
    @Final
    protected List<RenderLayer<T, M>> features;

    protected LivingEntityRendererMixin(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @SuppressWarnings("unchecked")
    @Inject(at = @At("RETURN"), method = "<init>")
    private void addFeatureRenderers(EntityRendererProvider.Context ctx, EntityModel<?> model, float shadowRadius, CallbackInfo ci) {
        if (model instanceof AgeableListModel<?>) // StuckKnives
        {
            addFeature((RenderLayer<T, M>) new StuckKnivesFeatureRenderer<>(ctx, (LivingEntityRenderer<T, ? extends AgeableListModel<T>>) (Object) this));
        }
        if (model != null) {
            addFeature((RenderLayer<T, M>) new ArmoredMoveFeatureRenderer<>(ctx, (LivingEntityRenderer<T, ? extends EntityModel<T>>) (Object) this));
        }
    }

    @Inject(method = "hasLabel(Lnet/minecraft/entity/LivingEntity;)Z", at = @At("HEAD"), cancellable = true)
    private void doNotRenderCloneLabel(T livingEntity, CallbackInfoReturnable<Boolean> cir) {
        if (livingEntity instanceof PlayerCloneClientPlayerEntity) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lnet/minecraft/client/util/math/MatrixStack;pop()V"))
    private void suckmahballs(T livingEntity, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, CallbackInfo ci) {
        if (true || !(JUtils.getStand((LivingEntity) (Object) this) instanceof KingCrimsonEntity kc) || kc.getState() != KingCrimsonEntity.State.PREDICT ||
                kc.getMoveStun() > (KingCrimsonEntity.PREDICTION.getWindupPoint())) {
            return;
        }

        RenderType renderLayer = JRenderLayerRegistry.RRRE;
        if (renderLayer != null) {
            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(renderLayer);
            int o = LivingEntityRenderer.getOverlayCoords(livingEntity, this.getAnimationProgress(livingEntity, g));
            this.model.renderToBuffer(matrixStack, vertexConsumer, i, o, 1, 1, 1, 1);
        }

        for (RenderLayer<T, M> featureRenderer : features) {
            //TODO we got all funny features here planet suckondeeze

        }
    }

    @Shadow
    protected abstract float getAnimationProgress(T entity, float tickDelta);

    @Shadow
    protected abstract boolean addFeature(RenderLayer<T, M> feature);
}
