package net.arna.jcraft.common.attack.actions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.*;
import net.arna.jcraft.api.attack.IAttacker;
import net.arna.jcraft.api.attack.core.MoveAction;
import net.arna.jcraft.api.attack.core.MoveActionType;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;

import java.util.Set;

@Getter
@AllArgsConstructor(staticName = "lightOnFire")
@RequiredArgsConstructor(staticName = "lightOnFire")
public class LightOnFireAction extends MoveAction<LightOnFireAction, IAttacker<?, ?>> {
    private final int duration; // Duration in ticks
    private boolean ignoreProtection;

    @Override
    public void perform(IAttacker<?, ?> attacker, LivingEntity user, Set<LivingEntity> targets) {
        for (LivingEntity target : targets) {
            int duration = this.duration;
            if (!ignoreProtection) {
                duration = ProtectionEnchantment.getFireAfterDampener(target, duration);
            }

            if (duration > target.getRemainingFireTicks()) {
                target.setRemainingFireTicks(duration);
            }
        }
    }

    @Override
    public @NonNull MoveActionType<LightOnFireAction> getType() {
        return Type.INSTANCE;
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Type extends MoveActionType<LightOnFireAction> {
        public static final Type INSTANCE = new Type();

        @Override
        public Codec<LightOnFireAction> getCodec() {
            return RecordCodecBuilder.create(instance -> instance.group(
                    runMoment(),
                    ExtraCodecs.POSITIVE_INT.fieldOf("duration").forGetter(LightOnFireAction::getDuration),
                    Codec.BOOL.optionalFieldOf("ignore_protection", false).forGetter(LightOnFireAction::isIgnoreProtection)
            ).apply(instance, apply((duration, ignoreProtection) -> LightOnFireAction.lightOnFire(duration, ignoreProtection))));
        }
    }
}
