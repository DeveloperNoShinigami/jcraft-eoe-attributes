package net.arna.jcraft.fabric.datagen;

import lombok.Getter;
import net.arna.jcraft.api.StandData;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiConsumer;

@Getter
public class JStandDataProvider extends FabricCodecDataProvider<StandData> {
    private final String name = "Stand Data Provider";

    public JStandDataProvider(final FabricDataOutput dataOutput) {
        super(dataOutput, PackOutput.Target.DATA_PACK, "stands", StandData.CODEC);
    }

    @Override
    protected void configure(final BiConsumer<ResourceLocation, StandData> provider) {
        // TODO
    }
}
