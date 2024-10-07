package net.arna.jcraft.client;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;
import java.util.function.BooleanSupplier;

public class JClientMixinPlugin implements IMixinConfigPlugin {
    private static final String MIXIN_CLASS_PREFIX = "net.arna.jcraft.client.mixin.sodium.";
    private static final String MIXIN_CLASS_SODIUM = MIXIN_CLASS_PREFIX + "sodium.SodiumWorldRendererMixin";
    private static final String MIXIN_CLASS_VANILLA = MIXIN_CLASS_PREFIX + "vanilla.WorldRendererVanillaMixin";

    private static final BooleanSupplier HAS_SODIUM = createModCompatibility("sodium");

    @Override
    public void onLoad(final String mixinPackage) {
        //MixinExtrasBootstrap.init();
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(final String targetClassName, final String mixinClassName) {
        if (mixinClassName.equals(MIXIN_CLASS_SODIUM)) {
            return HAS_SODIUM.getAsBoolean();
        } else if (mixinClassName.equals(MIXIN_CLASS_VANILLA)) {
            return !HAS_SODIUM.getAsBoolean();
        }

        return true;
    }

    @Override
    public void acceptTargets(final Set<String> myTargets, final Set<String> otherTargets) {
        return;
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(final String targetClassName, final ClassNode targetClass, final String mixinClassName, final IMixinInfo mixinInfo) {
        return;
    }

    @Override
    public void postApply(final String targetClassName, final ClassNode targetClass, final String mixinClassName, final IMixinInfo mixinInfo) {
        return;
    }

    private static BooleanSupplier createModCompatibility(final String id) {
        return () -> FabricLoader.getInstance().isModLoaded(id);
    }
}
