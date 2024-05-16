package net.arna.jcraft.common.item;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.component.living.CommonStandComponent;
import net.arna.jcraft.common.entity.stand.StandType;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class StandArrowItem extends Item {
    public StandArrowItem(Properties settings) {
        super(settings);
    }

    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.EAT;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        // Remove 1 from item stack
        ItemStack itemStack = user.getItemInHand(hand);
        if (!user.isCreative()) {
            itemStack.shrink(1);
        }

        // 1 second usage cooldown to prevent overuse
        user.getCooldowns().addCooldown(this, 20);

        // Roll for stand (can't roll the same one twice)
        if (!world.isClientSide) {
            CommonStandComponent standData = JComponentPlatformUtils.getStandData(user);

            RandomSource random = RandomSource.create();
            StandType oldType = standData.getType();
            StandType newType;
            do {
                newType = StandType.getRandomRegular(random);
            } while (newType == oldType);

            standData.setType(newType);
            user.unRide();
            JCraft.summon(world, user);

            user.awardStat(Stats.ITEM_USED.get(this));
        }

        return InteractionResultHolder.consume(itemStack);
    }
}
