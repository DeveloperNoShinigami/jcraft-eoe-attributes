package net.arna.jcraft.common.attack.moves.dirtydeedsdonedirtcheap;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.api.attack.MoveType;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.entity.stand.D4CEntity;
import net.arna.jcraft.registry.JItemRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public final class GiveGunMove extends AbstractMove<GiveGunMove, D4CEntity> {
    public GiveGunMove(final int cooldown, final int windup, final int duration, final float moveDistance) {
        super(cooldown, windup, duration, moveDistance);
    }

    @Override
    public @NotNull MoveType<GiveGunMove> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public void onInitiate(D4CEntity attacker) {
        super.onInitiate(attacker);

        attacker.equipRevolver();
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final D4CEntity attacker, final LivingEntity user) {
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

    public static final class Type extends AbstractMove.Type<GiveGunMove> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NotNull App<RecordCodecBuilder.Mu<GiveGunMove>, GiveGunMove> buildCodec(RecordCodecBuilder.Instance<GiveGunMove> instance) {
            return baseDefault(instance, GiveGunMove::new);
        }
    }
}
