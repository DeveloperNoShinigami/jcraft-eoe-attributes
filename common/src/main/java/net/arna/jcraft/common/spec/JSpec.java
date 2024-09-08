package net.arna.jcraft.common.spec;

import com.google.common.base.MoreObjects;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.IntSet;
import lombok.Getter;
import lombok.Setter;
import net.arna.jcraft.common.attack.core.IAttacker;
import net.arna.jcraft.common.attack.core.MoveInputType;
import net.arna.jcraft.common.attack.core.MoveMap;
import net.arna.jcraft.common.attack.core.MoveType;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.attack.moves.base.AbstractMultiHitAttack;
import net.arna.jcraft.common.component.living.CommonCooldownsComponent;
import net.arna.jcraft.common.entity.damage.JDamageSources;
import net.arna.jcraft.common.network.s2c.PlayerAnimPacket;
import net.arna.jcraft.common.network.s2c.ServerChannelFeedbackPacket;
import net.arna.jcraft.common.util.CooldownType;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.common.util.SpecAnimationState;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.arna.jcraft.registry.JStatusRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Class that needs to be instantiated per-player to contain temporary data relating to their current state.
 * Used to handle stand-off attacks.
 */
@Getter
public abstract class JSpec<A extends JSpec<A, S>, S extends Enum<S> & SpecAnimationState<A>> implements IAttacker<A, S> {
    private final MoveMap<A, S> moveMap = new MoveMap<>();
    private final MoveContext moveContext = new MoveContext();
    private final SpecType type;
    public final LivingEntity user;
    public final Player player;
    @Setter
    public int moveStun = 0;

    private S state;

    public AbstractMove<?, ? super A> curMove;
    public AbstractMove<?, ? super A> previousAttack;

    public MoveInputType queuedMove;

    public int armorPoints = 0;

    private boolean holding = false;
    private MoveInputType holdingType = null;

    protected JSpec(SpecType type, LivingEntity livingEntity) {
        this.type = type;
        this.user = livingEntity;
        if (this.user instanceof Player playerEntity) {
            this.player = playerEntity;
        } else {
            this.player = null;
        }
        registerMoves(moveMap);
        moveMap.freeze();
        moveMap.forEach(entry -> entry.getMove().registerContextEntries(moveContext));
    }

    @Override
    public LivingEntity getUser() {
        return user;
    }

    @Override
    public LivingEntity getBaseEntity() {
        return user;
    }

    @Override
    public DamageSource getDamageSource() {
        return JDamageSources.create(user.level(), DamageTypes.PLAYER_ATTACK);
    }

    @Override
    public boolean hasUser() {
        return user != null;
    }

    @Override
    public LivingEntity getUserOrThrow() {
        return Objects.requireNonNull(user, "Player must not be null");
    }

    @Override
    public AbstractMove<?, ? super A> getCurrentMove() {
        return curMove;
    }

    @Override
    public void setCurrentMove(AbstractMove<?, ? super A> move) {
        previousAttack = curMove;
        curMove = move;
    }

    @Override
    public void setState(S state) {
        if (player == null) return;
        ((ServerLevel) user.level()).players().forEach(serverPlayer -> PlayerAnimPacket.sendSpec(
                player, serverPlayer, (this.state = state).getKey(getThis()), moveStun, 1f));
    }

    @Override
    public void playAttackerSound(SoundEvent sound, float volume, float pitch) {
        user.level().playSound(null, user.getX(), user.getY(), user.getZ(), sound, SoundSource.PLAYERS,
                volume, pitch);
    }

    protected abstract void registerMoves(MoveMap<A, S> moves);

    public boolean initMove(MoveType type) {
        return handleMove(type);
    }

    public boolean canHoldMove(@Nullable MoveInputType type) {
        if (type == null || type.getMoveType() == null) {
            return false;
        }

        MoveMap.Entry<A, S> entry = moveMap.getFirstValidEntry(type.getMoveType(), getThis());
        return entry == null ? type.isHoldable() : MoreObjects.firstNonNull(entry.getMove().getIsHoldable(), type.isHoldable());
    }

    public final void onUserMoveInput(MoveInputType type, boolean pressed, boolean moveInitiated) {
        onUserMoveInput(curMove, type, pressed, moveInitiated);
    }

    public boolean canAttack() {
        return moveStun <= 0 && !JUtils.isAffectedByTimeStop(user) && !user.hasEffect(JStatusRegistry.DAZED.get());
    }

    public boolean handleMove(MoveType type) {
        return handleMove(type, 1f);
    }

    public boolean handleMove(MoveType type, float animationSpeed) {
        MoveMap.Entry<A, S> entry = moveMap.getFirstValidEntry(type, getThis());
        if (entry == null) {
            return false;
        }

        if (user.isShiftKeyDown()) {
            if (entry.getCrouchingVariant() != null) {
                entry = entry.getCrouchingVariant();
            }
        } else if (!user.onGround() && entry.getAerialVariant() != null) {
            entry = entry.getAerialVariant();
        }
        return handleMove(entry.getMove(), entry.getCooldownType(), entry.getAnimState(), animationSpeed);
    }

    public boolean handleMove(AbstractMove<?, ? super A> move, CooldownType cooldownType, S state) {
        return handleMove(move, cooldownType, state, 1f);
    }

    public boolean handleMove(AbstractMove<?, ? super A> move, CooldownType cooldownType, @Nullable S state, float animationSpeed) {
        move = moveMap.getRegisteredMoveFor(move);

        if (!move.canBeInitiated(getThis())) {
            return false;
        }

        CommonCooldownsComponent cooldowns = JComponentPlatformUtils.getCooldowns(user);
        int cd = cooldowns.getCooldown(cooldownType);
        if (cd > 0) {
            return false;
        }
        cooldowns.setCooldown(cooldownType, move.getCooldown());

        move.onInitiate(getThis());

        //JCraft.LOGGER.info("SERVER: Handling spec attack: " + attack + " in world: " + serverWorld);

        curMove = move.copy()
                .withDuration((int) (move.getDuration() / animationSpeed))
                .withWindup((int) (move.getWindup() / animationSpeed));
        moveStun = curMove.getDuration();

        if (curMove instanceof AbstractMultiHitAttack<?, ?> multiHitAttack) {
            multiHitAttack.withHitMoments(IntSet.of(multiHitAttack.getHitMoments().intStream()
                    .map(i -> (int) (i / animationSpeed))
                    .toArray()));
        }

        var finisher = curMove.getFinisher();
        if (finisher != null) {
            int finisherSwapTick = (int) (finisher.leftInt() / animationSpeed);
            curMove.modifyFinisherTime(finisherSwapTick);
            // Ensure the finisher will happen
            int finisherWindupTime = finisher.right().getWindup() + 1;
            if (moveStun < finisherWindupTime) {
                moveStun = finisherWindupTime;
            }
        }

        armorPoints = move.getArmor();

        if (state != null) {
            setPlayerAnimation((this.state = state).getKey(getThis()), moveStun, animationSpeed);
        }

        return true;
    }

    public void setPlayerAnimation(String animationID, int duration, float animationSpeed) {
        if (player == null) return;
        ((ServerLevel) user.level()).players().forEach(serverPlayer -> PlayerAnimPacket.sendSpec(
                player, serverPlayer, animationID, duration, animationSpeed));
    }

    public void cancelMove() {
        if (curMove != null) {
            curMove.onCancel(getThis());
        }
        curMove = null;
        queuedMove = null;
        armorPoints = 0;
        moveStun = 0;

        if (user == null) {
            return;
        }
        // Cancel player animation if it exists
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeShort(13);
        buf.writeInt(user.getId());
        ServerLevel serverWorld = (ServerLevel) user.level();
        for (ServerPlayer sendPlayer : serverWorld.players()) {
            ServerChannelFeedbackPacket.send(sendPlayer, buf);
        }
    }

    public boolean shouldSneak() {
        return false;
    }

    public void processAttackClient() {
    }

    public void tickSpec() {
        if (user.isSpectator()) {
            return;
        }

        Level world = user.level();

        if (world.isClientSide()) {
            //JCraft.LOGGER.info("CLIENT: Ticking spec " + this);

            if (moveStun > 0) {
                //JCraft.LOGGER.info("CLIENT: Movestun is " + moveStun);

                //player.setSneaking(shouldSneak());

                // Process attack
                moveStun--;
                processAttackClient();
            }

            return;
        }

        if (moveStun <= 0) {
            armorPoints = 0;

            if (queuedMove != null) {
                initMove(queuedMove.getMoveType());
                queuedMove = null;
            }

            if (curMove != previousAttack && curMove != null) {
                previousAttack = curMove;
            }
            return;
        }

        //JCraft.LOGGER.info("SERVER: Movestun is " + moveStun);

        // Process attack
        AbstractMove<?, ? super A> move = this.curMove;
        moveStun--;
        if (move != null) {
            // Make sure the correct holding type is set
            MoveInputType curMoveInputType = MoveInputType.fromMoveType(move.getMoveType());
            if (canHoldMove(curMoveInputType) && getHoldingType() != curMoveInputType) {
                setHoldingType(curMoveInputType);
                //setHolding(true);
            }

            move.tick(getThis());
        }
    }

    @Override
    public boolean isRemote() {
        return false;
    }

    @Override
    public boolean isHolding() {
        return holding;
    }

    @Override
    public void setHolding(boolean holding) {
        this.holding = holding;
    }

    @Override
    public MoveInputType getHoldingType() {
        return holdingType;
    }

    @Override
    public void setHoldingType(MoveInputType holdingType) {
        this.holdingType = holdingType;
    }

    public abstract A getThis();
}
