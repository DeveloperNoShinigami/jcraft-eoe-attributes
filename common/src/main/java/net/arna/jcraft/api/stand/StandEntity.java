package net.arna.jcraft.api.stand;

import com.google.common.base.MoreObjects;
import it.unimi.dsi.fastutil.Pair;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import mod.azure.azurelib.animatable.GeoEntity;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.core.animation.RawAnimation;
import mod.azure.azurelib.core.object.PlayState;
import mod.azure.azurelib.util.AzureLibUtil;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.api.AttackData;
import net.arna.jcraft.api.MoveSelectionResult;
import net.arna.jcraft.api.MoveUsage;
import net.arna.jcraft.api.attack.IAttacker;
import net.arna.jcraft.api.attack.MoveMap;
import net.arna.jcraft.api.attack.MoveSet;
import net.arna.jcraft.api.attack.MoveSetManager;
import net.arna.jcraft.api.attack.enums.MoveClass;
import net.arna.jcraft.api.attack.enums.MoveInputType;
import net.arna.jcraft.api.attack.moves.AbstractBarrageAttack;
import net.arna.jcraft.api.attack.moves.AbstractCounterAttack;
import net.arna.jcraft.api.attack.moves.AbstractMove;
import net.arna.jcraft.api.attack.moves.AbstractSimpleAttack;
import net.arna.jcraft.api.component.living.CommonCooldownsComponent;
import net.arna.jcraft.api.component.living.CommonHitPropertyComponent;
import net.arna.jcraft.api.component.living.CommonStandComponent;
import net.arna.jcraft.api.registry.JSoundRegistry;
import net.arna.jcraft.api.registry.JStatRegistry;
import net.arna.jcraft.api.registry.JStatusRegistry;
import net.arna.jcraft.api.spec.JSpec;
import net.arna.jcraft.common.attack.core.MoveMapImpl;
import net.arna.jcraft.common.attack.core.itfs.AttackRotationOffsetOverride;
import net.arna.jcraft.common.entity.damage.JDamageSources;
import net.arna.jcraft.common.entity.stand.PurpleHazeEntity;
import net.arna.jcraft.common.network.c2s.PlayerInputPacket;
import net.arna.jcraft.common.tickable.MoveTickQueue;
import net.arna.jcraft.common.util.*;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.JumpControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import static net.arna.jcraft.JCraft.comboBreak;
import static net.arna.jcraft.api.Attacks.damageLogic;

public abstract class StandEntity<E extends StandEntity<E, S>, S extends Enum<S> & StandAnimationState<E>>
        extends Mob implements GeoEntity, IAttacker<E, S>, ICustomDamageHandler, MoveSet.ReloadListener<E, S> {

    // TODO: finish custom player idle poses for all stands

    private MoveSet<E, S> moveSet;
    @Getter
    private final @NonNull MoveMap<E, S> moveMap = new MoveMapImpl<>();

    private static final EntityDataAccessor<String> MOVE_SET;

    private static final EntityDataAccessor<Integer> STATE;
    private static final EntityDataAccessor<Boolean> SAMESTATE; // Marks if the state was set to what it already was during the last setState() call
    private static final EntityDataAccessor<Boolean> RESET; // Set to true when state is set to idle. Set back to false when the after-idle reset code has run.
    private static final EntityDataAccessor<Integer> MOVESTUN;

    private static final EntityDataAccessor<Integer> SKIN;
    private static final EntityDataAccessor<Float> ROTATIONOFFSET;
    private static final EntityDataAccessor<Float> DISTANCEOFFSET;

    private static final EntityDataAccessor<Float> ALPHA_OVERRIDE;

    private static final EntityDataAccessor<Float> STANDGAUGE;

    private static final EntityDataAccessor<Float> FREEX;
    private static final EntityDataAccessor<Float> FREEY;
    private static final EntityDataAccessor<Float> FREEZ;

    private static final EntityDataAccessor<Boolean> FREE;
    private static final EntityDataAccessor<Boolean> REMOTE;

    private static final EntityDataAccessor<String> USER_POSE;

    @Setter
    protected int tsTime = 0;
    @Getter
    private float prevAlpha = 1f;

    @Getter
    @Setter
    @Nullable
    private LivingEntity user = null;

    public boolean wantToBlock = false;
    public boolean blocking = false;

    private boolean holding = false;
    protected boolean idleOverride = false;

    @Getter
    @Setter
    protected boolean standby = false;

    public static final float ATTACK_ROTATION = 90f;
    @Getter
    protected float maxStandGauge = 90f;

    @Getter @Setter
    protected MoveInputType queuedMove;
    private MoveInputType holdingType;
    private AbstractMove<?, ? super E> curMove;
    @Getter
    private MoveUsage moveUsage;
    public AbstractMove<?, ? super E> prevMove;
    public int armorPoints;
    private boolean performedThisTick;

    // Player Movement Input
    public int lastRemoteInputTime;
    public Vec3 remoteSpeed = Vec3.ZERO;
    @Getter
    private double remoteForwardInput = 0;
    @Getter
    private double remoteSideInput = 0;
    @Setter
    private boolean remoteJumpInput = false, remoteSneakInput = false;

    private boolean playSummonAnim = true;
    @Setter
    private boolean playSummonSound = true, playDesummonSound = true;

    // Data
    @Getter
    private final StandType standType;
    private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);
    protected Vector3f[] auraColors = {new Vector3f(), new Vector3f(1f, 0f, 0f), new Vector3f(0f, 1f, 0f), new Vector3f(0f, 0f, 1f)};

    protected StandEntity(StandType type, Level world) {
        super(type.getEntityType(), world);
        this.moveSet = MoveSetManager.get(type, "default");
        if (this.moveSet == null) {
            throw new NoSuchElementException("No 'default' move set found for stand" + type);
        }
        moveSet.registerListener(this);

        noPhysics = true;
        standType = type;
        this.noCulling = true;

        assert getThis() == this;
    }

    // State controls
    static {
        MOVE_SET = SynchedEntityData.defineId(StandEntity.class, EntityDataSerializers.STRING);

        STATE = SynchedEntityData.defineId(StandEntity.class, EntityDataSerializers.INT);
        SAMESTATE = SynchedEntityData.defineId(StandEntity.class, EntityDataSerializers.BOOLEAN);
        RESET = SynchedEntityData.defineId(StandEntity.class, EntityDataSerializers.BOOLEAN);

        MOVESTUN = SynchedEntityData.defineId(StandEntity.class, EntityDataSerializers.INT);

        SKIN = SynchedEntityData.defineId(StandEntity.class, EntityDataSerializers.INT);
        ROTATIONOFFSET = SynchedEntityData.defineId(StandEntity.class, EntityDataSerializers.FLOAT);
        DISTANCEOFFSET = SynchedEntityData.defineId(StandEntity.class, EntityDataSerializers.FLOAT);

        ALPHA_OVERRIDE = SynchedEntityData.defineId(StandEntity.class, EntityDataSerializers.FLOAT);

        STANDGAUGE = SynchedEntityData.defineId(StandEntity.class, EntityDataSerializers.FLOAT);

        FREE = SynchedEntityData.defineId(StandEntity.class, EntityDataSerializers.BOOLEAN);
        REMOTE = SynchedEntityData.defineId(StandEntity.class, EntityDataSerializers.BOOLEAN);

        FREEX = SynchedEntityData.defineId(StandEntity.class, EntityDataSerializers.FLOAT);
        FREEY = SynchedEntityData.defineId(StandEntity.class, EntityDataSerializers.FLOAT);
        FREEZ = SynchedEntityData.defineId(StandEntity.class, EntityDataSerializers.FLOAT);

        USER_POSE = SynchedEntityData.defineId(StandEntity.class, EntityDataSerializers.STRING);
    }

    public boolean allowMoveHandling() {
        return true;
    }

    @NonNull
    public LivingEntity getUserOrThrow() {
        if (user == null) {
            throw new NullPointerException("No user set");
        }
        return user;
    }

    public StandData getStandData() {
        return getStandType().getData();
    }

    public boolean hasUser() {
        return user != null;
    }

    public int getModeOrdinal() {
        return 0;
    }

    public S getState() {
        return boxState(getRawState());
    }

    public int getRawState() {
        return entityData.get(STATE);
    }

    private boolean isReset() {
        return entityData.get(RESET);
    }

    protected void setReset(boolean reset) {
        entityData.set(RESET, reset);
    }

    /**
     * Sets the stands state directly
     */
    public void setStateNoReset(@Nullable S state) {
        if (state == null) {
            return;
        }
        setRawStateNoReset(state.ordinal());
    }

    public void setRawStateNoReset(int state) {
        entityData.set(STATE, state);
    }

    /**
     * Sets the stands state with extra processing
     */
    public void setState(S state) {
        setRawState(state.ordinal());
    }

    public void setRawState(int state) {
        int oldState = getRawState();
        boolean sameState = oldState == state || oldState <= 1;
        entityData.set(STATE, state);
        entityData.set(SAMESTATE, sameState); // Pretty much just an animation reset flag
        // If we're switched states and are moving to idle, perform reset logic.
        setReset(!sameState && state == getIdleState().ordinal());
    }

    public boolean isSameState() {
        return entityData.get(SAMESTATE);
    }

    public void setSameState(boolean sameState) {
        entityData.set(SAMESTATE, sameState);
    }

    public int getMoveStun() {
        return entityData.get(MOVESTUN);
    }

    /**
     * Sets how many ticks the stand will be occupied doing an animation for
     */
    public void setMoveStun(int moveStun) {
        entityData.set(MOVESTUN, moveStun);
    }

    public int getSkin() {
        return Mth.clamp(entityData.get(SKIN), 0, getStandData().getInfo().getSkinCount());
    }

    public void setSkin(int skin) {
        if (skin < 0 || skin >= getStandData().getInfo().getSkinCount()) {
            skin = 0;
        }
        entityData.set(SKIN, skin);
    }

    public float getRotationOffset() {
        return this.entityData.get(ROTATIONOFFSET);
    }

    /**
     * Sets the angle of the offset the stand is at relative to the user, used in the cylindrical coordinates system in {@link net.arna.jcraft.mixin.EntityMixin}
     */
    public void setRotationOffset(float rotationOffset) {
        this.entityData.set(ROTATIONOFFSET, rotationOffset);
    }

    public float getDistanceOffset() {
        return this.entityData.get(DISTANCEOFFSET);
    }

    /**
     * Sets the distance between the stand and user
     */
    public void setDistanceOffset(float distanceOffset) {
        this.entityData.set(DISTANCEOFFSET, distanceOffset);
    }

    public boolean hasAlphaOverride() {
        return getAlphaOverride() >= 0;
    }

    public float getAlphaOverride() {
        return this.entityData.get(ALPHA_OVERRIDE);
    }

    public void setAlphaOverride(float alpha) {
        entityData.set(ALPHA_OVERRIDE, alpha);
    }

    public void resetAlphaOverride() {
        setAlphaOverride(-1);
    }

    public float getStandGauge() {
        return this.entityData.get(STANDGAUGE);
    }

    public void setStandGauge(float standGauge) {
        this.entityData.set(STANDGAUGE, standGauge);
    }

    public boolean isFree() {
        return this.entityData.get(FREE);
    }

    /**
     * Changes whether the stand is detached from the user
     */
    public void setFree(boolean free) {
        this.entityData.set(FREE, free);
    }

    /**
     * Called when the move set gets reloaded.
     * @param moveSet The reloaded move set.
     */
    @Override
    public void onMoveSetReload(MoveSet<E, S> moveSet) {
        if (!this.moveSet.getName().equals(moveSet.getName())) return;

        moveMap.copyFrom(moveSet.getMoveMap());
    }

    /**
     * Switches the move set to a different, registered move set.
     * @param name The name of the move set to switch to.
     */
    protected void switchMoveSet(String name) {
        MoveSet<E, S> moveSet = MoveSetManager.get(getStandType(), name);
        if (moveSet == null) {
            JCraft.LOGGER.error("Move set '{}' not found for {}", name, getStandType());
            return;
        }

        switchMoveSet(moveSet);
        if (!level().isClientSide())
            entityData.set(MOVE_SET, name, true);
    }

    private void switchMoveSet(MoveSet<E, S> moveSet) {
        this.moveSet = moveSet;
        moveSet.registerListener(this);
        moveMap.copyFrom(moveSet.getMoveMap());
    }

    /**
     * Synchronises the user inputs serverside
     */
    public void updateRemoteInputs(int f, int s, boolean j, boolean c) {
        // These persist, so implementation for cleaning should be done in the stand code
        Vec3 v = new Vec3(f, 0, s).normalize();
        remoteForwardInput = v.x;
        remoteSideInput = v.z;
        remoteJumpInput = j;
        remoteSneakInput = c;
        lastRemoteInputTime = tickCount;
    }

    public boolean getRemoteJumpInput() {
        return remoteJumpInput;
    }

    public boolean getRemoteSneakInput() {
        return remoteSneakInput;
    }

    public boolean isRemote() {
        return this.entityData.get(REMOTE);
    }

    /**
     * @return Whether this stand should be controllable in remote mode. Certain stands should not (i.e. {@link PurpleHazeEntity}).
     */
    public boolean remoteControllable() {
        return true;
    }

    /**
     * @return Whether this stand is in remote mode and should be controllable.
     */
    public boolean isRemoteAndControllable() {
        return isRemote() && remoteControllable();
    }

    public void setRemote(boolean r) {
        this.entityData.set(REMOTE, r);
        if (r) {
            beginRemote();
        } else {
            endRemote();
        }
    }

    public void togglePilotMode() {
        setRemote(!isRemote());
//        registerMoves(); // Switching movesets
        // TODO
    }

    /**
     * Puts the stand into remote mode.
     * @see StandEntity#setRemote(boolean) Where this method is called.
     */
    protected void beginRemote() {
        if (user == null) {
            return;
        }

        setFree(true);

        Vec3 fPos = user.position().add(user.getLookAngle());
        remoteSpeed = user.getDeltaMovement().scale(2); // Inertia

        setAlphaOverride(0.1f);

        unRide();

        noPhysics = false;
        hasImpulse = true;
        setPosRaw(fPos.x, user.getY() + 0.5, fPos.z);
    }

    /**
     * Ends remote mode instantly
     */
    protected void endRemote() {
        setFree(false);
        resetAlphaOverride();
        if (user != null) startRiding(user);
        noPhysics = true;
    }

    /*
     * Returns whether the utility should be used by the stand, otherwise calls initClientUtility()
     */
    /*
    @Environment(EnvType.CLIENT)
    public boolean allowUtilityUse() {
        return true;
    }
    public void initClientUtility() {
    }
    */

    /**
     * Gets this Stands position while detached
     */
    public Vector3f getFreePos() {
        return new Vector3f(this.entityData.get(FREEX), this.entityData.get(FREEY), this.entityData.get(FREEZ));
    }

    /**
     * Sets this Stands position while detached
     *
     * @param freePos new position
     */
    public void setFreePos(Vector3f freePos) {
        this.entityData.set(FREEX, freePos.x());
        this.entityData.set(FREEY, freePos.y());
        this.entityData.set(FREEZ, freePos.z());
    }

    /**
     * Sets the pose that will be applied to the user when the stand is summoned.
     * If not set, defaults to the pose corresponding to the stand type's ID.
     * <p>
     * If the given pose does not exist, no pose will be applied.
     * @param poseId The ID of the pose to apply to the user when the stand is summoned.
     */
    public void setUserPose(@NonNull ResourceLocation poseId) {
        entityData.set(USER_POSE, poseId.toString());
    }

    /**
     * Gets the pose that will be applied to the user when the stand is summoned.
     * If not set, defaults to the pose corresponding to the stand type's ID.
     * @return The ID of the pose to apply to the user when the stand is summoned.
     */
    public @NotNull ResourceLocation getUserPose() {
        String pose = entityData.get(USER_POSE);
        return pose.isEmpty() ? standType.getId() : new ResourceLocation(pose);
    }

    @Override
    public LivingEntity getBaseEntity() {
        return this;
    }

    @Override
    public DamageSource getDamageSource() {
        return JDamageSources.stand(this);
    }

    /**
     * May return null during the post-tick handling of attacks.
     * @return This Stands current move.
     */
    @Override
    public @Nullable AbstractMove<?, ? super E> getCurrentMove() {
        return curMove;
    }

    /**
     * @return Whether the followup condition was passed.
     */
    @SuppressWarnings("unchecked")
    public boolean tryFollowUp(final MoveClass in, final MoveClass followupType) {
        if (in == followupType && curMove != null && curMove.getMoveClass() == followupType && getMoveStun() < curMove.getWindupPoint()) {
            final AbstractMove<?, ? super E> followup = curMove.getFollowup();
            if (followup != null) {
                setMove(followup, (S) followup.getAnimation());
                return true;
            }
        }
        return false;
    }

    @Override
    public void setCurrentMove(@Nullable AbstractMove<?, ? super E> move) {
        prevMove = curMove;
        curMove = move;
        if (curMove != null) {
            moveUsage = new MoveUsage(tickCount, curMove);
        }
    }

    @Override
    public @NotNull Vec3 getLookAngle() {
        // Ignore pitch in rotation vectors.
        return calculateViewVector(0, getYRot());
    }

    @Override
    public @NotNull SoundSource getSoundSource() {
        return SoundSource.PLAYERS;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(MOVE_SET, "default");

        entityData.define(STATE, 0);
        entityData.define(SAMESTATE, false);
        entityData.define(RESET, true);

        entityData.define(MOVESTUN, 0);

        entityData.define(SKIN, 0);
        entityData.define(ROTATIONOFFSET, -90f);
        entityData.define(DISTANCEOFFSET, 1f);

        entityData.define(ALPHA_OVERRIDE, -1f);

        entityData.define(STANDGAUGE, 45f);

        entityData.define(FREE, false);
        entityData.define(REMOTE, false);

        entityData.define(FREEX, 0f);
        entityData.define(FREEY, 0f);
        entityData.define(FREEZ, 0f);

        entityData.define(USER_POSE, "");
    }

    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);

        if (!MOVE_SET.equals(key) || !level().isClientSide()) return;
        // Update move set on client when it changes
        switchMoveSet(entityData.get(MOVE_SET));
    }

    // Attack controls

    /**
     * @return whether the stand should be able to attack
     */
    public boolean canAttack() {
        // If we have no user or the user is affected by time stop or dazed, we can't attack.
        if (user == null || JUtils.isAffectedByTimeStop(user) || user.hasEffect(JStatusRegistry.DAZED.get()))
            return false;

        // If we're remote and dazed, we can't attack.
        if (isRemote() && hasEffect(JStatusRegistry.DAZED.get())) {
            return false;
        }

        // If the current move allows attacks, we can attack.
        if (getCurrentMove() != null && !getCurrentMove().preventsMoves()) {
            return true;
        }

        // Otherwise, we can only attack if the move stun has ended.
        return getMoveStun() <= 0;
    }

    /**
     * As a general rule, low-hitbox moves should modify this to false, since otherwise players may move the hitbox into the ground.
     * This should also usually use client-visible values to prevent desync.
     * @return whether the stand should change its height depending on the user's look pitch.
     */
    public boolean shouldOffsetHeight() {
        return getState().ordinal() > 0;
    }

    public boolean handleMove(MoveClass moveClass) {
        MoveMap.Entry<E, S> entry = getFirstValidEntry(moveClass);
        if (entry == null) {
            return false;
        }

        AbstractMove<?, ? super E> move = entry.getMove();
        return handleMove(move.isCopyOnUse() ? move.copy() : move, entry.getCooldownType(), entry.getAnimState());
    }

    /**
     * Initiates an attack with the stand
     *
     * @param move         move to handle
     * @param cooldownType type of cooldown to start
     * @param animState    the state to put the stand into
     */
    public boolean handleMove(AbstractMove<?, ? super E> move, CooldownType cooldownType, @Nullable S animState) {
        if (!move.canBeInitiated(getThis())) {
            return false;
        }

        if (cooldownType != null && move.getCooldown() > 0) {
            CommonCooldownsComponent cooldowns = JComponentPlatformUtils.getCooldowns(getUser());
            int cooldown = cooldowns.getCooldown(cooldownType);

            if (cooldown > 0) {
                return false;
            }

            if (!move.isManualCooldown()) cooldowns.setCooldown(cooldownType, move.getCooldown());
        }

        setMove(move, animState);
        return true;
    }

    /**
     * Instantly sets the stand's move without checking if it can be.
     *
     * @param move      move to set
     * @param animState int identifier for which state to put the stand into
     */
    public void setMove(AbstractMove<?, ? super E> move, @Nullable S animState) {
        move.onInitiate(getThis());

        // If the attack has a duration of 0, just perform it immediately.
        if (move.getDuration() == 0) {
            move.doPerform(getThis());
            return;
        }

        setCurrentMove(move);
        setMoveStun(move.getDuration());
        //setReset(false); // makes it worse
        if (animState != null) {
            setState(animState);
        }
        armorPoints = move.getArmor();
    }

    public final void onUserMoveInput(MoveInputType type, boolean pressed, boolean moveInitiated) {
        onUserMoveInput(getCurrentMove(), type, pressed, moveInitiated);
    }

    // Stock attacks to define

    // TODO this method should be removed (at least in the way it's currently used, i.e., for move init logic).
    // Special handling should be done in some kind of preInit method (or just onInitiate) in AbstractMove,
    // rather than in the stand entity itself.
    /**
     * Initiates a move of the specified moveClass.
     *
     * @param moveClass The moveClass of move to initiate.
     * @return Whether the move was initiated.
     */
    public boolean initMove(MoveClass moveClass) {
        if (getCurrentMove() != null) {
            if (getCurrentMove().onInitMove(getThis(), moveClass)) {
                return true;
            }

            if (getCurrentMove().getFollowup() != null && getCurrentMove().getFollowupFrame().isPresent() &&
                    getCurrentMove().getMoveClass() == moveClass && getMoveStun() <= getCurrentMove().getFollowupFrame().getAsInt()) {
                moveMap.initiateFollowup(getThis(), getCurrentMove(), false, 0);
            }
        }

        return handleMove(moveClass);
    }

    public boolean canHoldMove(@Nullable MoveInputType type) {
        if (type == null || type.getMoveClass(standby) == null) {
            return false;
        }
        MoveMap.Entry<E, S> entry = getFirstValidEntry(type.getMoveClass(standby));
        return entry == null ? type.isHoldable(standby) : MoreObjects.firstNonNull(entry.getMove().getIsHoldable(), type.isHoldable(standby));
    }

    /**
     * Defines what happens while the stand is blocking
     */
    public void standBlock() {
        if (!hasUser()) {
            return;
        }
        // Projectile deflection
        List<Projectile> toDeflect = this.level().getEntitiesOfClass(Projectile.class, this.getBoundingBox().inflate(0.75f), EntitySelector.ENTITY_STILL_ALIVE);

        for (Projectile projectile : toDeflect) {
            if (projectile.getOwner() == user) {
                continue;
            }
            projectile.setDeltaMovement(projectile.getDeltaMovement().scale(-0.5).add(0, -0.1, 0));
            projectile.hurtMarked = true;
        }

        JCraft.stun(user, 2, 2);
        getUserOrThrow().addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 5, 3, false, false, true));
    }

    public void tryUnblock() {
        if (getMoveStun() < 1) {
            blocking = false;
        }
    }

    // Define desummon conditions
    public void desummon() {
        desummon(true);
    }

    public void desummon(boolean playSound) {
        if (getCurrentMove() != null || getMoveStun() > 0) {
            return;
        }
        playDesummonSound = playSound;
        discard();
    }

    // Define idle override
    public void idleOverride() {
    }

    public void cancelMove() {
        cancelMove(false);
    }

    /**
     * Cancels the stand's move instantly
     * @param offensiveCancel Whether the move cancellation was initiated by another party,
     *                        and should execute the to-be-cancelled move if {@link AbstractMove#shouldPerform(IAttacker, int)} is true.
     *                        This is used to mitigate the problem of subtick update priority between different IAttackers.
     */
    public void cancelMove(boolean offensiveCancel) {
        if (curMove != null) {
            if (offensiveCancel && !performedThisTick && curMove.shouldPerform(getThis(), getMoveStun() - 1)) {
                setPerformedThisTick(true);
                curMove.perform(getThis(), getUserOrThrow());
            }
            if (curMove != null) {
                curMove.onCancel(getThis());
            }
        }
        setCurrentMove(null);
        setMoveStun(0);
        setState(getIdleState());
        setReset(true);
    }

    /**
     * Returns whether the stand defaults to returning to the user while idle and detached
     */
    public boolean defaultToNear() {
        return !isRemote();
    }

    @Override
    public boolean isNoGravity() {
        if (isFree() && !isRemote()) {
            return true;
        }
        return super.isNoGravity();
    }

    @Override
    public boolean isInvertedHealAndHarm() {
        if (user != null) {
            return user.isInvertedHealAndHarm();
        }
        return super.isInvertedHealAndHarm();
    }

    /**
     * does evrything :)
     */
    @Override
    public void tick() {
        if (user == null && getVehicle() instanceof LivingEntity vehicle) {
            user = vehicle;
        }

        super.tick();
        if (isDeadOrDying()) {
            return;
        }

        final boolean client = level().isClientSide;
        if (tickCount == 1) {
            playSummonSound();
            if (!client && getUser() instanceof Player player) {
                player.awardStat(JStatRegistry.STAND_SUMMONED.get());
            }
        }
        prevAlpha = getAlphaOverride();

        int moveStun = getMoveStun();
        if (moveStun > 0 && !(blocking && wantToBlock && moveStun == 1)) {
            setMoveStun(--moveStun); // Counting down animation time or similar
        }
        if (playSummonAnim && (moveStun > 0 || tickCount > getStandData().getSummonData().getAnimDuration() || getState() == getBlockState())) {
            playSummonAnim = false;
        }

        final boolean isFree = isFree();
        final boolean isRemote = isRemote();

        if (!hasUser()) {
            if (!client && !isFree && !isRemote) {
                discard();
            }
            return;
        }

        // Common code for remote mode
        if (isRemote) {
            if (isPassenger()) {
                unRide();
            }
            if (Objects.requireNonNull(user).isAlive()) {
                // Clientside rotational sync for controllable remote mode
                if (remoteControllable()) {
                    user.setYBodyRot(user.getYHeadRot());

                    setYHeadRot(user.getYHeadRot());
                    setRot(user.getYRot(), user.getXRot());
                }
            } else {
                discard();
            }
        } else if (!isPassenger() && !isFree()) {
            startRiding(Objects.requireNonNull(user), true);
        }

        /*
        JCraft.LOGGER.info(
                (client ? "CLIENT:" : "SERVER:") + " Ticking stand " + this +
                        "\nUser: " + user +
                        "\nVehicle (stand): " + getVehicle() +
                        "\nFree: " + getFree() +
                        "\nRemote: " + getRemote()
        );
         */

        if (client) {
            JCraft.getClientEntityHandler().standEntityClientTick(this);
            return;
        }

        ServerPlayer userPlayer = null;
        if (user instanceof ServerPlayer serverPlayerEntity) {
            userPlayer = serverPlayerEntity;
        }

        // Reset samestate
        if (isSameState()) {
            setSameState(false);
        }

        // Make sure the user is using this stand
        if (JUtils.getStand(user) != this) {
            discard();
        }

        // Tick moves
        moveMap.tickMoves(getThis());

        // Block break / Guard crush check
        if (getStandGauge() < 1) {
            user.addEffect(new MobEffectInstance(JStatusRegistry.DAZED.get(), 40, 2));
            playSound(SoundEvents.TOTEM_USE, 1, 0.5f);
            blocking = false;
            kill();
        }

        AbstractMove<?, ? super E> move = this.getCurrentMove();
        if (defaultToNear() && moveStun <= 0) {
            if (move == null) {
                if (this.queuedMove == null) {
                    setFree(false);
                }
            } else if (move.isCounter()) { //noinspection unchecked // not an issue here
                ((AbstractCounterAttack<?, ? super E>) move).whiff(getThis(), user);
                moveStun = 1;
            }
        }

        final boolean isRemoteAndControllable = isRemote && remoteControllable();

        // Rotate with user (provided user controls the stand)
        if (!isFree || isRemoteAndControllable) {
            setYHeadRot(user.getYHeadRot());
            setRot(user.getYRot(), user.getXRot());
        }

        // Remote mode users cannot move while controlling
        if (isRemoteAndControllable) {
            user.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 5, 9, true, false));
        }

        // Attack logic
        if (move != null) {
            MoveTickQueue.queueTick(getThis(), move, getMoveStun());

            // Make sure the correct holding type is set
            MoveInputType curMoveInputType = MoveInputType.fromMoveClass(move.getMoveClass());
            if (canHoldMove(curMoveInputType) && getHoldingType() != curMoveInputType) {
                setHoldingType(curMoveInputType);
                //setHolding(true);
            }

            if (moveStun >= 0 && !blocking) {
                final float attackDist = move.getMoveDistance();

                if (!move.isCharge()) {
                    if (!isRemote) {
                        // TODO: find a cleaner way to slow down the users attack speed
                        user.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 5, 4, true, false));
                    }

                    setAttackRotationOffset();
                    setDistanceOffset(attackDist);
                }
            }
        }

        if (wantToBlock && canBlock()) {
            if (isFree() && !isRemote()) {
                setFree(false);
            }
            tryBlock();
        }

        if (moveStun <= 0 && !blocking) {
            // Attack buffering
            if (queuedMove != null) {
                doQueuedMove(userPlayer);
            } else if (!idleOverride) {
                // Process idle
                if (curMove != null) setCurrentMove(null);

                setStandGauge(Mth.clamp(this.getStandGauge() + 0.5f, 0, maxStandGauge));

                if (getRawState() != 0 || isReset()) {
                    setRawState(0);
                    setReset(false);

                    setDistanceOffset(getStandData().getIdleDistance());
                    setRotationOffset(getStandData().getIdleRotation());
                }
            } else {
                idleOverride();
            }
        } else if (blocking) { // Process block
            if (wantToBlock) {
                if (curMove != null) setCurrentMove(null);

                if (moveStun < 1) {
                    setMoveStun(1);
                }

                setDistanceOffset(getStandData().getBlockDistance());
                setRotationOffset(ATTACK_ROTATION);
                standBlock();
                setStateNoReset(getBlockState()); // Set after standBlock() so blocking logic can account for previous state
            } else {
                tryUnblock();
            }
        }

        tsTime--;

        // JCraft.LOGGER.info( "State: " + this.getState() + " Movestun: " + curMoveStun + " Currently attacking: " + (this.curAttack != null)); // Massive debug log

        if (getCurrentMove() != prevMove && getCurrentMove() != null)
        //JCraft.LOGGER.info("Logged previous attack change: " + this.curAttack + " " + this.previousAttack);
        {
            prevMove = getCurrentMove();
        }
    }

    public boolean canBlock() {
        return !blocking && (user == null || !DashData.isDashing(user)) && canAttack();
    }

    protected void doQueuedMove(@Nullable ServerPlayer userPlayer) {
        if (queuedMove == MoveInputType.STAND_SUMMON) {
            setCurrentMove(null);
            desummon();
        } else {
            if (userPlayer != null && canHoldMove(queuedMove)) {
                setHolding(PlayerInputPacket.getInputStateManager(userPlayer).heldInputs.containsKey(queuedMove));
                if (isHolding()) {
                    setHoldingType(queuedMove);
                }
            }
            initMove(queuedMove.getMoveClass(standby));
        }

        queuedMove = null;
    }

    public void tryBlock() {
        this.blocking = true;
    }

    /**
     * Called when curAttack isn't null, and it's being processed
     * Sets the StandEntities rotation (in cylindrical coordinates) to the attack position
     */
    private void setAttackRotationOffset() {
        float rotation = getCurrentMove() instanceof AttackRotationOffsetOverride override
                ? override.getAttackRotationOffset(getThis())
                : ATTACK_ROTATION;

        setRotationOffset(rotation);
    }

    protected boolean shouldNotPlaySummonSound() {
        return user instanceof ArmorStand || !playSummonSound;
    }

    protected void playSummonSound() {
        if (shouldNotPlaySummonSound()) {
            return;
        }

        SummonData summonData = getStandData().getSummonData();
        SoundEvent summonSound = summonData.getSound();
        if (summonSound != null) {
            playSound(summonSound, 1f, 1f);
        }
        if (summonSound == null || summonData.isPlayGenericSound()) {
            playSound(JSoundRegistry.STAND_SUMMON.get(), 1f, 1f);
        }
    }

    @Override
    public void stopRiding() {
        if (tickCount == 0 && getRemovalReason() == null) {
            // This may be necessary because the packet that sets passengers arrives early on Forge
            // JCraft.LOGGER.warn("Prevented stopRiding() call for recently created {}", this);
            return;
        }
        if (getVehicle() == null) {
            return;
        }
        super.stopRiding();
        if (isRemote() || level().isClientSide) {
            return;
        }
        if (playDesummonSound) {
            playSound(JSoundRegistry.STAND_DESUMMON.get(), 1, 1);
        }
        discard();
    }

    @Override
    public void readAdditionalSaveData(@NonNull final CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);

        setSkin(nbt.getInt("Skin"));
        if (getVehicle() instanceof LivingEntity livingEntity) {
            final CommonStandComponent standComp = JComponentPlatformUtils.getStandComponent(livingEntity);
            if (standComp.getType() == standType) {
                standComp.setStand(this);
            }
        }
    }

    @Override
    public void addAdditionalSaveData(@NonNull final CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);

        nbt.putInt("Skin", getSkin());
    }

    @Override
    public boolean hurt(@NonNull final DamageSource source, float amount) {
        if (user == null ||
                source.getEntity() == user ||
                user.isInvulnerableTo(source) ||
                source.is(DamageTypes.FALLING_BLOCK) ||
                source.is(DamageTypes.DROWN)) {
            return false;
        }

        // Perfectly block projectiles
        if (blocking && (source.is(DamageTypes.MOB_PROJECTILE) || source.is(DamageTypes.ARROW))) {
            return false;
        }

        // AoE effects have damage nerfed
        if (source.is(DamageTypes.MAGIC) || source.is(DamageTypes.EXPLOSION)) {
            amount /= 2.0F;
        }

        if (getStandGauge() <= 0.0F || source.is(DamageTypes.FELL_OUT_OF_WORLD) || source.is(DamageTypes.GENERIC_KILL)) {
            return super.hurt(source, amount);
        }
        return user.hurt(source, amount);
    }

    /**
     * Gets this StandEntity's instance of a specific move class.
     */
    public @Nullable <T extends AbstractMove<T, ?>> T getMove(Class<T> clazz) {
        for (var move : getMoveMap().asMovesList()) {
            if (move.getClass().isAssignableFrom(clazz)) {
                return (T)move;
            }
        }

        return null;
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

    @Override
    public void setPerformedThisTick(boolean b) {
        performedThisTick = b;
    }

    @Override
    public boolean performedThisTick() {
        return performedThisTick;
    }

    public abstract @NonNull E getThis();

    // Physical properties
    @Override
    public void push(@NonNull Entity entity) {}

    @Override
    public boolean canCollideWith(@NonNull Entity other) {
        return false;
    }

    @Override
    public boolean addEffect(@NotNull MobEffectInstance effect, @Nullable Entity source) {
        if (level().isClientSide || user == null) {
            return false;
        }
        return user.addEffect(effect, source);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        if (damageSource.getEntity() == this) {
            return true;
        }
        // Non-remote stands redirect damage within the AbstractSimpleAttack targetting filters.
        // Remote stands take normal damage, then redirect it within this classes damage() method.
        return !isRemote() && !damageSource.is(DamageTypes.FELL_OUT_OF_WORLD) && !damageSource.is(DamageTypes.GENERIC_KILL);
    }

    /**
     * Handles out-of-combat AI for Stand user mobs.
     * @return Mob using this Stand.
     */
    public @Nullable Mob standUserPassiveAI() {
        // Guaranteed cast due to being called in JEnemies, which only handles MobEntities
        final Mob user = (Mob) getUser();
        if (user == null) {
            JCraft.LOGGER.error("standUserPassiveAI() called with no Stand user for {}", this);
        } else {
            // Block fall damage
            this.wantToBlock = user.fallDistance >= 3;
            // Occasionally dash to destination
            if (user.getNavigation().isInProgress() && random.nextFloat() < 0.01f) DashData.tryDash(1, 0, user);
        }
        return user;
    }
    private static final double sideswitchDistance = 1.25;
    /**
     * Handles movement, stand control, system mechanic control for Stand User mobs while they have a target.
     * General-purpose, and should be specialized to allow the AIs better control of their stands.
     */
    public static void standUserCombatAI(Mob mob, LivingEntity target, StandEntity<?, ?> stand) {
        if (mob == target || !JUtils.canDamage(JDamageSources.stand(stand), target)) {
            return;
        }

        final JumpControl mobJumpControl = mob.getJumpControl();
        final MoveControl mobMoveControl = mob.getMoveControl();

        mob.lookAt(target, 30, 30); // Point body at enemy
        mob.getLookControl().setLookAt(target); // Usually detrimental not to

        final JSpec<?, ?> enemySpec;
        StandEntity<?, ?> enemyStand = JUtils.getStand(target);
        if (enemyStand == stand) // Stands that attack their users would tweak tf out otherwise
        {
            enemyStand = null;
        }
        AbstractMove<?, ?> enemyAttack = null;
        final boolean enemyHasStand = enemyStand != null;

        double distance = target.distanceTo(mob);
        int enemyMoveStun = 0;
        int blockPlusTicks = 0;

        // Get enemy stand attack (most common)
        if (enemyHasStand) {
            enemyMoveStun = enemyStand.getMoveStun();
            enemyAttack = enemyStand.getCurrentMove();

            if (enemyStand.blocking) {
                blockPlusTicks = enemyMoveStun;
            }

            distance = enemyStand.distanceTo(mob);
        }

        // If none was found, try to find a spec attack
        if (enemyAttack == null) {
            if (target instanceof Player player) {
                enemySpec = JComponentPlatformUtils.getSpecData(player).getSpec();

                if (enemySpec != null) {
                    enemyMoveStun = enemySpec.moveStun;
                    enemyAttack = enemySpec.curMove;
                }
            }
        }

        boolean wantToBlock = stand.doAutoBlocking(mob, enemyAttack, enemyHasStand, distance, enemyMoveStun);
        stand.wantToBlock = wantToBlock;

        if (wantToBlock) {
            if (!stand.blocking) {
                if (stand.canAttack() && !DashData.isDashing(mob)) {
                    stand.tryBlock();
                }
            } else if (stand.getMoveStun() > 1) { // Being made to block by an enemy
                if (stand.random.nextDouble() > 0.96) {
                    JCraft.tryPushBlock((ServerLevel) stand.level(), mob, stand);
                }
            }
        } else {
            stand.blocking = false;
        }

        final MobEffectInstance mobStun = mob.getEffect(JStatusRegistry.DAZED.get());
        // If stunned, and about to get hit by another move, Combo Break occasionally
        if (mobStun != null) {
            if (!stand.blocking && enemyAttack != null && enemyMoveStun > enemyAttack.getWindup() && stand.random.nextFloat() < 0.1f) {
                comboBreak((ServerLevel) stand.level(), mob, mobStun);
            }
        }

        // Movement towards/away from target
        PathNavigation entityNavigation = mob.getNavigation();
        boolean evade = stand.doEvasion(entityNavigation, distance, enemyStand, enemyAttack);

        if (!stand.blocking) {
            final MobEffectInstance stun = target.getEffect(JStatusRegistry.DAZED.get());
            // Overestimating stun up to 1/4 of a second for longer combos and frametraps
            int stunTicks = stun != null ? stun.getDuration() + stand.random.nextInt(5) : 0;
            stunTicks += blockPlusTicks;
            if (JComponentPlatformUtils.getTimeStopData(target).isPresent()) {
                stunTicks += JComponentPlatformUtils.getTimeStopData(target).get().getTicks();
            }

            Pair<AbstractMove<?, ?>, Boolean> attackData = null;
            // Only select or buffer attacks when necessary
            if (stand.getMoveStun() <= 1) {
                attackData = (Pair<AbstractMove<?, ?>, Boolean>) stand.doMoveSelection(mob, target, mobJumpControl, enemyStand, enemyAttack, distance, enemyMoveStun, stunTicks);
            }

            stand.doMovement(mob, mobJumpControl, mobMoveControl, enemyStand, enemyHasStand, distance, entityNavigation, evade, stunTicks, attackData);
        } else if (stand.getMoveStun() > 4) { // blocking & movestun > 4 likely means the enemy made you block
            // Don't buffer any attacks as you are minus and will DIE
            stand.queuedMove = null;
        }
    }

    /**
     * Handles forward/backward movement of an AI Stand User.
     * @return Whether the AI Stand User should evade
     */
    protected boolean doEvasion(final PathNavigation entityNavigation, final double distance,
                                @Nullable final StandEntity<?,?> enemyStand, @Nullable final AbstractMove<?,?> enemyAttack) {
        boolean evade = enemyAttack != null;
        if ( // in range (to get hit), or the enemy attack is unblockable
                enemyAttack instanceof AbstractSimpleAttack<?, ?> simpleEnemyAttack && (
                        (!enemyAttack.isRanged() && distance < enemyAttack.getMoveDistance() + simpleEnemyAttack.getHitboxSize() * 1.5) ||
                                simpleEnemyAttack.getBlockableType().isNonBlockableEffects()
                )
        ) {
            entityNavigation.setSpeedModifier(-0.25);
        } else {
            entityNavigation.setSpeedModifier(1.0);
        }
        return evade;
    }

    /**
     * Handles strafing and dashing of an AI Stand User.
     */
    protected void doMovement(Mob mob, JumpControl mobJumpControl, MoveControl mobMoveControl, StandEntity<?, ?> enemyStand, boolean enemyHasStand,
                              double distance, PathNavigation entityNavigation, boolean evade, int stunTicks, @Nullable Pair<AbstractMove<?, ?>, Boolean> attackData) {
        if (attackData != null) {
            AbstractMove<?, ?> selectedAttack = attackData.first();
            if ( // in range (to attack)
                    (selectedAttack instanceof AbstractSimpleAttack<?, ?> simpleAttack &&
                            distance < selectedAttack.getMoveDistance() + simpleAttack.getHitboxSize() * 0.75)
            ) {
                entityNavigation.setSpeedModifier(0.25);
            }
        }

        // Dash to target
        BlockPos targetPos = entityNavigation.getTargetPos();
        if (targetPos != null && mob.onGround() && distance > 1.5) {
            DashData.tryDash(evade ? -1 : 1, evade ? this.random.nextInt(2) - 1 : 0, mob);
        }

        // Move away during combo to prevent point-blank misses
        float sStrafe = Mth.sin(this.tickCount * 0.02f) / 3f;
        if (stunTicks > 0) {
            float back = -0.5f;
            if (enemyHasStand && enemyStand.blocking) {
                back = 0f;
            }
            mobMoveControl.strafe(back, sStrafe);
        } else if (distance < sideswitchDistance * 8) { // Outside of combo, strafe or jump over if close
            float fStrafe = 0f;

            // Jump if extremely close to opponent in an attempt to sideswitch
            if (distance < sideswitchDistance) {
                fStrafe = 1;
                mobJumpControl.jump();
            }

            mobMoveControl.strafe(fStrafe, sStrafe);
        }
    }

    /**
     * @return A Pair containing the selected move, and a bool of whether the move is a crouching variant. Null if no selection.
     */
    protected @Nullable Pair<AbstractMove<?, ? super E>, Boolean> doMoveSelection(
            Mob mob, LivingEntity target, JumpControl mobJumpControl, StandEntity<?, ?> enemyStand,
            AbstractMove<?, ?> enemyAttack, double distance, int enemyMoveStun, int stunTicks) {
        // Ensures the cooldowns are read/written to the correct entity.
        Pair<AbstractMove<?, ? super E>, Boolean> selectedAttackData;
        if (mob instanceof StandEntity<?, ?> standEntity && standEntity.hasUser()) {
            selectedAttackData = this.selectAttack(
                    JComponentPlatformUtils.getCooldowns(standEntity.getUser()),
                    mob, target, stunTicks, enemyMoveStun, distance, enemyStand, enemyAttack);
        } else {
            selectedAttackData = this.selectAttack(
                    JComponentPlatformUtils.getCooldowns(mob),
                    mob, target, stunTicks, enemyMoveStun, distance, enemyStand, enemyAttack);
        }

        if (selectedAttackData == null) return selectedAttackData;
        AbstractMove<?, ?> selectedAttack = selectedAttackData.first();

        if (selectedAttack == null) return selectedAttackData;

        boolean shouldPerformMove = this.getMoveStun() < 1;

        if (this.getCurrentMove() != null && this.getCurrentMove().getFollowup() != null) {
            shouldPerformMove = true;
        }

        mob.setShiftKeyDown(selectedAttackData.second());
        if (selectedAttack.isAerialVariant()) {
            mobJumpControl.jump();
            mob.setOnGround(false);
        }

        if (shouldPerformMove) {
            //JCraft.LOGGER.info("Stand User AI: Performing attack " + selectedAttack);
            if (selectedAttack.getMoveClass() == null) {
                JCraft.LOGGER.error("Attempting to use move with unset MoveClass: {}, stand: {}",
                        selectedAttack.getName().getString(), this);
            } else {
                this.initMove(selectedAttack.getMoveClass());
            }
        } else {
            this.queueMove(MoveInputType.fromMoveClass(selectedAttack.getMoveClass()));
        }
        return selectedAttackData;
    }

    private boolean doAutoBlocking(Mob mob, AbstractMove<?,?> enemyAttack, boolean enemyHasStand, double distance, int enemyMoveStun) {
        if (tsTime > 0) return false;
        boolean wantToBlock = this.wantToBlock;
        wantToBlock = this.doCombatBlocking(mob, enemyAttack, enemyHasStand, distance, enemyMoveStun, wantToBlock);
        // Block if falling or there are projectiles nearby
        wantToBlock = this.doEnvironmentalBlocking(mob, wantToBlock);
        //JCraft.LOGGER.info("Want to block: " + wantToBlock);
        return wantToBlock;
    }

    protected boolean doEnvironmentalBlocking(Mob mob, boolean wantToBlock) {
        // Finding entities is expensive
        if (this.tickCount % 2 == 0) {
            List<Projectile> nearbyProjectiles = level().getEntitiesOfClass(Projectile.class, mob.getBoundingBox().inflate(3), EntitySelector.ENTITY_STILL_ALIVE);
            boolean anyInAir = false;
            Vec3 pos = this.position();
            for (Projectile projectile : nearbyProjectiles) {
                if (projectile.getOwner() == mob) {
                    continue;
                }
                // Is it moving towards the stand?
                if (projectile.distanceToSqr(pos) < new Vec3(projectile.xo, projectile.yo, projectile.zo).distanceToSqr(pos)) {
                    anyInAir = true;
                    break;
                }
            }

            if (anyInAir) {
                wantToBlock = true;
            }
        }

        if (mob.fallDistance > 3) wantToBlock = true; // Block fall damage

        return wantToBlock;
    }

    protected boolean doCombatBlocking(Mob mob, AbstractMove<?, ?> enemyAttack, boolean enemyHasStand, double distance, int enemyMoveStun, boolean wantToBlock) {
        // Blocking logic
        if (enemyAttack != null && enemyMoveStun > 0) { // Only block if the attack is actually active
            // Block regardless of range if the attack is ranged, or is a barrage
            if (enemyAttack.isRanged() || enemyAttack instanceof AbstractBarrageAttack<?, ?>) {
                wantToBlock = true;
            }
            // Otherwise block if within hitting distance, and the attack doesn't block break/bypass
            if (enemyAttack instanceof AbstractSimpleAttack<?, ?> simpleEnemyAttack) {
                if (simpleEnemyAttack.getBlockableType().isNonBlockableEffects()) {
                    return false;
                }
                if (enemyAttack.getMoveDistance() + simpleEnemyAttack.getHitboxSize() * 0.66 > distance &&
                        simpleEnemyAttack.getDamage() * 2 < this.getStandGauge() && !simpleEnemyAttack.getBlockableType().isNonBlockable()) {
                    wantToBlock = true;
                }
            }
        } else {
            wantToBlock = false;
        }

        if (!enemyHasStand) { // Blocking logic against standless opponents
            CommonCooldownsComponent cooldowns = JComponentPlatformUtils.getCooldowns(mob);
            if (cooldowns.getCooldown(CooldownType.DASH) > 0) // Careful approach
            {
                wantToBlock = distance > 2 && distance < 5; // Block at range <2, 5> (outside stand attack range, but in player/ravager attack range)
            } else {
                wantToBlock = false;
            }
        }
        return wantToBlock;
    }

    public void queueMove(MoveInputType type) {
        if (user == null) {
            return;
        }

        MoveClass moveClass = type.getMoveClass(standby);
        if (moveClass != null) {
            for (MoveMap.Entry<E, S> entry : moveMap.getEntries(moveClass)) {
                if (!entry.getMove().canBeQueued(getThis())) {
                    return;
                }
            }
        }

        // This check helps users intuitively use light and its followup without mis-inputting
        // Such a check should be applied to any quick move with a followup
        //noinspection ConstantValue // no it's not
        if (type != MoveInputType.LIGHT || JComponentPlatformUtils.getCooldowns(user).getCooldown(CooldownType.STAND_LIGHT) <= 0) {
            queuedMove = type;
        }
    }

    public Vector3f getAuraColor() {
        return auraColors[getSkin()];
    }

    // Animation code

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(getThis(), "controller", 0, this::predicate));
    }

    private PlayState predicate(AnimationState<E> state) {
        AnimationController<E> controller = state.getController();

        String summonAnimation = getSummonAnimation();
        if (playSummonAnim && summonAnimation != null) {
            return state.setAndContinue(RawAnimation.begin().thenPlay(summonAnimation));
        }

        if (isSameState()) {
            controller.forceAnimationReset();
        }

        S superState = getState();
        superState.playAnimation(getThis(), state);
        superState.configureController(getThis(), controller);

        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public boolean isSilent() {
        // Make stands silent if their users are.
        return super.isSilent() || (user != null && user.isSilent());
    }

    @Override
    public boolean reflectsDamage() {
        return false;
    }

    /**
     * Redirects damage from the stand to its user.
     *
     * @return whether the damage logic should proceed in harming the stand itself
     */
    @Override
    public boolean handleDamage(Vec3 kbVec, int stunTicks, int stunLevel, boolean overrideStun, float damage, boolean lift,
                                int blockstun, DamageSource source, Entity attacker, CommonHitPropertyComponent.HitAnimation hitAnimation,
                                MoveUsage moveUsage, boolean canBackstab, boolean unblockable) {
        if (!hasUser()) return false;
        boolean hit = true;

        // Remote stands can only block for themselves
        if (isRemote()) {
            if (blocking) {
                boolean backstabbed = false;
                if (attacker != null) {
                    double delta = Math.abs((yHeadRot + 90.0f) % 360.0f - (attacker.getYHeadRot() + 90.0f) % 360.0f);
                    if (canBackstab && (360.0 - delta % 360.0 < 45 || delta % 360.0 < 45) && distanceToSqr(attacker.position()) >= 1.5625) { // Backstab logic
                        JCraft.createParticle((ServerLevel) attacker.level(), getX(), attacker.getEyeY(), getZ(), JParticleType.BACK_STAB);
                        playSound(JSoundRegistry.BACKSTAB.get(), 1, 1);
                        blocking = false;
                        overrideStun = true;
                        backstabbed = true;
                    }
                }

                if (!backstabbed && !unblockable) { // Didn't backstab, not unblockable
                    setMoveStun(blockstun);
                    setStandGauge(getStandGauge() - 2 * damage);
                    playSound(JSoundRegistry.STAND_BLOCK.get(), 1, 1);
                    hit = false;
                    overrideStun = false;
                } else {
                    blocking = false;
                }

                if (!backstabbed) {
                    hit = false;
                }
            } else {
                setStandGauge(getStandGauge() - damage * 2);
            }
        }

        if (hit) {
            damageLogic(
                    level(),
                    user,
                    new AttackData(kbVec, stunTicks, stunLevel, overrideStun, damage, lift, blockstun, source, attacker, hitAnimation, moveUsage, canBackstab, unblockable)
            );
        }
        return false;
    }

    /**
     * Needed because the super constructor invokes some things that need this.
     * Meaning we can't use a constructor parameter.
     *
     * @return literally just {@code State.values()}
     */
    protected abstract S[] getStateValues();

    public S boxState(int rawState) {
        return getStateValues()[rawState];
    }

    public S getIdleState() {
        return boxState(0);
    }

    public abstract S getBlockState();

    public boolean isIdle() {
        return getRawState() == 0;
    }

    /**
     * Should be used on the CLIENT only, due to the sub-tick delay between setting {@link StandEntity#blocking} and {@link StandEntity#STATE}
     *
     * @return whether the stands state indicates it is blocking.
     */
    public boolean isBlocking() {
        return getState() == getBlockState();
    }

    @Nullable
    protected abstract String getSummonAnimation();

    /**
     * Gets called after damage calculation if the damaged entity was slain.
     */
    public void freshKill(@Nullable LivingEntity entity) {
    }
}
