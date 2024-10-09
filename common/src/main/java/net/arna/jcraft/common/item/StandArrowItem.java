package net.arna.jcraft.common.item;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.component.living.CommonStandComponent;
import net.arna.jcraft.common.entity.projectile.StandArrowEntity;
import net.arna.jcraft.common.entity.stand.StandType;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.minecraft.core.Holder;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class StandArrowItem extends ArrowItem {
    public static final DamageType DAMAGE_TYPE = new DamageType("stand_arrow", DamageScaling.NEVER, 0f, DamageEffects.HURT);

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

        // damage by arrow
        int damage = Math.max(world.getGameRules().getInt(JCraft.STAND_ARROW_BASE_DAMAGE), 0);
        if (world.getDifficulty() == Difficulty.HARD) {
            damage *= 2;
        }
        else if (world.getDifficulty() == Difficulty.EASY) {
            damage /= 2;
        }
        else if (world.getDifficulty() == Difficulty.PEACEFUL) {
            damage = 0;
        }
        if (damage > 0) {
            user.hurt(new DamageSource(Holder.direct(DAMAGE_TYPE)), damage);
        }

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

    @Override
    public AbstractArrow createArrow(Level level, ItemStack stack, LivingEntity shooter) {
        return new StandArrowEntity(shooter, level);
    }
}
