package net.arna.jcraft.client.renderer.entity;

import lombok.NonNull;
import mod.azure.azurelib.animation.AzAnimator;
import mod.azure.azurelib.animation.controller.AzAnimationController;
import mod.azure.azurelib.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.animation.impl.AzEntityAnimator;
import mod.azure.azurelib.render.entity.AzEntityRenderer;
import mod.azure.azurelib.render.entity.AzEntityRendererConfig;
import net.arna.jcraft.JCraft;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

@Environment(EnvType.CLIENT)
public abstract class AbstractEntityRenderer<T extends Entity> extends AzEntityRenderer<T> {

    protected static final String MODEL_STR_TEMPLATE = "geo/%s.geo.json";
    protected static final String TEXTURE_STR_TEMPLATE = "textures/entity/%s.png";
    protected static final String ANIMATION_STR_TEMPLATE = "animations/%s.animation.json";

    /**
     * String ID of the base controller.
     */
    public static final String BASE_CONTROLLER = "base_controller";

    /**
     * Path to the model to be used for this entity.
     */
    protected final @NonNull ResourceLocation model;
    /**
     * Path to the texture to be used for this entity.
     */
    protected final @NonNull ResourceLocation texture;

    /**
     * Constructs a renderer with a fully customizable config and the given model/texture paths.
     */
    protected AbstractEntityRenderer(final @NonNull AzEntityRendererConfig<T> config, final @NonNull EntityRendererProvider.Context context,
                                     final @NonNull ResourceLocation model, final @NonNull ResourceLocation texture) {
        super(config, context);
        this.model = model;
        this.texture = texture;
    }

    /**
     * Constructs a renderer with a fully customizable config and model/texture paths based on the specified ID.
     *
     * <ul>
     * <li>Resulting model path will be equivalent to <code>JCraft.id("geo/" + id + ".geo.json")</code></li>
     * <li>Resulting texture path will be equivalent to <code>JCraft.id("textures/entity/" + id + ".png")</code></li>
     * </ul>
     */
    protected AbstractEntityRenderer(final @NonNull AzEntityRendererConfig<T> config, final @NonNull EntityRendererProvider.Context context, final @NonNull String id) {
        this(config, context, JCraft.id(MODEL_STR_TEMPLATE.formatted(id)), JCraft.id(TEXTURE_STR_TEMPLATE.formatted(id)));
    }

    /**
     * Constructs a renderer with a simple config based on the {@link AzAnimator} {@link Supplier} and the given model/texture paths.
     */
    protected AbstractEntityRenderer(final @NonNull EntityRendererProvider.Context context, final @NonNull Supplier<AzAnimator<UUID, T>> animatorSupplier,
                                     final @NonNull ResourceLocation model, final @NonNull ResourceLocation texture) {
        this(AzEntityRendererConfig.<T>builder(model, texture).setAnimatorProvider(animatorSupplier).build(), context, model, texture);
    }

    /**
     * Constructs a renderer with a simple config based on the {@link AzAnimator} {@link Supplier} and model/texture paths based on the specified ID.
     *
     * <ul>
     * <li>Resulting model path will be equivalent to <code>JCraft.id("geo/" + id + ".geo.json")</code></li>
     * <li>Resulting texture path will be equivalent to <code>JCraft.id("textures/entity/" + id + ".png")</code></li>
     * </ul>
     */
    protected AbstractEntityRenderer(final @NonNull EntityRendererProvider.Context context, final @NonNull Supplier<AzAnimator<UUID, T>> animatorSupplier, final @NonNull String id) {
        this(context, animatorSupplier, JCraft.id(MODEL_STR_TEMPLATE.formatted(id)), JCraft.id(TEXTURE_STR_TEMPLATE.formatted(id)));
    }

    /**
     * Constructs a renderer with a config based on the {@link AzAnimator} {@link Supplier} and the {@link mod.azure.azurelib.render.entity.AzEntityRendererConfig.Builder} {@link UnaryOperator}, and model/texture paths based on the specified ID.
     *
     * <ul>
     * <li>Resulting model path will be equivalent to <code>JCraft.id("geo/" + id + ".geo.json")</code></li>
     * <li>Resulting texture path will be equivalent to <code>JCraft.id("textures/entity/" + id + ".png")</code></li>
     * </ul>
     */
    protected AbstractEntityRenderer(final @NonNull EntityRendererProvider.Context context, final @NonNull Supplier<AzAnimator<UUID, T>> animatorSupplier, final @NonNull UnaryOperator<AzEntityRendererConfig.Builder<T>> additionalConfigs, final @NonNull String id) {
        this(additionalConfigs.apply(AzEntityRendererConfig.<T>builder(JCraft.id(MODEL_STR_TEMPLATE.formatted(id)), JCraft.id(TEXTURE_STR_TEMPLATE.formatted(id))).setAnimatorProvider(animatorSupplier)).build(),
                context, JCraft.id(MODEL_STR_TEMPLATE.formatted(id)), JCraft.id(TEXTURE_STR_TEMPLATE.formatted(id)));
    }

    @Override
    public @NonNull ResourceLocation getTextureLocation(final @NonNull T entity) {
        return texture;
    }

    /**
     * Basic {@link AzEntityAnimator} implementation that can be used or extended for all kinds of {@link Entity} animators.
     */
    public static class EntityAnimator<T extends Entity> extends AzEntityAnimator<T> {

        /**
         * Path to the animation to be used for this entity.
         */
        protected final @NonNull ResourceLocation animation;

        /**
         * Constructs an animator with the given animation path.
         */
        public EntityAnimator(final @NonNull ResourceLocation animation) {
            this.animation = animation;
        }

        /**
         * Constructs an animator with the given ID.
         * <p>
         * Resulting animation path will be equivalent to <code>JCraft.id("animations/" + id + ".animation.json")</code>
         */
        public EntityAnimator(final @NonNull String id) {
            this(JCraft.id(ANIMATION_STR_TEMPLATE.formatted(id)));
        }

        @Override
        public void registerControllers(final @NonNull AzAnimationControllerContainer<T> animationControllerContainer) {
            animationControllerContainer.add(AzAnimationController.builder(this, BASE_CONTROLLER).build());
        }

        @Override
        public @NotNull ResourceLocation getAnimationLocation(final @NonNull T animatable) {
            return animation;
        }
    }
}
