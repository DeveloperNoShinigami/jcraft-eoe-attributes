package net.arna.jcraft.mixin.client;

import com.mojang.authlib.GameProfile;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import net.arna.jcraft.client.animlayer.PlayerModifierLayer;
import net.arna.jcraft.common.util.IJCraftAnimatedPlayer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractClientPlayer.class)
public class AbstractClientPlayerEntityMixin implements IJCraftAnimatedPlayer {

    @Unique
    private final PlayerModifierLayer<IAnimation> modAnimationContainer = new PlayerModifierLayer<>();

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void jcraft$init(ClientLevel world, GameProfile profile, CallbackInfo ci) {
        PlayerAnimationAccess.getPlayerAnimLayer((AbstractClientPlayer) (Object) this).addAnimLayer(1001, modAnimationContainer);
    }

    @Override
    public ModifierLayer<IAnimation> jcraft_getModAnimation() {
        return modAnimationContainer;
    }
}
