package net.arna.jcraft.common.attack.moves.thefool;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.api.attack.enums.MoveClass;
import net.arna.jcraft.api.attack.MoveType;
import net.arna.jcraft.api.attack.moves.AbstractSimpleAttack;
import net.arna.jcraft.common.entity.stand.TheFoolEntity;
import net.arna.jcraft.registry.JSoundRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import java.util.Set;

public final class PoundAttack extends AbstractSimpleAttack<PoundAttack, TheFoolEntity> {
    public PoundAttack(final int cooldown, final int windup, final int duration, final float moveDistance, final float damage, final int stun,
                       final float hitboxSize, final float knockback, final float offset) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset);
    }

    @Override
    public @NonNull MoveType<PoundAttack> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final TheFoolEntity attacker, final LivingEntity user) {
        final Set<LivingEntity> targets = super.perform(attacker, user);

        for (LivingEntity target : targets) {
            Vec3 vel = target.getDeltaMovement();
            target.setDeltaMovement(vel.x, (attacker.getMoveStun() > 14) ? 0.5 : -1, vel.y);
            target.hurtMarked = true;
        }

        return targets;
    }

    @Override
    public boolean onInitMove(TheFoolEntity attacker, MoveClass moveClass) {
        if (attacker.getMoveStun() > 11) return false;

        switch (moveClass) {
            case SPECIAL1 -> initSlam(attacker, 1);
            case SPECIAL2 -> initSlam(attacker, 2);
            case SPECIAL3 -> initSlam(attacker, 3);
            default -> {
                return false;
            }
        }

        return true;
    }

    private void initSlam(TheFoolEntity attacker, int type) {
        SlamAttack slam = TheFoolEntity.SLAM.copy();
        slam.setVariant(type);
        attacker.setMove(slam, TheFoolEntity.State.POUND_DOWN);
        attacker.playSound(JSoundRegistry.FOOL_BARK1.get(), 1, 1);
    }

    @Override
    protected @NonNull PoundAttack getThis() {
        return this;
    }

    @Override
    public @NonNull PoundAttack copy() {
        return copyExtras(new PoundAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(),
                getStun(), getHitboxSize(), getKnockback(), getOffset()));
    }

    public static class Type extends AbstractSimpleAttack.Type<PoundAttack> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<PoundAttack>, PoundAttack> buildCodec(RecordCodecBuilder.Instance<PoundAttack> instance) {
            return attackDefault(instance, PoundAttack::new);
        }
    }
}
