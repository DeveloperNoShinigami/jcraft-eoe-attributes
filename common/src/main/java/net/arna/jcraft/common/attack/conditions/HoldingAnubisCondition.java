package net.arna.jcraft.common.attack.conditions;

import com.mojang.serialization.Codec;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.arna.jcraft.api.attack.IAttacker;
import net.arna.jcraft.api.attack.core.MoveCondition;
import net.arna.jcraft.api.attack.core.MoveConditionType;
import net.arna.jcraft.api.registry.JItemRegistry;

@RequiredArgsConstructor(staticName = "holdingAnubis")
public class HoldingAnubisCondition extends MoveCondition<HoldingAnubisCondition, IAttacker<?, ?>> {
    @Override
    public boolean test(IAttacker<?, ?> attacker) {
        return attacker.getUserOrThrow().isHolding(JItemRegistry.ANUBIS.get());
    }

    @Override
    public @NonNull MoveConditionType<HoldingAnubisCondition> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements MoveConditionType<HoldingAnubisCondition> {
        public static final Type INSTANCE = new Type();

        @Override
        public Codec<HoldingAnubisCondition> getCodec() {
            return Codec.unit(HoldingAnubisCondition::new);
        }
    }
}
