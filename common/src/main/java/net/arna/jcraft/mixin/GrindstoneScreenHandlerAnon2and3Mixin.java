package net.arna.jcraft.mixin;

import net.arna.jcraft.common.item.StandDiscItem;
import net.arna.jcraft.api.registry.JItemRegistry;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = {"net/minecraft/world/inventory/GrindstoneMenu$2", "net/minecraft/world/inventory/GrindstoneMenu$3"})
public class GrindstoneScreenHandlerAnon2and3Mixin extends Slot {
    public GrindstoneScreenHandlerAnon2and3Mixin(Container inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Inject(method = "mayPlace", at = @At("HEAD"), cancellable = true)
    private void canInsertStandDiscs(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.getItem() == JItemRegistry.STAND_DISC.get() && !StandDiscItem.isEmptyDisc(stack))
        // This is executed before the item is inserted, so both slots must be empty when inserting a disc.
        // You cannot insert two discs simultaneously.
        {
            cir.setReturnValue(container.getItem(0).isEmpty() && container.getItem(1).isEmpty());
        }
    }
}
