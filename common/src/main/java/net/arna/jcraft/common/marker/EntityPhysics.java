package net.arna.jcraft.common.marker;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public record EntityPhysics(@Nullable Vec3 position, @Nullable Float xRot, @Nullable Float yHeadRot, @Nullable Float yBodyRot, @Nullable Vec3 velocity) {

    public static final Codec<EntityPhysics> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Vec3.CODEC.fieldOf("position").forGetter(EntityPhysics::position),
                    Codec.FLOAT.fieldOf("xRot").forGetter(EntityPhysics::xRot),
                    Codec.FLOAT.fieldOf("yHeadRot").forGetter(EntityPhysics::yHeadRot),
                    Codec.FLOAT.fieldOf("yBodyRot").forGetter(EntityPhysics::yBodyRot),
                    Vec3.CODEC.fieldOf("velocity").forGetter(EntityPhysics::velocity))
            .apply(instance, EntityPhysics::new));

}
