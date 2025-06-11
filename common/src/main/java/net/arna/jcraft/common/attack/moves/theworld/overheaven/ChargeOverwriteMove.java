package net.arna.jcraft.common.attack.moves.theworld.overheaven;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import lombok.NonNull;
import net.arna.jcraft.api.attack.enums.MoveClass;
import net.arna.jcraft.api.attack.MoveType;
import net.arna.jcraft.api.attack.moves.AbstractMove;
import net.arna.jcraft.common.entity.stand.TheWorldOverHeavenEntity;
import net.arna.jcraft.api.registry.JSoundRegistry;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;

@Getter
public class ChargeOverwriteMove extends AbstractMove<ChargeOverwriteMove, TheWorldOverHeavenEntity> {
    private final int minChargeTime;

    public ChargeOverwriteMove(int cooldown, int windup, int duration, float moveDistance, int minChargeTime) {
        super(cooldown, windup, duration, moveDistance);
        this.minChargeTime = minChargeTime;
    }

    @Override
    public @NonNull MoveType<ChargeOverwriteMove> getMoveType() {
        return Type.INSTANCE;
    }

    // Does nothing on its own
    @Override
    public @NonNull Set<LivingEntity> perform(TheWorldOverHeavenEntity attacker, LivingEntity user) {
        return Set.of();
    }

    @Override
    public boolean onInitMove(TheWorldOverHeavenEntity attacker, MoveClass moveClass) {
        if (attacker.getMoveStun() >= getDuration() - minChargeTime) {
            return false;
        }

        switch (moveClass) {
            case SPECIAL1 -> initFollowup(attacker, 1);
            case SPECIAL2 -> initFollowup(attacker, 2);
            case SPECIAL3 -> initFollowup(attacker, 3);
            default -> {
                return false;
            }
        }

        return true;
    }

    private void initFollowup(TheWorldOverHeavenEntity attacker, int type) {
        attacker.setOverwriteType(type);
        attacker.getMoveMap().initiateFollowup(attacker, this, false, 0);
        attacker.playSound(JSoundRegistry.TWOH_OVERWRITE.get(), 1, 1);
    }

    @Override
    protected @NonNull ChargeOverwriteMove getThis() {
        return this;
    }

    @Override
    public @NonNull ChargeOverwriteMove copy() {
        return copyExtras(new ChargeOverwriteMove(getCooldown(), getWindup(), getDuration(), getMoveDistance(), minChargeTime));
    }

    public static class Type extends AbstractMove.Type<ChargeOverwriteMove> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<ChargeOverwriteMove>, ChargeOverwriteMove> buildCodec(RecordCodecBuilder.Instance<ChargeOverwriteMove> instance) {
            return instance.group(extras(), cooldown(), windup(), duration(), moveDistance(),
                            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("min_charge_time").forGetter(ChargeOverwriteMove::getMinChargeTime))
                    .apply(instance, applyExtras(ChargeOverwriteMove::new));
        }
    }
}
