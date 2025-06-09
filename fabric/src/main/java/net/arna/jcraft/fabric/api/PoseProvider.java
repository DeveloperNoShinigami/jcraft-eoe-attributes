package net.arna.jcraft.fabric.api;

import net.arna.jcraft.api.pose.ModelType;
import net.arna.jcraft.api.pose.PoseModifiers;
import net.arna.jcraft.api.pose.modifier.IPoseModifier;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

public abstract class PoseProvider extends FabricCodecDataProvider<IPoseModifier> {
    protected PoseProvider(FabricDataOutput dataOutput) {
        super(dataOutput, PackOutput.Target.RESOURCE_PACK, "poses", PoseModifiers.CODEC);
    }

    @Override
    protected final void configure(BiConsumer<ResourceLocation, IPoseModifier> provider) {
        registerPoses((id, modelType, pose) -> {
            if (!pose.isModelSupported(modelType)) {
                throw new IllegalArgumentException("Pose " + id + " is not supported by model type " + modelType);
            }

            ResourceLocation absId = id.withPath(p -> p + "/" + modelType.getName());
            provider.accept(absId, pose);
        });
    }

    protected abstract void registerPoses(PoseRegistrar registrar);

    @Override
    public @NotNull String getName() {
        return "Poses";
    }

    @FunctionalInterface
    public interface PoseRegistrar {
        void register(ResourceLocation id, ModelType<?> modelType, IPoseModifier pose);
    }
}
