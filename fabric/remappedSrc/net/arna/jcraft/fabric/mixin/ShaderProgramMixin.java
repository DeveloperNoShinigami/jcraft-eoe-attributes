package net.arna.jcraft.fabric.mixin;

import net.arna.jcraft.fabric.client.JShader;
import net.fabricmc.fabric.impl.client.rendering.FabricShaderProgram;
import net.minecraft.client.renderer.ShaderInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ShaderInstance.class)
abstract class ShaderProgramMixin {
    @Shadow
    @Final
    private String name;

    // Allow loading our ShaderPrograms from arbitrary namespaces.
    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Identifier;<init>(Ljava/lang/String;)V"), allow = 1)
    private String modifyProgramId(String id) {
        if ((Object) this instanceof JShader) {
            return FabricShaderProgram.rewriteAsId(id, name);
        }

        return id;
    }
}