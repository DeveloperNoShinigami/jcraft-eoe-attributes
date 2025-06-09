package net.arna.jcraft.fabric.datagen;

import com.mojang.serialization.Codec;
import dev.architectury.registry.registries.Registrar;
import lombok.Getter;
import lombok.NonNull;
import net.arna.jcraft.api.stand.StandType;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.PackOutput;

@Getter
public abstract class JStandTypeBasedConstantProvider<T> extends JAttackerDataProvider<StandType, T> {
    public JStandTypeBasedConstantProvider(FabricDataOutput dataOutput, PackOutput.Target outputType, String directoryName,
                                           Codec<T> codec, Registrar<StandType> typeRegistry, Class<? extends T> clazz,
                                           @NonNull String fieldName) {
        super(dataOutput, outputType, directoryName, codec, typeRegistry, clazz, fieldName);
    }

    @Override
    protected Class<?> getHolderClass(StandType type) {
        return JDataGen.getEntityClass(type.getEntityType());
    }
}
