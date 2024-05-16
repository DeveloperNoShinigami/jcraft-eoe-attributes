package net.arna.jcraft.common.attack.moves.killerqueen;

import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractSimpleAttack;
import net.arna.jcraft.common.component.living.CommonBombTrackerComponent;
import net.arna.jcraft.common.entity.stand.AbstractKillerQueenEntity;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.gravity.util.RotationUtil;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class BombPlantAttack extends AbstractSimpleAttack<BombPlantAttack, AbstractKillerQueenEntity<?, ?>> {
    public BombPlantAttack(int cooldown, int windup, int duration, float attackDistance, int stun, float hitboxSize, float offset) {
        super(cooldown, windup, duration, attackDistance, 0f, stun, hitboxSize, 0f, offset);
    }

    private static final Vec3 halfBox = new Vec3(0.5, 0.5, 0.5);

    @Override
    public @NonNull Set<LivingEntity> perform(AbstractKillerQueenEntity<?, ?> attacker, LivingEntity user, MoveContext ctx) {
        CommonBombTrackerComponent.BombData mainBomb = JComponentPlatformUtils.getBombTracker(user).getMainBomb();

        Set<LivingEntity> targets = super.perform(attacker, user, ctx);

        Vec3 rotVec = getRotVec(attacker);
        Vec3 boxCenter = attacker.position().add(
                RotationUtil.vecPlayerToWorld(new Vec3(0, attacker.getBbHeight() * 0.66, 0), GravityChangerAPI.getGravityDirection(attacker))
        ).add(rotVec);

        targets.stream()
                .findFirst()
                .<Entity>map(JUtils::getUserIfStand)
                .or(() -> {
                    // If none are found, re-do an optimized hitbox check for any entity type
                    List<Entity> hit = attacker.level().getEntitiesOfClass(Entity.class,
                            new AABB(boxCenter.subtract(halfBox), boxCenter.add(halfBox)),
                            EntitySelector.NO_CREATIVE_OR_SPECTATOR.and(e -> e != attacker && e != user));
                    return hit.isEmpty() ? Optional.empty() : Optional.of(hit.get(0));
                })
                .ifPresentOrElse(mainBomb::setBomb, () -> {
                    // If none are found again, try to place the bomb on the wall
                    BlockPos closePos = BlockPos.containing(boxCenter.subtract(rotVec));
                    BlockPos farPos = BlockPos.containing(boxCenter);
                    BlockState blockState = attacker.level().getBlockState(closePos);
                    if (blockState.isAir()) {
                        blockState = attacker.level().getBlockState(farPos);
                        if (!blockState.isAir()) {
                            mainBomb.setBomb(farPos);
                        }
                    } else {
                        mainBomb.setBomb(closePos);
                    }
                });

        return targets;
    }

    @Override
    protected @NonNull BombPlantAttack getThis() {
        return this;
    }

    @Override
    public @NonNull BombPlantAttack copy() {
        return copyExtras(new BombPlantAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getStun(), getHitboxSize(),
                getOffset()));
    }
}
