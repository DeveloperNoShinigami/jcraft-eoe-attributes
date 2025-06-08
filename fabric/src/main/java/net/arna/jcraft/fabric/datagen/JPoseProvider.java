package net.arna.jcraft.fabric.datagen;

import lombok.Getter;
import net.arna.jcraft.api.pose.ModelType;
import net.arna.jcraft.api.pose.PoseModifiers;
import net.arna.jcraft.api.pose.modifier.IPoseModifier;
import net.arna.jcraft.common.entity.stand.*;
import net.arna.jcraft.registry.JStandTypeRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiConsumer;

@Getter
public class JPoseProvider extends FabricCodecDataProvider<IPoseModifier> {
    private final String name = "Poses";

    public JPoseProvider(FabricDataOutput dataOutput) {
        super(dataOutput, PackOutput.Target.RESOURCE_PACK, "poses", PoseModifiers.CODEC);
    }

    @Override
    protected void configure(BiConsumer<ResourceLocation, IPoseModifier> provider) {
        registerPoses((id, modelType, pose) -> {
            if (!pose.isModelSupported(modelType)) {
                throw new IllegalArgumentException("Pose " + id + " is not supported by model type " + modelType);
            }

            ResourceLocation absId = id.withPath(p -> modelType.getName() + "/" + p);
            provider.accept(absId, pose);
        });
    }

    private void registerPoses(PoseRegistrar registrar) {
        registrar.register(JStandTypeRegistry.GOLD_EXPERIENCE_REQUIEM.getId(), ModelType.HUMANOID, GEREntity.POSE);
        registrar.register(JStandTypeRegistry.KING_CRIMSON.getId(), ModelType.HUMANOID, KingCrimsonEntity.POSE);
        registrar.register(JStandTypeRegistry.KILLER_QUEEN_BITES_THE_DUST.getId(), ModelType.HUMANOID, KQBTDEntity.POSE);
        registrar.register(JStandTypeRegistry.KILLER_QUEEN.getId(), ModelType.HUMANOID, KillerQueenEntity.POSE);
        registrar.register(JStandTypeRegistry.STAR_PLATINUM.getId(), ModelType.HUMANOID, StarPlatinumEntity.POSE);
        registrar.register(JStandTypeRegistry.THE_WORLD_OVER_HEAVEN.getId(), ModelType.HUMANOID, TheWorldOverHeavenEntity.POSE);
    }

    @FunctionalInterface
    private interface PoseRegistrar {
        void register(ResourceLocation id, ModelType<?> modelType, IPoseModifier pose);
    }
}
