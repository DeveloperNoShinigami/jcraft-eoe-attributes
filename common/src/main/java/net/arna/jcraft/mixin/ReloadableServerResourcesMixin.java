package net.arna.jcraft.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.arna.jcraft.common.attack.core.data.MoveSetLoader;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = ReloadableServerResources.class, priority = 100)
public class ReloadableServerResourcesMixin {
    @ModifyReturnValue(method = "listeners", at = @At("RETURN"))
    private List<PreparableReloadListener> addMoveSetLoaderToListeners(final List<PreparableReloadListener> original) {
        List<PreparableReloadListener> listeners = new ArrayList<>(original);
        listeners.add(MoveSetLoader::onReload);
        return listeners;
    }
}
