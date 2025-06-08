package net.arna.jcraft.fabric.datagen;

import lombok.Getter;
import net.arna.jcraft.api.JRegistries;
import net.arna.jcraft.api.pose.PoseModifiers;
import net.arna.jcraft.api.pose.modifier.IPoseModifier;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.PackOutput;

@Getter
public class JPoseProvider extends JStandTypeBasedProvider<IPoseModifier> {
    private final String name = "Poses";

    public JPoseProvider(FabricDataOutput dataOutput) {
        super(dataOutput, PackOutput.Target.RESOURCE_PACK, "poses", PoseModifiers.CODEC,
                JRegistries.STAND_TYPE_REGISTRY, IPoseModifier.class, "POSE");
    }
}
