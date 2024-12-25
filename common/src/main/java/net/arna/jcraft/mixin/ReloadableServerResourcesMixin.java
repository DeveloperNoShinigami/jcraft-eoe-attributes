package net.arna.jcraft.mixin;

import net.arna.jcraft.common.attack.core.data.MoveSetLoader;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(ReloadableServerResources.class)
public class ReloadableServerResourcesMixin {
    @Inject(method = "listeners", at = @At("RETURN"), cancellable = true)
    private void addMoveSetLoaderToListeners(CallbackInfoReturnable<List<PreparableReloadListener>> cir) {
        List<PreparableReloadListener> listeners = new ArrayList<>(cir.getReturnValue());
        listeners.add(MoveSetLoader::onReload);
        cir.setReturnValue(listeners);
    }
}
