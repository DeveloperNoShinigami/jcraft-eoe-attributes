package net.arna.jcraft.common.attack.core;

import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.core.data.MoveActionType;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;

public abstract class MoveAction<T extends MoveAction<? extends T, A>, A extends IAttacker<? extends A, ?>> {
    protected MoveAction() {}

    public abstract void perform(final A attacker, final LivingEntity user, final MoveContext ctx, final Set<LivingEntity> targets);

    public abstract @NonNull MoveActionType<T> getType();
}
