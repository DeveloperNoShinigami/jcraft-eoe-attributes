package net.arna.jcraft.common.attack.moves.silverchariot;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.api.attack.MoveType;
import net.arna.jcraft.api.attack.moves.AbstractSimpleAttack;
import net.arna.jcraft.common.entity.stand.SilverChariotEntity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;

public final class ArmorOffAttack extends AbstractSimpleAttack<ArmorOffAttack, SilverChariotEntity> {
    private int armorTime = 0;

    public ArmorOffAttack(final int cooldown, final int windup, final int duration, final float moveDistance,
                          final float damage, final int stun, final float hitboxSize, final float knockback, final float offset) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset);
    }

    @Override
    public @NonNull MoveType<ArmorOffAttack> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final SilverChariotEntity attacker, final LivingEntity user) {
        final Set<LivingEntity> targets = super.perform(attacker, user);

        attacker.setMode(SilverChariotEntity.Mode.ARMORLESS);
        armorTime = 500;

        return targets;
    }

    @Override
    public void tick(final SilverChariotEntity attacker) {
        tickArmor(attacker);
    }

    private void tickArmor(final SilverChariotEntity stand) {
        if (stand.getMode() != SilverChariotEntity.Mode.ARMORLESS) {
            return;
        }

        if (--armorTime <= 0) {
            stand.setMode(SilverChariotEntity.Mode.REGULAR);
        }
    }

    @Override
    protected @NonNull ArmorOffAttack getThis() {
        return this;
    }

    @Override
    public @NonNull ArmorOffAttack copy() {
        return copyExtras(new ArmorOffAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(),
                getStun(), getHitboxSize(), getKnockback(), getOffset()));
    }

    public static class Type extends AbstractSimpleAttack.Type<ArmorOffAttack> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<ArmorOffAttack>, ArmorOffAttack> buildCodec(RecordCodecBuilder.Instance<ArmorOffAttack> instance) {
            return attackDefault(instance, ArmorOffAttack::new);
        }
    }
}
