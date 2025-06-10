package net.arna.jcraft.api.attack.core;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record HitBoxData(double forwardOffset, double verticalOffset, double size) {
    public static final Codec<HitBoxData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.DOUBLE.optionalFieldOf("forward_offset", 0.0).forGetter(HitBoxData::forwardOffset),
            Codec.DOUBLE.optionalFieldOf("vertical_offset", 0.0).forGetter(HitBoxData::verticalOffset),
            Codec.DOUBLE.fieldOf("size").forGetter(HitBoxData::size)
    ).apply(instance, HitBoxData::new));

    public HitBoxData(final double size) {
        this(0, 0, size);
    }
}
