package net.arna.jcraft.datagen.providers.data;

import lombok.Getter;
import net.arna.jcraft.api.JRegistries;
import net.arna.jcraft.api.spec.SpecData;
import net.arna.jcraft.api.spec.SpecType;
import net.arna.jcraft.datagen.JDataGen;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.PackOutput;

@Getter
public class JSpecDataProvider extends JAttackerDataProvider<SpecType, SpecData> {
    private final String name = "Spec Data";

    public JSpecDataProvider(FabricDataOutput dataOutput) {
        super(dataOutput, PackOutput.Target.DATA_PACK, "specs", SpecData.CODEC,
                JRegistries.SPEC_TYPE_REGISTRY, SpecData.class, "DATA");
    }

    @Override
    protected Class<?> getHolderClass(SpecType type) {
        return JDataGen.getSpecClass(type);
    }
}
