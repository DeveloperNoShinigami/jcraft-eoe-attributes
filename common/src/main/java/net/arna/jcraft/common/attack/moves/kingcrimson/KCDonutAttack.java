package net.arna.jcraft.common.attack.moves.kingcrimson;

import lombok.NonNull;
import net.arna.jcraft.common.attack.moves.base.AbstractSimpleAttack;
import net.arna.jcraft.common.entity.stand.KingCrimsonEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class KCDonutAttack extends AbstractSimpleAttack<KCDonutAttack, KingCrimsonEntity> {
    public KCDonutAttack(int cooldown, int windup, int duration, float moveDistance, float damage, int stun,
                         float hitboxSize, float knockback, float offset) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset);
    }

    @Override
    protected void processTarget(KingCrimsonEntity attacker, LivingEntity target, Vec3 kbVec, DamageSource damageSource) {
        super.processTarget(attacker, target, kbVec, damageSource);
        Vec3 pos = attacker.position().add(attacker.getLookAngle().scale(1.5));
        target.teleportToWithTicket(pos.x, target.getY(), pos.z);
    }

    @Override
    protected @NonNull KCDonutAttack getThis() {
        return this;
    }

    @Override
    public @NonNull KCDonutAttack copy() {
        return copyExtras(new KCDonutAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(),
                getStun(), getHitboxSize(), getKnockback(), getOffset()));
    }
}
