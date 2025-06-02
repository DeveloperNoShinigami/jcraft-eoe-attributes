package net.arna.jcraft.common.attack.moves.shared;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.IAttacker;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.core.data.MoveType;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.util.JUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Set;

public final class TossMove<A extends IAttacker<? extends A, ?>> extends AbstractMove<TossMove<A>, A> {

    public TossMove(int cooldown, int windup, int duration, float moveDistance) {
        super(cooldown, windup, duration, moveDistance);
    }

    @Override
    public @NonNull MoveType<TossMove<A>> getMoveType() {
        return Type.INSTANCE.cast();
    }

    @Override
    public @NonNull Set<LivingEntity> perform(A attacker, LivingEntity user, MoveContext ctx) {
        if (attacker instanceof StandEntity<?,?> stand && !stand.level().isClientSide()) {
            final ItemStack projectile = stand.getItemInHand(InteractionHand.MAIN_HAND);
            JUtils.tossItem(stand, stand.level(), projectile, true);
        }
        return Set.of();
    }

    @Override
    protected @NonNull TossMove<A> getThis() {
        return this;
    }

    @Override
    public @NonNull TossMove<A> copy() {
        return copyExtras(new TossMove<>(getCooldown(), getWindup(), getDuration(), getMoveDistance()));
    }

    public static class Type extends AbstractMove.Type<TossMove<?>> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<TossMove<?>>, TossMove<?>> buildCodec(RecordCodecBuilder.Instance<TossMove<?>> instance) {
            return instance.group(cooldown(), windup(), duration(), moveDistance()).apply(instance, TossMove::new);
        }
    }
}
