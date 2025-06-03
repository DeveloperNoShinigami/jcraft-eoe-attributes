package net.arna.jcraft.common.attack.actions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.arna.jcraft.common.attack.core.MoveAction;
import net.arna.jcraft.api.attack.MoveActionType;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.spec.JSpec;
import net.arna.jcraft.common.util.JUtils;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;

@RequiredArgsConstructor(staticName = "cancelSpecMove")
public class CancelSpecMoveAction extends MoveAction<CancelSpecMoveAction, StandEntity<?, ?>> {

    @Override
    public void perform(StandEntity<?, ?> attacker, LivingEntity user, Set<LivingEntity> targets) {
        JSpec<?, ?> spec = JUtils.getSpec(user);
        if (spec != null && spec.getCurrentMove() != null) {
            spec.cancelMove();
        }
    }

    @Override
    public @NonNull MoveActionType<CancelSpecMoveAction> getType() {
        return Type.INSTANCE;
    }

    public static class Type extends MoveActionType<CancelSpecMoveAction> {
        public static final Type INSTANCE = new Type();

        @Override
        public Codec<CancelSpecMoveAction> getCodec() {
            return RecordCodecBuilder.create(instance -> instance.group(
                    runMoment()
            ).apply(instance, apply(CancelSpecMoveAction::new)));
        }
    }
}
