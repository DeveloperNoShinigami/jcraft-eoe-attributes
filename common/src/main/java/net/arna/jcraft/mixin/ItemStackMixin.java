package net.arna.jcraft.mixin;

import net.arna.jcraft.common.item.MockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Inject(method = "isOf", at = @At("HEAD"), cancellable = true)
    private void mockItem(Item item, CallbackInfoReturnable<Boolean> cir) {
        ItemStack thiz = (ItemStack) (Object) this;
        if (MockItem.isMockItem(thiz)) {
            cir.setReturnValue(MockItem.getMockedStack(thiz).isOf(item));
        }
    }

    @Inject(method = "areEqual", at = @At("HEAD"), cancellable = true)
    private static void mockItemEqualsCheck(ItemStack left, ItemStack right, CallbackInfoReturnable<Boolean> cir) {

        if (!MockItem.isMockItem(left) && !MockItem.isMockItem(right)) {
            return;
        }

        ItemStack stack1 = MockItem.isMockItem(left) ? MockItem.getMockedStack(left) : left;
        ItemStack stack2 = MockItem.isMockItem(right) ? MockItem.getMockedStack(right) : right;

        cir.setReturnValue(ItemStack.areEqual(stack1, stack2));
    }
}
