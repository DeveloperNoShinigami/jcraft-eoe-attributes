package net.arna.jcraft.common.attack.actions;

import com.mojang.serialization.Codec;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.arna.jcraft.common.attack.core.MoveAction;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.core.data.MoveActionType;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.spec.JSpec;
import net.arna.jcraft.common.util.JUtils;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CancelSpecMoveAction extends MoveAction<CancelSpecMoveAction, StandEntity<?, ?>> {
    public static CancelSpecMoveAction cancelSpecMove() {
        return new CancelSpecMoveAction();
    }

    @Override
    public void perform(StandEntity<?, ?> attacker, LivingEntity user, MoveContext ctx, Set<LivingEntity> targets) {
        JSpec<?, ?> spec = JUtils.getSpec(user);
        if (spec != null && spec.getCurrentMove() != null) {
            spec.cancelMove();
        }
    }

    @Override
    public @NonNull MoveActionType<CancelSpecMoveAction> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements MoveActionType<CancelSpecMoveAction> {
        public static final Type INSTANCE = new Type();

        @Override
        public Codec<CancelSpecMoveAction> getCodec() {
            return Codec.unit(new CancelSpecMoveAction());
        }
    }
}
