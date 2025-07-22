package net.arna.jcraft.common.ai;

import lombok.NonNull;
import net.arna.jcraft.api.attack.moves.AbstractMove;
import net.arna.jcraft.api.registry.JStatusRegistry;
import net.arna.jcraft.api.spec.JSpec;
import net.arna.jcraft.api.stand.StandEntity;
import net.arna.jcraft.common.util.JUtils;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public record CombatEntityContext(@NonNull LivingEntity entity,
                                  @Nullable StandEntity<?, ?> stand,
                                  @Nullable AbstractMove<?, ?> standAttack,
                                  @Nullable JSpec<?, ?> spec,
                                  @Nullable AbstractMove<?, ?> specAttack,
                                  int moveStun,
                                  boolean blocking,
                                  @Nullable MobEffectInstance stun) {

    public static CombatEntityContext from(LivingEntity entity) {
        final StandEntity<?, ?> stand = JUtils.getStand(entity);
        final JSpec<?, ?> spec = JUtils.getSpec(entity);

        return new CombatEntityContext(
                entity,
                stand,
                stand == null ? null : stand.getCurrentMove(),
                spec,
                spec == null ? null : spec.getCurrentMove(),
                Math.max(
                        stand == null ? 0 : stand.getMoveStun(),
                        spec == null ? 0 : spec.getMoveStun()
                ),
                stand != null && stand.blocking,
                entity.getEffect(JStatusRegistry.DAZED.get())
        );
    }
}
