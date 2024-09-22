package net.arna.jcraft.common.attack.moves.dirtydeedsdonedirtcheap;

import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.entity.stand.D4CEntity;
import net.arna.jcraft.registry.JItemRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;

public final class GiveGunMove extends AbstractMove<GiveGunMove, D4CEntity> {
    public GiveGunMove(int cooldown, int windup, int duration, float moveDistance) {
        super(cooldown, windup, duration, moveDistance);
    }

    @Override
    public @NonNull Set<LivingEntity> perform(D4CEntity attacker, LivingEntity user, MoveContext ctx) {
        if (user instanceof final ServerPlayer playerEntity) {
            playerEntity.addItem(JItemRegistry.FV_REVOLVER.get().getDefaultInstance());
            attacker.getMainHandItem().shrink(1);
        }

        return Set.of();
    }

    @Override
    protected @NonNull GiveGunMove getThis() {
        return this;
    }

    @Override
    public @NonNull GiveGunMove copy() {
        return copyExtras(new GiveGunMove(getCooldown(), getWindup(), getDuration(), getMoveDistance()));
    }
}
