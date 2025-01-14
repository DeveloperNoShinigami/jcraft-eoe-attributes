package net.arna.jcraft.platform.fabric;

import com.mojang.serialization.Codec;
import net.arna.jcraft.client.rendering.api.MultiInstancePostProcessor;
import net.arna.jcraft.client.rendering.post.TimestopShaderFX;
import net.arna.jcraft.common.attack.core.data.MoveActionType;
import net.arna.jcraft.common.attack.core.data.MoveConditionType;
import net.arna.jcraft.common.attack.core.data.MoveType;
import net.arna.jcraft.fabric.JCraftFabric;
import net.arna.jcraft.fabric.client.JShaderRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.renderer.ShaderInstance;

public class JPlatformUtilsImpl {

    public static MultiInstancePostProcessor<TimestopShaderFX> getZaWarudo(){
        return JShaderRegistry.ZA_WARUDO;
    }

    public static ShaderInstance getTest() {
        return JShaderRegistry.TEST.getInstance().get();
    }

    public static ShaderInstance getRred() {
        return JShaderRegistry.RREDE.getInstance().get();
    }
    public static boolean isModLoaded(String name) {
        return FabricLoader.getInstance().isModLoaded("name");
    }

    public static Codec<MoveType<?>> getMoveTypeCodec() {
        return JCraftFabric.getMoveTypeCodec();
    }

    public static Codec<MoveConditionType<?>> getMoveConditionTypeCodec() {
        return JCraftFabric.getMoveConditionTypeCodec();
    }

    public static Codec<MoveActionType<?>> getMoveActionTypeCodec() {
        return JCraftFabric.getMoveActionTypeCodec();
    }
}
