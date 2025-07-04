package net.arna.jcraft.api.attack.enums;

import com.mojang.serialization.Codec;
import lombok.Getter;
import net.arna.jcraft.common.util.JCodecUtils;

@Getter
public enum BlockableType {
    BLOCKABLE(false, false),
    UNBLOCKABLE(true, true),
    UNBLOCKABLE_EFFECTS_ONLY(false, true);

    public static final Codec<BlockableType> CODEC = JCodecUtils.createEnumCodec(BlockableType.class);

    private final boolean nonBlockable, nonBlockableEffects;

    BlockableType(final boolean nonBlockable, final boolean nonBlockableEffects) {
        this.nonBlockable = nonBlockable;
        this.nonBlockableEffects = nonBlockableEffects;
    }
}
