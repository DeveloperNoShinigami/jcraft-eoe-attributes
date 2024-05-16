package net.arna.jcraft.mixin;

import net.arna.jcraft.common.item.StandDiscItem;
import net.arna.jcraft.registry.JItemRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(GrindstoneMenu.class)
public class GrindstoneScreenHandlerMixin {
    @Shadow
    @Final
    Container input;

    @ModifyVariable(method = "updateResult", at = @At("STORE"), ordinal = 2)
    private boolean allowStandDiscs(boolean value) {
        ItemStack stack1 = input.getItem(0);
        ItemStack stack2 = input.getItem(1);

        ItemStack stack = stack1.isEmpty() ? stack2 : stack1;
        if (stack.getItem() != JItemRegistry.STAND_DISC) {
            return value;
        }

        return StandDiscItem.isEmptyDisc(stack); // True means not allowed
    }

    @Inject(method = "grind", at = @At("RETURN"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private void grindStandDisc(ItemStack stack, int damage, int amount, CallbackInfoReturnable<ItemStack> cir, ItemStack copy) {
        if (copy.getItem() != JItemRegistry.STAND_DISC) {
            return;
        }

        if (StandDiscItem.isEmptyDisc(copy)) {
            cir.setReturnValue(ItemStack.EMPTY);
            return;
        }

        CompoundTag nbt = copy.getTag();
        if (nbt == null) {
            return; // Should be impossible
        }

        nbt.remove("StandID");
        nbt.remove("Skin");
    }
}
