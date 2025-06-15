package net.arna.jcraft.api.pose;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

public class ModelType<T extends Model> {
    private static final Map<String, ModelType<?>> FROM_NAME = new HashMap<>();
    private static final Map<Class<? extends Model>, ModelType<?>> FROM_CLASS = new HashMap<>();

    public static final ModelType<HumanoidModel<?>> HUMANOID = new ModelType<>("humanoid", castClass(HumanoidModel.class), Map.of(
            "head", m -> m.head,
            "hat", m -> m.hat,
            "body", m -> m.body,
            "rightArm", m -> m.rightArm,
            "leftArm", m -> m.leftArm,
            "rightLeg", m -> m.rightLeg,
            "leftLeg", m -> m.leftLeg
    ));

    public static final Codec<ModelType<?>> CODEC = Codec.STRING.comapFlatMap(
            name -> {
                ModelType<?> modelType = FROM_NAME.get(name);
                if (modelType != null) {
                    return DataResult.success(modelType);
                }
                return DataResult.error(() -> "Model type " + name + " not found");
            },
            ModelType::getName
    );

    @Getter
    private final String name;
    @Getter
    private final Class<T> modelClass;
    private final Map<String, Function<T, ModelPart>> parts;

    private ModelType(final String name, final Class<T> modelClass, final Map<String, Function<T, ModelPart>> parts) {
        FROM_NAME.put(name.toLowerCase(Locale.ROOT), this);
        FROM_CLASS.put(modelClass, this);
        this.name = name;
        this.modelClass = modelClass;
        this.parts = ImmutableMap.copyOf(parts);
    }

    private static <T extends Model> Class<T> castClass(final Class<?> clazz) {
        //noinspection unchecked
        return (Class<T>) clazz;
    }

    /**
     * Checks whether the given entity supports pose modification.
     * @param entity The entity to check.
     * @return {@code true} if the entity's model is supported, {@code false} otherwise.
     */
    public static boolean isSupported(final LivingEntity entity) {
        EntityRenderer<? super LivingEntity> renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entity);

        // Only living entities are supported, all (vanilla) living entities should have a renderer extending this.
        if (!(renderer instanceof LivingEntityRenderer<?,?> livingRenderer)) {
            return false;
        }

        ModelType<?> modelType = fromClassRaw(livingRenderer.getModel().getClass());
        return modelType != null;
    }

    public static Map<String, ModelType<?>> getAllModelTypes() {
        return ImmutableMap.copyOf(FROM_NAME);
    }

    @Nullable
    public static ModelType<?> fromName(final String name) {
        return FROM_NAME.get(name.toLowerCase(Locale.ROOT));
    }

    @SuppressWarnings("unchecked")
    private static <T extends Model> ModelType<T> fromClassRaw(final Class<? extends T> modelClass) {
        ModelType<?> modelType = FROM_CLASS.get(modelClass);
        if (modelType != null) {
            return (ModelType<T>) modelType;
        }

        for (final Map.Entry<Class<? extends Model>, ModelType<?>> entry : FROM_CLASS.entrySet()) {
            if (entry.getKey().isAssignableFrom(modelClass)) {
                FROM_CLASS.put(modelClass, entry.getValue());
                return (ModelType<T>) entry.getValue();
            }
        }

        return null;
    }

    public static <T extends Model> ModelType<T> fromClass(final Class<? extends T> modelClass) {
        ModelType<T> modelType = fromClassRaw(modelClass);

        if (modelType == null) {
            throw new IllegalArgumentException("Model type for class " + modelClass.getSimpleName() + " not found");
        }

        return modelType;
    }

    public boolean hasPart(final String partName) {
        return parts.containsKey(partName);
    }

    public ModelPart getPart(final T model, final String partName) {
        final Function<T, ModelPart> partFunction = parts.get(partName);
        if (partFunction != null) {
            return partFunction.apply(model);
        }

        throw new IllegalArgumentException("Part " + partName + " not found in model " + modelClass.getSimpleName());
    }
}
