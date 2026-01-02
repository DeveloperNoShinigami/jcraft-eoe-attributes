package net.arna.jcraft.common.attack.moves.hamon;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.api.attack.MoveType;
import net.arna.jcraft.api.attack.moves.AbstractSimpleAttack;
import net.arna.jcraft.common.spec.HamonSpec;
import net.arna.jcraft.common.util.JUtils;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;

public final class ImproviserAttack extends AbstractSimpleAttack<ImproviserAttack, HamonSpec> {
    public static final float CHARGE_COST = 10.0F;
    public ImproviserAttack(int cooldown, int windup, int duration, float moveDistance, float damage, int stun,
                            float hitboxSize, float knockback, float offset) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset);
    }

    @Override
    public @NonNull MoveType<ImproviserAttack> getMoveType() {
        return ImproviserAttack.Type.INSTANCE;
    }

    @Override
    public void onInitiate(HamonSpec attacker) {
        super.onInitiate(attacker);

        attacker.drainCharge(CHARGE_COST);
        attacker.setUseHamonNext(false);

        final LivingEntity user = attacker.getUser();

        boolean hasWeapon = !user.getMainHandItem().getAttributeModifiers(EquipmentSlot.MAINHAND).isEmpty();
    }

    @Override
    public void tick(HamonSpec attacker) {
        super.tick(attacker);
    }

    @Override
    public @NonNull Set<LivingEntity> perform(HamonSpec attacker, LivingEntity user) {


        final Set<LivingEntity> targets = super.perform(attacker, user);

        if (JUtils.getSpec(user) instanceof HamonSpec hamonSpec)
            for (LivingEntity target : targets)
                hamonSpec.processTarget(target);

        return targets;
    }

    @Override
    protected @NonNull ImproviserAttack getThis() {
        return this;
    }

    @Override
    public @NonNull ImproviserAttack copy() {
        return copyExtras(new ImproviserAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(),
                getStun(), getHitboxSize(), getKnockback(), getOffset()));
    }

    public static class Type extends AbstractSimpleAttack.Type<ImproviserAttack> {
        public static final ImproviserAttack.Type INSTANCE = new ImproviserAttack.Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<ImproviserAttack>, ImproviserAttack> buildCodec(RecordCodecBuilder.Instance<ImproviserAttack> instance) {
            return attackDefault(instance, ImproviserAttack::new);
        }
    }
}
