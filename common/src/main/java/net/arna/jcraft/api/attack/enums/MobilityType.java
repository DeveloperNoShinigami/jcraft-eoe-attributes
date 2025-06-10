package net.arna.jcraft.api.attack.enums;

import com.mojang.serialization.Codec;
import net.arna.jcraft.common.util.JCodecUtils;

public enum MobilityType {
    DASH,
    TELEPORT,
    HIGHJUMP,
    FLIGHT;

    public static final Codec<MobilityType> CODEC = JCodecUtils.createEnumCodec(MobilityType.class);
}
