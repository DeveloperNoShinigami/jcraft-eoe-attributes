package net.arna.jcraft.common.attack.moves.metallica;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.core.data.MoveType;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.entity.stand.MetallicaEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;

public class InvisibilityMove extends AbstractMove<InvisibilityMove, MetallicaEntity> {
    public InvisibilityMove(int cooldown, int windup, int duration) {
        super(cooldown, windup, duration, 0f);
    }

    @Override
    public @NonNull MoveType<InvisibilityMove> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(MetallicaEntity attacker, LivingEntity user, MoveContext ctx) {
        final boolean newInvis = !attacker.getEntityData().get(MetallicaEntity.INVISIBLE);
        attacker.getEntityData().set(MetallicaEntity.INVISIBLE, newInvis);
        if (newInvis) {
            user.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 39, 0, true, false));
        }

        return Set.of();
    }

    @Override
    public void tick(MetallicaEntity attacker) {
        super.tick(attacker);

        // Invisibility iron drain
        boolean invisible = attacker.getEntityData().get(MetallicaEntity.INVISIBLE);
        if (!invisible || attacker.tickCount % 20 != 0) return;

        boolean canStayInvis = attacker.drainIron(10.0f);
        if (!canStayInvis) {
            attacker.getEntityData().set(MetallicaEntity.INVISIBLE, false);
        } else {
            attacker.getUserOrThrow().addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 21, 0, true, false));
        }
    }

    @Override
    protected @NonNull InvisibilityMove getThis() {
        return this;
    }

    @Override
    public @NonNull InvisibilityMove copy() {
        return copyExtras(new InvisibilityMove(getCooldown(), getWindup(), getDuration()));
    }

    public static class Type extends AbstractMove.Type<InvisibilityMove> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<InvisibilityMove>, InvisibilityMove> buildCodec(RecordCodecBuilder.Instance<InvisibilityMove> instance) {
            return instance.group(extras(), cooldown(), windup(), duration()).apply(instance, applyExtras(InvisibilityMove::new));
        }
    }
}
