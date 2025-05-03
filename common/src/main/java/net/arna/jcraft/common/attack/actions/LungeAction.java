package net.arna.jcraft.common.attack.actions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.*;
import net.arna.jcraft.common.attack.core.IAttacker;
import net.arna.jcraft.common.attack.core.MoveAction;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.core.data.MoveActionType;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.util.JUtils;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;

/**
 * Lunges the user forwards with some factor
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor(staticName = "lunge")
public class LungeAction extends MoveAction<LungeAction, IAttacker<?, ?>> {
    @Getter
    private final float factor, verticalFactor;
    private boolean onGround = false, notFree = false;

    public LungeAction onGround() {
        this.onGround = true;
        return this;
    }

    public LungeAction isNotFree() {
        this.notFree = true;
        return this;
    }

    public boolean requireOnGround() {
        return onGround;
    }

    public boolean requireNotFree() {
        return notFree;
    }

    @Override
    public void perform(IAttacker<?, ?> attacker, LivingEntity user, MoveContext ctx, Set<LivingEntity> targets) {
        if (onGround && !user.onGround() || notFree && attacker instanceof StandEntity<?, ?> stand && stand.isFree()) return;

        JUtils.addVelocity(user, attacker.getBaseEntity().getLookAngle().scale(factor).add(0.0, verticalFactor, 0.0));
    }

    @Override
    public @NonNull MoveActionType<LungeAction> getType() {
        return Type.INSTANCE;
    }

    public static class Type extends MoveActionType<LungeAction> {
        public static final Type INSTANCE = new Type();

        @Override
        public Codec<LungeAction> getCodec() {
            return RecordCodecBuilder.create(instance -> instance.group(
                    runMoment(),
                    Codec.FLOAT.fieldOf("factor").forGetter(LungeAction::getFactor),
                    Codec.FLOAT.fieldOf("vertical_factor").forGetter(LungeAction::getVerticalFactor),
                    Codec.BOOL.optionalFieldOf("on_ground", false).forGetter(LungeAction::requireOnGround),
                    Codec.BOOL.optionalFieldOf("not_free", false).forGetter(LungeAction::requireNotFree)
            ).apply(instance, apply(LungeAction::new)));
        }
    }
}
