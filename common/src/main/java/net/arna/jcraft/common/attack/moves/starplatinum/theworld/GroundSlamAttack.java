package net.arna.jcraft.common.attack.moves.starplatinum.theworld;

import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractSimpleAttack;
import net.arna.jcraft.common.entity.stand.SPTWEntity;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import java.util.Set;

public final class GroundSlamAttack extends AbstractSimpleAttack<GroundSlamAttack, SPTWEntity> {
    public GroundSlamAttack(int cooldown, int windup, int duration, float moveDistance, float damage, int stun,
                            float hitboxSize, float knockback, float offset) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset);
    }

    @Override
    public @NonNull Set<LivingEntity> perform(SPTWEntity attacker, LivingEntity user, MoveContext ctx) {
        Set<LivingEntity> targets = super.perform(attacker, user, ctx);

        Vec3 pos = user.position();
        for (LivingEntity target : targets) {
            Vec3 launchVec = target.position().subtract(pos).normalize().scale(1.3);
            target.push(launchVec.x, launchVec.y + 0.4, launchVec.z);

            target.hurtMarked = true;
            if (target instanceof ServerPlayer serverPlayer) {
                serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(serverPlayer));
            }
        }

        return targets;
    }

    @Override
    protected @NonNull GroundSlamAttack getThis() {
        return this;
    }

    @Override
    public @NonNull GroundSlamAttack copy() {
        return copyExtras(new GroundSlamAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(),
                getStun(), getHitboxSize(), getKnockback(), getOffset()));
    }
}
