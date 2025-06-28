package net.arna.jcraft.api.datagen;

import net.arna.jcraft.api.stand.StandData;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider;
import net.minecraft.data.PackOutput;
import org.jetbrains.annotations.NotNull;

public abstract class JCraftStandDataProvider extends FabricCodecDataProvider<StandData> {
    public JCraftStandDataProvider(FabricDataOutput dataOutput) {
        super(dataOutput, PackOutput.Target.DATA_PACK, "stands", StandData.CODEC);
    }

    @Override
    public @NotNull String getName() {
        return "Stand Data";
    }
}
