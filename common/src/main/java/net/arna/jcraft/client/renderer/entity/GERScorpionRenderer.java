package net.arna.jcraft.client.renderer.entity;

import lombok.NonNull;
import mod.azure.azurelib.animation.AzAnimator;
import mod.azure.azurelib.animation.controller.AzAnimationController;
import mod.azure.azurelib.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.animation.impl.AzEntityAnimator;
import mod.azure.azurelib.render.entity.AzEntityRenderer;
import mod.azure.azurelib.render.entity.AzEntityRendererConfig;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.client.model.entity.GERScorpionModel;
import net.arna.jcraft.common.entity.GERScorpionEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * The {@link AbstractEntityRenderer} for {@link GERScorpionEntity}.
 */
@Environment(EnvType.CLIENT)
public class GERScorpionRenderer extends AbstractEntityRenderer<GERScorpionEntity> {
    
    public static final String ID = "gerscorpion";

    // TODO fix this
    private static final ResourceLocation
            texture = JCraft.id("textures/entity/rock.png"),
            rock = JCraft.id("textures/entity/gerscorpion.png");

    protected GERScorpionRenderer(final @NonNull EntityRendererProvider.Context context) {
        super(context, () -> new EntityAnimator<>(ID), ID);
    }
}
