package net.arna.jcraft.common.attack.moves.vampire;

import lombok.NonNull;
import net.arna.jcraft.common.attack.core.IAttacker;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.tickable.Revivables;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import java.util.Set;

public final class ReviveMove<A extends IAttacker<? extends A, ?>> extends AbstractMove<ReviveMove<A>, A> {
    public ReviveMove(int cooldown, int windup, int duration, float reviveDistance) {
        super(cooldown, windup, duration, reviveDistance);
    }

    @Override
    public @NonNull Set<LivingEntity> perform(A attacker, LivingEntity user, MoveContext ctx) {
        MinecraftServer server = user.getServer();
        assert server != null;
        ServerLevel serverWorld = server.getLevel(user.level().dimension());
        assert serverWorld != null;

        for (Revivables.ReviveData revivable : Revivables.getAround(user.position(), getMoveDistance())) {
            EntityType<?> entityType = revivable.getType();

            // Convert Testificates to zombie Testificates
            if (entityType.is(EntityTypeTags.RAIDERS) || entityType.equals(EntityType.VILLAGER)) {
                entityType = EntityType.ZOMBIE_VILLAGER;
            }
            // Humans to zombies
            if (entityType.equals(EntityType.PLAYER)) {
                entityType = EntityType.ZOMBIE;
            }

            Entity entity = entityType.create(serverWorld);
            if (entity instanceof LivingEntity living) {
                if (living.isInvertedHealAndHarm()) {
                    entity.setPos(revivable.getPos());
                    entity.tickCount = 1;
                    if (user instanceof ServerPlayer serverPlayer) {
                        JComponentPlatformUtils.getMiscData(living).setSlavedTo(serverPlayer.getUUID());
                    }
                    if (!isBoss(living)) {
                        serverWorld.addFreshEntity(entity);
                        Revivables.removeRevivable(revivable);
                    }
                }
            }
        }
        return Set.of();
    }

    public static boolean isBoss(LivingEntity living) {
        //todo: find a better way to check if smth is a boss
        return living.getMaxHealth() >= 80.0f;
    }

    @Override
    protected @NonNull ReviveMove<A> getThis() {
        return this;
    }

    @Override
    public @NonNull ReviveMove<A> copy() {
        return copyExtras(new ReviveMove<>(getCooldown(), getWindup(), getDuration(), getMoveDistance()));
    }
}
