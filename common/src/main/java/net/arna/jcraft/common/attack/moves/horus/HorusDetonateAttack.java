package net.arna.jcraft.common.attack.moves.horus;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.core.data.MoveType;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.entity.stand.HorusEntity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;

public class HorusDetonateAttack extends AbstractMove<HorusDetonateAttack, HorusEntity> {
    public HorusDetonateAttack(int cooldown, int windup, int duration, float moveDistance) {
        super(cooldown, windup, duration, moveDistance);
    }

    @Override
    public @NonNull MoveType<HorusDetonateAttack> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public boolean conditionsMet(HorusEntity attacker) {
        return super.conditionsMet(attacker) && attacker.getLastLargeIcicle() != null;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(HorusEntity attacker, LivingEntity user, MoveContext ctx) {
        attacker.getLastLargeIcicle().detonate();
        return Set.of();
    }

    @Override
    protected @NonNull HorusDetonateAttack getThis() {
        return this;
    }

    @Override
    public @NonNull HorusDetonateAttack copy() {
        return copyExtras(new HorusDetonateAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance()));
    }

    public static class Type extends AbstractMove.Type<HorusDetonateAttack> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<HorusDetonateAttack>, HorusDetonateAttack> buildCodec(RecordCodecBuilder.Instance<HorusDetonateAttack> instance) {
            return baseDefault(instance, HorusDetonateAttack::new);
        }
    }
}
