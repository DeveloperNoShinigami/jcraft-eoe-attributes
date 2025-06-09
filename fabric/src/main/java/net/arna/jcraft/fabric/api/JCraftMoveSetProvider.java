package net.arna.jcraft.fabric.api;

import com.mojang.serialization.Codec;
import lombok.Getter;
import net.arna.jcraft.api.IAttackerType;
import net.arna.jcraft.api.attack.MoveSet;
import net.arna.jcraft.common.attack.core.IAttacker;
import net.arna.jcraft.common.attack.core.MoveClass;
import net.arna.jcraft.common.attack.core.MoveMap;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Getter
public abstract class JCraftMoveSetProvider<A extends IAttacker<A, S>, S extends Enum<S>> extends FabricCodecDataProvider<MoveMap.Entry<A, S>> {
    private final ResourceLocation type;

    protected JCraftMoveSetProvider(FabricDataOutput dataOutput, Codec<MoveMap.Entry<A, S>> codec, ResourceLocation type) {
        super(dataOutput, PackOutput.Target.DATA_PACK, "movesets", codec);
        this.type = type;
    }

    @Override
    protected final void configure(BiConsumer<ResourceLocation, MoveMap.Entry<A, S>> provider) {
        configureMoveSets(set -> {
            IAttackerType type = set.getType();
            ResourceLocation base = type.getId().withPath(p -> String.format("%s/%s/%s", type.kind(), p, set.getName()));
            MoveMap<A, S> map = set.save();

            for (Map.Entry<MoveClass, MoveMap.Entry<A, S>> entry : map.getEntries().entries()) {
                String moveClass = entry.getKey().getName();
                String moveName = entry.getValue().getMove().getName().getString()
                        .toLowerCase().replace(" ", "_").replaceAll("[^a-z0-9/._-]", "");
                ResourceLocation id = base.withPath(p -> "%s/%s/%s".formatted(p, moveClass, moveName));

                provider.accept(id, entry.getValue());
            }
        });
    }

    protected abstract void configureMoveSets(Consumer<MoveSet<A, S>> provider);

    @Override
    public @NotNull String getName() {
        return "Move Sets for " + type;
    }
}
