package net.arna.jcraft.common.attack.moves.killerqueen;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.data.MoveType;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.core.ctx.MoveVariable;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.entity.stand.KillerQueenEntity;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.arna.jcraft.registry.JItemRegistry;
import net.arna.jcraft.registry.JSoundRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public final class CoinTossMove extends AbstractMove<CoinTossMove, KillerQueenEntity> {
    public static final MoveVariable<ItemEntity> COIN = new MoveVariable<>(ItemEntity.class);

    public CoinTossMove(final int cooldown) {
        super(cooldown, 0, 0, 1f);
        ranged = true;
    }

    @Override
    public @NotNull MoveType<CoinTossMove> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final KillerQueenEntity attacker, final LivingEntity user, final MoveContext ctx) {
        ItemEntity coin = ctx.get(COIN);
        final Vec3 lookVec = user.getLookAngle().scale(0.75);
        if (coin != null) {
            coin.discard();
        }
        coin = new ItemEntity(attacker.level(), user.getX(), user.getY() + user.getBbHeight() * 2 / 3, user.getZ(),
                new ItemStack(JItemRegistry.KQ_COIN.get(), 1), lookVec.x, lookVec.y, lookVec.z);
        coin.setNeverPickUp();

        attacker.level().addFreshEntity(coin);

        JComponentPlatformUtils.getBombTracker(user).getMainBomb().setBomb(coin);

        attacker.playSound(JSoundRegistry.COIN_TOSS.get(), 1, 1);

        return Set.of();
    }

    @Override
    public void registerExtraContextEntries(final MoveContext ctx) {
        ctx.register(COIN);
    }

    @Override
    protected @NonNull CoinTossMove getThis() {
        return this;
    }

    @Override
    public @NonNull CoinTossMove copy() {
        return copyExtras(new CoinTossMove(getCooldown()));
    }

    public static class Type extends AbstractMove.Type<CoinTossMove> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NotNull App<RecordCodecBuilder.Mu<CoinTossMove>, CoinTossMove> buildCodec(RecordCodecBuilder.Instance<CoinTossMove> instance) {
            return instance.group(extras(), cooldown()).apply(instance, applyExtras(CoinTossMove::new));
        }
    }
}
