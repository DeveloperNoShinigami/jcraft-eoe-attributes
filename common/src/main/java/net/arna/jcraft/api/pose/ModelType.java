package net.arna.jcraft.api.pose;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import lombok.Getter;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ModelType<T extends Model> {
    private static final Map<String, ModelType<?>> fromName = new HashMap<>();
    private static final Map<Class<? extends Model>, ModelType<?>> fromClass = new HashMap<>();

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
                ModelType<?> modelType = fromName.get(name);
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

    private ModelType(String name, Class<T> modelClass, Map<String, Function<T, ModelPart>> parts) {
        fromName.put(name, this);
        fromClass.put(modelClass, this);
        this. name = name;
        this.modelClass = modelClass;
        this.parts = ImmutableMap.copyOf(parts);
    }

    private static <T extends Model> Class<T> castClass(Class<?> clazz) {
        //noinspection unchecked
        return (Class<T>) clazz;
    }

    @Nullable
    public static ModelType<?> fromName(String name) {
        return fromName.get(name);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Model> ModelType<T> fromClass(Class<? extends T> modelClass) {
        ModelType<?> modelType = fromClass.get(modelClass);
        if (modelType != null) {
            return (ModelType<T>) modelType;
        }

        for (Map.Entry<Class<? extends Model>, ModelType<?>> entry : fromClass.entrySet()) {
            if (entry.getKey().isAssignableFrom(modelClass)) {
                fromClass.put(modelClass, entry.getValue());
                return (ModelType<T>) entry.getValue();
            }
        }

        throw new IllegalArgumentException("Model type for class " + modelClass.getSimpleName() + " not found");
    }

    public boolean hasPart(String partName) {
        return parts.containsKey(partName);
    }

    public ModelPart getPart(T model, String partName) {
        Function<T, ModelPart> partFunction = parts.get(partName);
        if (partFunction != null) {
            return partFunction.apply(model);
        }

        throw new IllegalArgumentException("Part " + partName + " not found in model " + modelClass.getSimpleName());
    }
}
