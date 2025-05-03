package net.arna.jcraft.common.attack.core;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.core.data.MoveActionType;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;

@Getter
@Setter
public abstract class MoveAction<T extends MoveAction<? extends T, A>, A extends IAttacker<? extends A, ?>> {
    private RunMoment runMoment = RunMoment.ON_STRIKE;

    protected MoveAction() {}

    public abstract void perform(final A attacker, final LivingEntity user, final MoveContext ctx, final Set<LivingEntity> targets);

    public abstract @NonNull MoveActionType<T> getType();
}
