package net.arna.jcraft.mixin.gravity;

import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.ThrowablePotionItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ThrowablePotionItem.class)
public abstract class ThrowablePotionItemMixin extends PotionItem {

    public ThrowablePotionItemMixin(Properties settings) {
        super(settings);
    }

    /*
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        if (!world.isClientSide) {
            ThrownPotion potionEntity = new ThrownPotion(world, user);
            potionEntity.setItem(itemStack);
            potionEntity.shootFromRotation(user, user.getXRot(), user.getYRot(), 0.0F, 0.5F, 1.0F);
            world.addFreshEntity(potionEntity);
        }

        user.awardStat(Stats.ITEM_USED.get(this));
        if (!user.getAbilities().instabuild) {
            itemStack.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(itemStack, world.isClientSide());
    }
     */
}



