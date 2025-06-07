package net.arna.jcraft.fabric.datagen;

import lombok.Getter;
import net.arna.jcraft.api.JRegistries;
import net.arna.jcraft.api.stand.StandData;
import net.arna.jcraft.api.stand.StandType;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

@Getter
public class JStandDataProvider extends JAttackerDataProvider<StandType, StandData> {
    private final String name = "Stand Data";

    public JStandDataProvider(FabricDataOutput dataOutput) {
        super(dataOutput, "stands", StandData.CODEC, JRegistries.STAND_TYPE_REGISTRY);
    }

    @Override
    protected Class<?> getHolderClass(StandType type) {
        return type.createEntity(null).getClass();
    }
}
