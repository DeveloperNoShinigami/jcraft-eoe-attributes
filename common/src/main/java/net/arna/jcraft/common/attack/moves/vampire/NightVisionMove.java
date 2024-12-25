package net.arna.jcraft.common.attack.moves.vampire;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.data.MoveType;
import net.arna.jcraft.common.attack.core.ctx.BooleanMoveVariable;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.spec.VampireSpec;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;

public class NightVisionMove extends AbstractMove<NightVisionMove, VampireSpec> {
    private static final BooleanMoveVariable ACTIVE = new BooleanMoveVariable();

    public NightVisionMove(int cooldown) {
        super(cooldown, 0, 0, 0);
    }

    @Override
    public @NonNull MoveType<NightVisionMove> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(VampireSpec attacker, LivingEntity user, MoveContext ctx) {
        ctx.setBoolean(ACTIVE, !ctx.getBoolean(ACTIVE));
        if (!ctx.getBoolean(ACTIVE)) {
            user.removeEffect(MobEffects.NIGHT_VISION);
        }

        return Set.of();
    }

    @Override
    public void tick(VampireSpec attacker) {
        LivingEntity user = attacker.getUser();
        if (user == null || !attacker.getMoveContext().getBoolean(ACTIVE)) return;

        user.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 400, 0, true, false));
    }

    @Override
    public void registerExtraContextEntries(MoveContext ctx) {
        ctx.register(ACTIVE, false);
    }

    @Override
    protected @NonNull NightVisionMove getThis() {
        return this;
    }

    @Override
    public @NonNull NightVisionMove copy() {
        return copyExtras(new NightVisionMove(getCooldown()));
    }

    public static class Type extends AbstractMove.Type<NightVisionMove> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<NightVisionMove>, NightVisionMove> buildCodec(RecordCodecBuilder.Instance<NightVisionMove> instance) {
            return instance.group(extras(), cooldown()).apply(instance, applyExtras(NightVisionMove::new));
        }
    }
}
