package net.arna.jcraft.common.attack.moves.metallica;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.core.data.MoveType;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.entity.projectile.ScalpelProjectile;
import net.arna.jcraft.common.entity.stand.MetallicaEntity;
import net.arna.jcraft.registry.JItemRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public final class GiveScalpelMove extends AbstractMove<GiveScalpelMove, MetallicaEntity> {
    public GiveScalpelMove(int cooldown, int windup, int duration, float moveDistance) {
        super(cooldown, windup, duration, moveDistance);
    }

    @Override
    public @NonNull MoveType<GiveScalpelMove> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(MetallicaEntity attacker, LivingEntity user, MoveContext ctx) {
        if (user instanceof final ServerPlayer playerEntity) {
            playerEntity.addItem(JItemRegistry.SCALPEL.get().getDefaultInstance());
            attacker.drainIron(ScalpelProjectile.IRON_COST);
        }

        return Set.of();
    }

    @Override
    protected @NonNull GiveScalpelMove getThis() {
        return this;
    }

    @Override
    public @NonNull GiveScalpelMove copy() {
        return copyExtras(new GiveScalpelMove(getCooldown(), getWindup(), getDuration(), getMoveDistance()));
    }

    public static final class Type extends AbstractMove.Type<GiveScalpelMove> {
        public static final GiveScalpelMove.Type INSTANCE = new GiveScalpelMove.Type();

        @Override
        protected @NotNull App<RecordCodecBuilder.Mu<GiveScalpelMove>, GiveScalpelMove> buildCodec(RecordCodecBuilder.Instance<GiveScalpelMove> instance) {
            return baseDefault(instance, GiveScalpelMove::new);
        }
    }
}
