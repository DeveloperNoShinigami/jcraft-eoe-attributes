package net.arna.jcraft.fabric.datagen;

import lombok.Getter;
import net.arna.jcraft.api.JRegistries;
import net.arna.jcraft.api.spec.SpecData;
import net.arna.jcraft.api.spec.SpecType2;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Pig;

@Getter
public class JSpecDataProvider extends JAttackerDataProvider<SpecType2, SpecData> {
    private final String name = "Spec Data";

    public JSpecDataProvider(FabricDataOutput dataOutput) {
        super(dataOutput, "specs", SpecData.CODEC, JRegistries.SPEC_TYPE_REGISTRY);
    }

    @SuppressWarnings("DataFlowIssue") // We're making a fake Pig in a null level.
    @Override
    protected Class<?> getHolderClass(SpecType2 type) {
        return type.createSpec(new Pig(EntityType.PIG, null)).getClass();
    }
}
