package net.arna.jcraft.common.attack.moves.killerqueen;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.api.attack.MoveType;
import net.arna.jcraft.api.attack.moves.AbstractMove;
import net.arna.jcraft.common.entity.stand.AbstractKillerQueenEntity;
import net.arna.jcraft.registry.JSoundRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public final class ExplosiveDashAttack extends AbstractMove<ExplosiveDashAttack, AbstractKillerQueenEntity<?, ?>> {
    public ExplosiveDashAttack(final int cooldown) {
        super(cooldown, 0, 0, 0);
        dash = true;
    }

    @Override
    public @NotNull MoveType<ExplosiveDashAttack> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final AbstractKillerQueenEntity<?, ?> attacker, final LivingEntity user) {
        final Vec3 lookVec = user.getLookAngle().scale(0.9);
        attacker.level().explode(user,
                user.getX() - lookVec.x,
                user.getY() + user.getBbHeight() / 2 - lookVec.y,
                user.getZ() - lookVec.z,
                1f, Level.ExplosionInteraction.NONE);

        user.setDeltaMovement(user.getDeltaMovement().add(lookVec));
        user.hurtMarked = true;

        attacker.playSound(JSoundRegistry.KQ_DETONATE.get(), 1, 1);

        return Set.of();
    }

    @Override
    protected @NonNull ExplosiveDashAttack getThis() {
        return this;
    }

    @Override
    public @NonNull ExplosiveDashAttack copy() {
        return copyExtras(new ExplosiveDashAttack(getCooldown()));
    }

    public static class Type extends AbstractMove.Type<ExplosiveDashAttack> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<ExplosiveDashAttack>, ExplosiveDashAttack> buildCodec(RecordCodecBuilder.Instance<ExplosiveDashAttack> instance) {
            return instance.group(extras(), cooldown()).apply(instance, applyExtras(ExplosiveDashAttack::new));
        }
    }
}
