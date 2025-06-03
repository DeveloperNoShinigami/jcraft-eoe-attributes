package net.arna.jcraft.common.attack.moves.metallica;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.api.attack.MoveType;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.entity.stand.MetallicaEntity;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.tickable.MagneticFields;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

import java.lang.ref.WeakReference;
import java.util.Set;

public class InvisibilityMove extends AbstractMove<InvisibilityMove, MetallicaEntity> {
    private WeakReference<MagneticFields.MagneticField> magneticField;

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

            magneticField = new WeakReference<>(MagneticFields.createField((ServerLevel) metallica.level(),
                    null, metallica.position(), 3.0f, 5.0f));
        } else {
            MagneticFields.MagneticField field = magneticField == null ? null : magneticField.get();
            if (field == null) {
                JCraft.LOGGER.warn("Metallica InvisibilityMove#MAGNETIC_FIELD was null when decloaking!");
            } else {
                field.time = 0;
            }
        }
    }

    @Override
    public @NonNull Set<LivingEntity> perform(MetallicaEntity attacker, LivingEntity user) {
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
            final MagneticFields.MagneticField field = magneticField == null ? null : magneticField.get();
            if (field != null)
                field.pos = attacker.position().add(GravityChangerAPI.getEyeOffset(user));
            user.addEffect(
                    new MobEffectInstance(MobEffects.INVISIBILITY, 21, 0, true, false)
            );
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
