package net.arna.jcraft.common.item;

import lombok.NonNull;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.component.living.CommonStandComponent;
import net.arna.jcraft.common.entity.damage.JDamageSources;
import net.arna.jcraft.common.entity.projectile.StandArrowEntity;
import net.arna.jcraft.common.entity.stand.StandType;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class StandArrowItem extends ArrowItem {
    // private static final DamageType DAMAGE_TYPE = new DamageType("stand_arrow", DamageScaling.NEVER, 0f, DamageEffects.HURT);
    public static final ResourceKey<DamageType> STAND_ARROW = JDamageSources.createDamageType("stand_arrow");

    public StandArrowItem(Properties settings) {
        super(settings);
    }

    public @NonNull UseAnim getUseAnimation(@NonNull final ItemStack stack) {
        return UseAnim.EAT;
    }

    @Override
    public @NonNull InteractionResultHolder<ItemStack> use(@NonNull final Level world, final Player user, @NonNull final InteractionHand hand) {
        // Remove 1 from item stack
        final ItemStack itemStack = user.getItemInHand(hand);
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
            user.hurt(JDamageSources.create(world, STAND_ARROW, user), damage);
        }

        // Roll for stand (can't roll the same one twice)
        if (!world.isClientSide) {
            final CommonStandComponent standData = JComponentPlatformUtils.getStandData(user);

            final RandomSource random = RandomSource.create();
            final StandType oldType = standData.getType();
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
    public @NonNull AbstractArrow createArrow(@NonNull final Level level, @NonNull final ItemStack stack, @Nullable final LivingEntity shooter) {
        return new StandArrowEntity(shooter, level);
    }
}
