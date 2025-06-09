package net.arna.jcraft.fabric.api;

import net.arna.jcraft.api.spec.SpecData;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider;
import net.minecraft.data.PackOutput;
import org.jetbrains.annotations.NotNull;

public abstract class JCraftSpecDataProvider extends FabricCodecDataProvider<SpecData> {
    public JCraftSpecDataProvider(FabricDataOutput dataOutput) {
        super(dataOutput, PackOutput.Target.DATA_PACK, "specs", SpecData.CODEC);
    }

    @Override
    public @NotNull String getName() {
        return "Spec Data";
    }
}
