package net.arna.jcraft.common.attack.moves.shared;

import lombok.Getter;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.IAttacker;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractSimpleAttack;
import net.arna.jcraft.common.util.JUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@Getter
public final class HealMove<A extends IAttacker<? extends A, ?>> extends AbstractSimpleAttack<HealMove<A>, A> {
    private final float health;
    private final HealTarget target;
    private final Consumer<LivingEntity> consumer;

    public HealMove(int cooldown, int windup, int duration, float moveDistance, float hitboxSize, float offset,
                    float health, HealTarget target) {
        this(cooldown, windup, duration, moveDistance, hitboxSize, offset, health, target, e -> {
        });
    }

    public HealMove(int cooldown, int windup, int duration, float moveDistance, float hitboxSize, float offset,
                    float health, HealTarget target, Consumer<LivingEntity> consumer) {
        super(cooldown, windup, duration, moveDistance, 0f, 0, hitboxSize, 0f, offset);
        this.health = health;
        this.target = target;
        this.consumer = consumer;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(A attacker, LivingEntity user, MoveContext ctx) {
        Set<LivingEntity> targets = target.pickTargets(super.perform(attacker, user, ctx), user);
        targets.forEach(e -> {
            e.heal(health);
            consumer.accept(e);
        });

        if (target == HealTarget.TARGETS && attacker.getUserOrThrow().isShiftKeyDown()) {
            ServerLevel world = (ServerLevel) user.level();
            BlockHitResult hitResult = JUtils.genericBlockRaycast(world, user, 2, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE);
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                BlockPos blockPos = hitResult.getBlockPos();
                BlockState blockState = world.getBlockState(blockPos);

                boolean fertilized = false;

                if (blockState.getBlock() instanceof BonemealableBlock fertilizable) {
                    if (fertilizable.isValidBonemealTarget(world, blockPos, blockState, false)) {
                        if (fertilizable.isBonemealSuccess(world, world.random, blockPos, blockState)) {
                            for (int i = 0; i < 5; i++) {
                                fertilizable.performBonemeal(world, world.random, blockPos, blockState);
                            }
                            fertilized = true;
                        }
                    }
                }

                if (!fertilized) {
                    BoneMealItem.growWaterPlant(new ItemStack(Items.AIR), world, blockPos, Direction.DOWN);
                }

                world.levelEvent(1505, blockPos, 0); // Display
            }
        }

        return targets;
    }

    @Override
    protected @NonNull HealMove<A> getThis() {
        return this;
    }

    @Override
    public @NonNull HealMove<A> copy() {
        return copyExtras(new HealMove<>(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getHitboxSize(), getOffset(),
                health, target, consumer));
    }

    public enum HealTarget {
        TARGETS((targets, user) -> targets),
        USER((targets, user) -> Set.of(user));

        private final BiFunction<Set<LivingEntity>, LivingEntity, Set<LivingEntity>> targetPicker;

        HealTarget(BiFunction<Set<LivingEntity>, LivingEntity, Set<LivingEntity>> targetPicker) {
            this.targetPicker = targetPicker;
        }

        public Set<LivingEntity> pickTargets(Set<LivingEntity> targets, LivingEntity user) {
            return targetPicker.apply(targets, user);
        }
    }
}
