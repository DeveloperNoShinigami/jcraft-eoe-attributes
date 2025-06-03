package net.arna.jcraft.common.attack.moves.shared;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import lombok.NonNull;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.attack.core.IAttacker;
import net.arna.jcraft.api.attack.MoveType;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractSimpleAttack;
import net.arna.jcraft.common.util.JCodecUtils;
import net.arna.jcraft.common.util.JUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
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

@Getter
public final class HealMove<A extends IAttacker<? extends A, ?>> extends AbstractSimpleAttack<HealMove<A>, A> {
    private final float health;
    private final HealTarget target;
    private final boolean pacifyMobs;

    public HealMove(final int cooldown, final int windup, final int duration, final float moveDistance,
                    final float hitboxSize, final float offset, final float health, final HealTarget target,
                    final boolean pacifyMobs) {
        super(cooldown, windup, duration, moveDistance, 0f, 0, hitboxSize, 0f, offset);
        this.health = health;
        this.target = target;
        this.pacifyMobs = pacifyMobs;
    }

    @Override
    public @NonNull MoveType<HealMove<A>> getMoveType() {
        return Type.INSTANCE.cast();
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final A attacker, final LivingEntity user, final MoveContext ctx) {
        final Set<LivingEntity> targets = target.pickTargets(super.perform(attacker, user, ctx), user);
        targets.forEach(e -> {
            e.heal(health);

            if (pacifyMobs) {
                pacifyMob(e);
            }
        });

        if (target == HealTarget.TARGETS && attacker.getUserOrThrow().isShiftKeyDown()) {
            final ServerLevel world = (ServerLevel) user.level();
            final BlockHitResult hitResult = JUtils.genericBlockRaycast(world, user, 2, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE);
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                final BlockPos blockPos = hitResult.getBlockPos();
                final BlockState blockState = world.getBlockState(blockPos);

                boolean fertilized = false;

                if (blockState.getBlock() instanceof final BonemealableBlock fertilizable) {
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

    private static void pacifyMob(LivingEntity target) {
        target.setLastHurtByMob(null);

        if (!(target instanceof Mob mob)) {
            return;
        }
        JCraft.stun(mob, 10, 0);
        mob.setTarget(null);
        mob.setLastHurtByPlayer(null);
        if (mob instanceof NeutralMob angerable) {
            angerable.stopBeingAngry();
        }
    }

    @Override
    protected @NonNull HealMove<A> getThis() {
        return this;
    }

    @Override
    public @NonNull HealMove<A> copy() {
        return copyExtras(new HealMove<>(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getHitboxSize(), getOffset(),
                health, target, pacifyMobs));
    }

    public enum HealTarget {
        TARGETS((targets, user) -> targets),
        USER((targets, user) -> Set.of(user));

        public static final Codec<HealTarget> CODEC = JCodecUtils.createEnumCodec(HealTarget.class);

        private final BiFunction<Set<LivingEntity>, LivingEntity, Set<LivingEntity>> targetPicker;

        HealTarget(BiFunction<Set<LivingEntity>, LivingEntity, Set<LivingEntity>> targetPicker) {
            this.targetPicker = targetPicker;
        }

        public Set<LivingEntity> pickTargets(Set<LivingEntity> targets, LivingEntity user) {
            return targetPicker.apply(targets, user);
        }
    }

    public static class Type extends AbstractSimpleAttack.Type<HealMove<?>> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<HealMove<?>>, HealMove<?>> buildCodec(RecordCodecBuilder.Instance<HealMove<?>> instance) {
            return instance.group(extras(), attackExtras(), cooldown(), windup(), duration(), moveDistance(), hitboxSize(), offset(),
                    Codec.FLOAT.fieldOf("health").forGetter(HealMove::getHealth),
                    HealTarget.CODEC.fieldOf("target").forGetter(HealMove::getTarget),
                    Codec.BOOL.fieldOf("pacify_mobs").forGetter(HealMove::isPacifyMobs))
                    .apply(instance, applyAttackExtras(HealMove::new));
        }
    }
}
