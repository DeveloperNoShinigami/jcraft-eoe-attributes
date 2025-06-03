package net.arna.jcraft.common.attack.moves.thefool;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.api.attack.MoveType;
import net.arna.jcraft.common.attack.core.ctx.IntMoveVariable;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractSimpleAttack;
import net.arna.jcraft.common.entity.stand.TheFoolEntity;
import net.arna.jcraft.registry.JSoundRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import java.util.Set;

public final class SlamAttack extends AbstractSimpleAttack<SlamAttack, TheFoolEntity> {
    public static final IntMoveVariable VARIANT = new IntMoveVariable();

    public SlamAttack(final int cooldown, final int windup, final int duration, final float moveDistance, final float damage, final int stun,
                      final float hitboxSize, final float knockback, final float offset) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset);
    }

    @Override
    public @NonNull MoveType<SlamAttack> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public void activeTick(TheFoolEntity attacker, int moveStun) {
        super.activeTick(attacker, moveStun);

        int slamType = attacker.getMoveContext().getInt(SlamAttack.VARIANT);
        if (slamType != 1) {
            attacker.setQueuedMove(null);
        }
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final TheFoolEntity attacker, final LivingEntity user, final MoveContext ctx) {
        final Set<LivingEntity> targets = super.perform(attacker, user, ctx);

        attacker.playSound(JSoundRegistry.IMPACT_11.get(), 1, 1);

        switch (ctx.getInt(VARIANT)) {
            case 2 -> {
                Vec3 leftVec = user.getLookAngle().yRot(1.75f);
                for (int i = 0; i < 8; i++) {
                    leftVec = leftVec.yRot(-3.141592f / 8).normalize();
                    TheFoolEntity.createFoolishSand(attacker.level(), attacker, attacker.blockPosition(),
                            new Vec3(leftVec.x / 4, 0.25, leftVec.z / 4));
                }
            }
            case 3 -> {
                Vec3 rotVec = user.getLookAngle();
                for (double i = 0; i < 7; i++) {
                    for (double y = 0; y < i; y++) {
                        double hDiv = 4.5 * (1 + y / i);
                        TheFoolEntity.createFoolishSand(attacker.level(), attacker, attacker.blockPosition(),
                                new Vec3(rotVec.x * Math.sqrt(i) / hDiv, y / 4.3, rotVec.z * Math.sqrt(i) / hDiv));
                    }
                }
            }
        }

        return targets;
    }

    @Override
    public void registerExtraContextEntries(final MoveContext ctx) {
        ctx.register(VARIANT);
    }

    @Override
    protected @NonNull SlamAttack getThis() {
        return this;
    }

    @Override
    public @NonNull SlamAttack copy() {
        return copyExtras(new SlamAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(),
                getStun(), getHitboxSize(), getKnockback(), getOffset()));
    }

    public static class Type extends AbstractSimpleAttack.Type<SlamAttack> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<SlamAttack>, SlamAttack> buildCodec(RecordCodecBuilder.Instance<SlamAttack> instance) {
            return attackDefault(instance, SlamAttack::new);
        }
    }
}
