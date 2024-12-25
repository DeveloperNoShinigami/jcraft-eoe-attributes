package net.arna.jcraft.common.attack.core;

import lombok.Getter;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.data.MoveConditionType;

@Getter
public abstract class MoveCondition<C extends MoveCondition<C, A>, A extends IAttacker<? extends A, ?>> {


    public abstract boolean test(final A attacker);

    public abstract @NonNull MoveConditionType<C> getType();
}
