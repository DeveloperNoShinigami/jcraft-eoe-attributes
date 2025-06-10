package net.arna.jcraft.api.attack.enums;

import com.mojang.serialization.Codec;
import net.arna.jcraft.common.util.JCodecUtils;

public enum StunType {
    UNBURSTABLE,
    BURSTABLE,
    BLOCK,
    LAUNCH,
    WINDED;

    public static final Codec<StunType> CODEC = JCodecUtils.createEnumCodec(StunType.class);
}
