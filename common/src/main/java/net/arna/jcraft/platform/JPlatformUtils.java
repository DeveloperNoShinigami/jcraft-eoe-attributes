package net.arna.jcraft.platform;

import com.mojang.serialization.Codec;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.arna.jcraft.client.rendering.api.MultiInstancePostProcessor;
import net.arna.jcraft.client.rendering.post.TimestopShaderFX;
import net.arna.jcraft.common.attack.core.data.MoveActionType;
import net.arna.jcraft.common.attack.core.data.MoveConditionType;
import net.arna.jcraft.common.attack.core.data.MoveType;
import net.minecraft.client.renderer.ShaderInstance;

public class JPlatformUtils {

    @ExpectPlatform
    public static MultiInstancePostProcessor<TimestopShaderFX> getZaWarudo(){
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @ExpectPlatform
    public static ShaderInstance getTest() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @ExpectPlatform
    public static ShaderInstance getRred() {
        throw new UnsupportedOperationException("Not implemented yet");
    }


    @ExpectPlatform
    public static boolean isModLoaded(String name) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @ExpectPlatform
    public static Codec<MoveType<?>> getMoveTypeCodec() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @ExpectPlatform
    public static Codec<MoveConditionType<?>> getMoveConditionTypeCodec() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @ExpectPlatform
    public static Codec<MoveActionType<?>> getMoveActionTypeCodec() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
