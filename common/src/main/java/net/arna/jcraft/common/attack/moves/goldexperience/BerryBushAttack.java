package net.arna.jcraft.common.attack.moves.goldexperience;

import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractSimpleAttack;
import net.arna.jcraft.common.entity.stand.GoldExperienceEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Set;

public final class BerryBushAttack extends AbstractSimpleAttack<BerryBushAttack, GoldExperienceEntity> {
    private static final BlockState berryBush = Blocks.SWEET_BERRY_BUSH.defaultBlockState().setValue(SweetBerryBushBlock.AGE, 1);

    public BerryBushAttack(int cooldown, int windup, int duration, float attackDistance, float damage, int stun,
                           float hitboxSize, float knockback, float offset) {
        super(cooldown, windup, duration, attackDistance, damage, stun, hitboxSize, knockback, offset);
    }

    @Override
    public @NonNull Set<LivingEntity> perform(GoldExperienceEntity attacker, LivingEntity user, MoveContext ctx) {
        Level world = attacker.level();
        BlockPos blockPos = attacker.blockPosition();
        if (world.getBlockState(blockPos).isAir() && world.getBlockState(blockPos.below()).canOcclude()) {
            world.setBlockAndUpdate(blockPos, berryBush);
        }

        return super.perform(attacker, user, ctx);
    }

    @Override
    protected @NonNull BerryBushAttack getThis() {
        return this;
    }

    @Override
    public @NonNull BerryBushAttack copy() {
        return copyExtras(new BerryBushAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(), getStun(),
                getHitboxSize(), getKnockback(), getOffset()));
    }
}
