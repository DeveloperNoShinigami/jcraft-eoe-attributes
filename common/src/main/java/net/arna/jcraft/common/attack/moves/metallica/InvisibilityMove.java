package net.arna.jcraft.common.attack.moves.metallica;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.core.ctx.MoveVariable;
import net.arna.jcraft.common.attack.core.ctx.WeakMoveVariable;
import net.arna.jcraft.common.attack.core.data.MoveType;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.entity.stand.MetallicaEntity;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.tickable.MagneticFields;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;

public class InvisibilityMove extends AbstractMove<InvisibilityMove, MetallicaEntity> {
    public static final MoveVariable<MagneticFields.MagneticField> MAGNETIC_FIELD = new WeakMoveVariable<>(MagneticFields.MagneticField.class);

    public InvisibilityMove(int cooldown, int windup, int duration) {
        super(cooldown, windup, duration, 0f);
    }

    @Override
    public @NonNull MoveType<InvisibilityMove> getMoveType() {
        return Type.INSTANCE;
    }

    private void setInvisible(final MetallicaEntity metallica, final boolean invis) {
        metallica.getEntityData().set(MetallicaEntity.INVISIBLE, invis);

        if (invis) {
            final LivingEntity user = metallica.getUserOrThrow();

            user.addEffect(
                    new MobEffectInstance(MobEffects.INVISIBILITY, 39, 0, true, false)
            );

            metallica.getMoveContext().set(
                    MAGNETIC_FIELD,
                    MagneticFields.createField((ServerLevel) metallica.level(), null, metallica.position(), 3.0f, 5.0f)
            );
        } else {
            MagneticFields.MagneticField field = metallica.getMoveContext().get(MAGNETIC_FIELD);
            if (field == null) {
                JCraft.LOGGER.warn("Metallica InvisibilityMove#MAGNETIC_FIELD was null when decloaking!");
            } else {
                field.time = 0;
            }
        }
    }

    @Override
    public @NonNull Set<LivingEntity> perform(MetallicaEntity attacker, LivingEntity user, MoveContext ctx) {
        final boolean newInvis = !attacker.getEntityData().get(MetallicaEntity.INVISIBLE);
        setInvisible(attacker, newInvis);
        return Set.of();
    }

    @Override
    public void tick(MetallicaEntity attacker) {
        super.tick(attacker);

        // Invisibility iron drain
        final boolean invisible = attacker.getEntityData().get(MetallicaEntity.INVISIBLE);
        if (!invisible || attacker.tickCount % 2 != 0) return;

        final boolean canStayInvis = attacker.drainIron(0.75f);
        if (!canStayInvis) {
            setInvisible(attacker, false);
        } else {
            final LivingEntity user = attacker.getUserOrThrow();
            attacker.getMoveContext().get(MAGNETIC_FIELD).pos = attacker.position().add(GravityChangerAPI.getEyeOffset(user));
            user.addEffect(
                    new MobEffectInstance(MobEffects.INVISIBILITY, 21, 0, true, false)
            );
        }
    }

    @Override
    public void registerExtraContextEntries(final MoveContext ctx) {
        ctx.register(MAGNETIC_FIELD, null);
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
