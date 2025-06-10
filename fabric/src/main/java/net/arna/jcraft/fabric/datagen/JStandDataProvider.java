package net.arna.jcraft.fabric.datagen;

import lombok.Getter;
import net.arna.jcraft.api.JRegistries;
import net.arna.jcraft.api.stand.StandData;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.PackOutput;

@Getter
public class JStandDataProvider extends JStandTypeBasedConstantProvider<StandData> {
    private final String name = "Stand Data";

    public JStandDataProvider(FabricDataOutput dataOutput) {
        super(dataOutput, PackOutput.Target.DATA_PACK, "stands", StandData.CODEC,
                JRegistries.STAND_TYPE_REGISTRY,  StandData.class, "DATA");
    }
}
