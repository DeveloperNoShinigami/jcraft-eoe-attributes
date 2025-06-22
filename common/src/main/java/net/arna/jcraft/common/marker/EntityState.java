package net.arna.jcraft.common.marker;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

@Getter
@AllArgsConstructor
public class EntityState {

    public static final @NonNull Codec<EntityState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    EntityPhysics.CODEC.fieldOf("physics").forGetter(EntityState::getPhysics),
                    CompoundTag.CODEC.fieldOf("nbt").forGetter(EntityState::getNbt))
            .apply(instance, EntityState::new));

    protected @Nullable EntityPhysics physics;

    protected @Nullable CompoundTag nbt;

}
