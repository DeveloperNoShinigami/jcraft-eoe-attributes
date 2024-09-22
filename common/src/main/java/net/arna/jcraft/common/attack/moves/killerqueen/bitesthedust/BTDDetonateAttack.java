package net.arna.jcraft.common.attack.moves.killerqueen.bitesthedust;

import lombok.NonNull;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.entity.stand.AbstractKillerQueenEntity;
import net.arna.jcraft.common.util.JParticleType;
import net.arna.jcraft.registry.JStatusRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import java.util.List;
import java.util.Set;

public final class BTDDetonateAttack extends AbstractMove<BTDDetonateAttack, AbstractKillerQueenEntity<?, ?>> {
    public BTDDetonateAttack(int cooldown, int windup, int duration, float moveDistance) {
        super(cooldown, windup, duration, moveDistance);
    }

    @Override
    public @NonNull Set<LivingEntity> perform(AbstractKillerQueenEntity<?, ?> attacker, LivingEntity user, MoveContext ctx) {
        final LivingEntity btdEntity = ctx.get(BTDPlantAttack.BTD_ENTITY);
        final Vec3 btdPos = ctx.get(BTDPlantAttack.BTD_POS);
        if (btdEntity == null) {
            return Set.of();
        }

        attacker.level().explode(user, btdEntity.getX(), btdEntity.getY() + btdEntity.getBbHeight() / 2, btdEntity.getZ(), 2f, Level.ExplosionInteraction.NONE);
        btdEntity.addEffect(new MobEffectInstance(JStatusRegistry.KNOCKDOWN.get(), 35, 0, true, false));

        final Vec3 pos = btdEntity.position();
        JCraft.createParticle((ServerLevel) attacker.level(), pos.x, pos.y + 5, pos.z, JParticleType.BITES_THE_DUST);
        final Vec3 v1 = pos.add(3, 3, 3);
        final Vec3 v2 = pos.add(-3, -3, -3);
        List<LivingEntity> list = attacker.level().getEntitiesOfClass(LivingEntity.class, new AABB(v1, v2),
                EntitySelector.LIVING_ENTITY_STILL_ALIVE.and(e -> e != user.getVehicle() && e != user && e != attacker && e != btdEntity));

        for (LivingEntity l : list) {
            if (l.distanceToSqr(pos) < 9) {
                if (l.distanceToSqr(pos) < 2.25) {
                    attacker.level().explode(user, l.getX(), l.getY() + l.getBbHeight() / 2, l.getZ(), 1.5f, Level.ExplosionInteraction.NONE);
                } else {
                    attacker.level().explode(user, l.getX(), l.getY() + l.getBbHeight() / 2, l.getZ(), 1f, Level.ExplosionInteraction.NONE);
                }
            }
        }

        btdEntity.teleportToWithTicket(btdPos.x, btdPos.y, btdPos.z);
        ctx.set(BTDPlantAttack.BTD_ENTITY, null);
        ctx.set(BTDPlantAttack.BTD_POS, null);

        return Set.of();
    }

    @Override
    protected @NonNull BTDDetonateAttack getThis() {
        return this;
    }

    @Override
    public @NonNull BTDDetonateAttack copy() {
        return copyExtras(new BTDDetonateAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance()));
    }
}
