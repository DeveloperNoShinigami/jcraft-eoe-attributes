package net.arna.jcraft.fabric.datagen;

import lombok.Getter;
import net.arna.jcraft.api.pose.ModelType;
import net.arna.jcraft.common.entity.stand.*;
import net.arna.jcraft.fabric.api.PoseProvider;
import net.arna.jcraft.registry.JStandTypeRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

@Getter
public class JPoseProvider extends PoseProvider {
    private final String name = "Poses";

    public JPoseProvider(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    protected void registerPoses(PoseRegistrar registrar) {
        registrar.register(JStandTypeRegistry.GOLD_EXPERIENCE_REQUIEM.getId(), ModelType.HUMANOID, GEREntity.POSE);
        registrar.register(JStandTypeRegistry.KING_CRIMSON.getId(), ModelType.HUMANOID, KingCrimsonEntity.POSE);
        registrar.register(JStandTypeRegistry.KILLER_QUEEN_BITES_THE_DUST.getId(), ModelType.HUMANOID, KQBTDEntity.POSE);
        registrar.register(JStandTypeRegistry.KILLER_QUEEN.getId(), ModelType.HUMANOID, KillerQueenEntity.POSE);
        registrar.register(JStandTypeRegistry.STAR_PLATINUM.getId(), ModelType.HUMANOID, StarPlatinumEntity.POSE);
        registrar.register(JStandTypeRegistry.THE_WORLD_OVER_HEAVEN.getId(), ModelType.HUMANOID, TheWorldOverHeavenEntity.POSE);
    }
}
