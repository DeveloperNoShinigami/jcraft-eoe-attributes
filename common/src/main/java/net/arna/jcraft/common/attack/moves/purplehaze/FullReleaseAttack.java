package net.arna.jcraft.common.attack.moves.purplehaze;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntCollection;
import lombok.NonNull;
import net.arna.jcraft.api.attack.MoveType;
import net.arna.jcraft.api.attack.moves.AbstractMultiHitAttack;
import net.arna.jcraft.common.entity.stand.AbstractPurpleHazeEntity;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;

// Ambattukam
public class FullReleaseAttack extends AbstractMultiHitAttack<FullReleaseAttack, AbstractPurpleHazeEntity<?, ?>> {
    public FullReleaseAttack(int cooldown, int duration, float moveDistance, float damage, int stun, float hitboxSize,
                             float knockback, float offset, @NonNull IntCollection hitMoments) {
        super(cooldown, duration, moveDistance, damage, stun, hitboxSize, knockback, offset, hitMoments);
    }

    @Override
    public @NonNull MoveType<FullReleaseAttack> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(AbstractPurpleHazeEntity<?, ?> attacker, LivingEntity user) {
        Set<LivingEntity> targets = super.perform(attacker, user);

        float baseYaw = user.getYRot();
        final Direction gravity = GravityChangerAPI.getGravityDirection(attacker);

        if (attacker.getMoveStun() == 6) {
            baseYaw += 60.0F;
        }

        for (int i = 0; i < 3; i++) {
            LaunchCapsuleAttack.launchCapsule(attacker, user, gravity, 0.6F, baseYaw + i * 120.0F);
        }

        return targets;
    }

    @Override
    protected @NonNull FullReleaseAttack getThis() {
        return this;
    }

    @Override
    public @NonNull FullReleaseAttack copy() {
        return copyExtras(new FullReleaseAttack(getCooldown(), getDuration(), getMoveDistance(), getDamage(), getStun(),
                getHitboxSize(), getKnockback(), getOffset(), getHitMoments()));
    }

    public static class Type extends AbstractMultiHitAttack.Type<FullReleaseAttack> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<FullReleaseAttack>, FullReleaseAttack> buildCodec(RecordCodecBuilder.Instance<FullReleaseAttack> instance) {
            return multiHitDefault(instance, FullReleaseAttack::new);
        }
    }
}
