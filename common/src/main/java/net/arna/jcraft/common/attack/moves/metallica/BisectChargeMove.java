package net.arna.jcraft.common.attack.moves.metallica;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.api.attack.MoveType;
import net.arna.jcraft.common.attack.moves.base.AbstractHoldableMove;
import net.arna.jcraft.common.entity.stand.MetallicaEntity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;

public class BisectChargeMove extends AbstractHoldableMove<BisectChargeMove, MetallicaEntity> {
    public BisectChargeMove(int cooldown, int windup, int duration, float moveDistance, int minimumCharge) {
        super(cooldown, windup, duration, moveDistance, minimumCharge);
    }

    @Override
    public @NonNull MoveType<BisectChargeMove> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public boolean conditionsMet(MetallicaEntity attacker) {
        return super.conditionsMet(attacker) && (attacker.getIron() >= 24.0f || attacker.getBisectChargeTime() > 0);
    }

    @Override
    public void onInitiate(MetallicaEntity attacker) {
        super.onInitiate(attacker);

        attacker.setBisectChargeTime(0);
    }

    @Override
    public void activeTick(MetallicaEntity attacker, int moveStun) {
        super.activeTick(attacker, moveStun);

        if (attacker.drainIron(2.0f)) {
            attacker.setBisectChargeTime(attacker.getBisectChargeTime() + 1);
        }
    }

    @Override
    public @NonNull Set<LivingEntity> perform(MetallicaEntity attacker, LivingEntity user, MoveContext ctx) {
        return Set.of();
    }

    @Override
    protected @NonNull BisectChargeMove getThis() {
        return this;
    }

    @Override
    public @NonNull BisectChargeMove copy() {
        return copyExtras(new BisectChargeMove(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getMinimumCharge()));
    }

    public static class Type extends AbstractHoldableMove.Type<BisectChargeMove> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<BisectChargeMove>, BisectChargeMove> buildCodec(RecordCodecBuilder.Instance<BisectChargeMove> instance) {
            return holdableDefault(instance, BisectChargeMove::new);
        }
    }
}
