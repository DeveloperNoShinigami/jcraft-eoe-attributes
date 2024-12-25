package net.arna.jcraft.common.attack.moves.metallica;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.core.data.MoveType;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.entity.projectile.BisectProjectile;
import net.arna.jcraft.common.entity.stand.MetallicaEntity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;

public class BisectAttack extends AbstractMove<BisectAttack, MetallicaEntity> {
    public BisectAttack(int cooldown, int windup, int duration, float moveDistance) {
        super(cooldown, windup, duration, moveDistance);
    }

    @Override
    public @NonNull MoveType<BisectAttack> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(MetallicaEntity attacker, LivingEntity user, MoveContext ctx) {
        BisectProjectile bisect = new BisectProjectile(attacker.level(), user);
        bisect.shootFromRotation(user, user.getXRot(), user.getYRot(), 0, 2.0f, 0);
        bisect.setScale(attacker.getBisectChargeTime() / 10.0f);
        attacker.level().addFreshEntity(bisect);

        return Set.of();
    }

    @Override
    protected @NonNull BisectAttack getThis() {
        return this;
    }

    @Override
    public @NonNull BisectAttack copy() {
        return copyExtras(new BisectAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance()));
    }

    public static class Type extends AbstractMove.Type<BisectAttack> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<BisectAttack>, BisectAttack> buildCodec(RecordCodecBuilder.Instance<BisectAttack> instance) {
            return baseDefault(instance, BisectAttack::new);
        }
    }
}
