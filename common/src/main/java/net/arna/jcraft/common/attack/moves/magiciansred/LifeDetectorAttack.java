package net.arna.jcraft.common.attack.moves.magiciansred;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.api.attack.MoveType;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.entity.projectile.LifeDetectorEntity;
import net.arna.jcraft.common.entity.stand.MagiciansRedEntity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;

public final class LifeDetectorAttack extends AbstractMove<LifeDetectorAttack, MagiciansRedEntity> {
    public LifeDetectorAttack(final int cooldown, final int windup, final int duration, final float moveDistance) {
        super(cooldown, windup, duration, moveDistance);
        ranged = true;
    }

    @Override
    public @NonNull MoveType<LifeDetectorAttack> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final MagiciansRedEntity attacker, final LivingEntity user, final MoveContext ctx) {
        final LifeDetectorEntity lifeDetector = new LifeDetectorEntity(attacker.level());
        lifeDetector.setMaster(user);
        lifeDetector.moveTo(attacker.getX(), attacker.getY() + 1.5, attacker.getZ(), attacker.getYRot(), attacker.getXRot());
        attacker.level().addFreshEntity(lifeDetector);

        return Set.of();
    }

    @Override
    protected @NonNull LifeDetectorAttack getThis() {
        return this;
    }

    @Override
    public @NonNull LifeDetectorAttack copy() {
        return copyExtras(new LifeDetectorAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance()));
    }

    public static class Type extends AbstractMove.Type<LifeDetectorAttack> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<LifeDetectorAttack>, LifeDetectorAttack> buildCodec(RecordCodecBuilder.Instance<LifeDetectorAttack> instance) {
            return baseDefault(instance, LifeDetectorAttack::new);
        }
    }
}
