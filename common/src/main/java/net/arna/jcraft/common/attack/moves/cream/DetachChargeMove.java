package net.arna.jcraft.common.attack.moves.cream;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.api.attack.MoveType;
import net.arna.jcraft.common.entity.stand.CreamEntity;
import net.minecraft.world.entity.LivingEntity;

public class DetachChargeMove extends AbstractSurpriseMove<DetachChargeMove>{
    public DetachChargeMove(int cooldown, int windup, int duration, float moveDistance) {
        super(cooldown, windup, duration, moveDistance);
    }

    @Override
    public @NonNull MoveType<DetachChargeMove> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public void onInitiate(CreamEntity attacker) {
        super.onInitiate(attacker);

        LivingEntity user = attacker.getUser();
        if (user == null) return;

        attacker.endHalfBall();
        outPos = user.position().toVector3f();
        outDir = user.getLookAngle().scale(0.75f).toVector3f();
    }

    @Override
    protected @NonNull DetachChargeMove getThis() {
        return this;
    }

    @Override
    public @NonNull DetachChargeMove copy() {
        return copyExtras(new DetachChargeMove(getCooldown(), getWindup(), getDuration(), getMoveDistance()));
    }

    public static class Type extends AbstractSurpriseMove.Type<DetachChargeMove> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<DetachChargeMove>, DetachChargeMove> buildCodec(RecordCodecBuilder.Instance<DetachChargeMove> instance) {
            return baseDefault(instance, DetachChargeMove::new);
        }
    }
}
