package net.arna.jcraft.api.pose;

import com.mojang.serialization.Codec;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;

public record ModelPartGetter(String partName) {
    public static final Codec<ModelPartGetter> CODEC = Codec.STRING.xmap(ModelPartGetter::new, ModelPartGetter::partName);

    public ModelPart getPart(Model model) {
        return getPart(model, partName);
    }

    // Helper method to get past generic type checks.
    @SuppressWarnings("unchecked")
    private static <T extends Model> ModelPart getPart(T model, String partName) {
        Class<T> modelClass = (Class<T>) model.getClass();
        return ModelType.fromClass(modelClass).getPart(model, partName);
    }
}
