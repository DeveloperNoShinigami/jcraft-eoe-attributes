package net.arna.jcraft.platform.forge;

import com.mojang.serialization.Codec;
import net.arna.jcraft.client.rendering.api.MultiInstancePostProcessor;
import net.arna.jcraft.client.rendering.post.TimestopShaderFX;
import net.arna.jcraft.common.attack.core.data.MoveActionType;
import net.arna.jcraft.common.attack.core.data.MoveConditionType;
import net.arna.jcraft.common.attack.core.data.MoveType;
import net.arna.jcraft.forge.JCraftForge;
import net.arna.jcraft.forge.client.JShaderRegistry;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraftforge.fml.ModList;

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
        return ModList.get().isLoaded("name");
    }

    public static Codec<MoveType<?>> getMoveTypeCodec() {
        return JCraftForge.getMoveTypeCodec();
    }

    public static Codec<MoveConditionType<?>> getMoveConditionTypeCodec() {
        return JCraftForge.getMoveConditionTypeCodec();
    }

    public static Codec<MoveActionType<?>> getMoveActionTypeCodec() {
        return JCraftForge.getMoveActionTypeCodec();
    }
}
