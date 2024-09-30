package net.arna.jcraft.common.entity.stand;

import com.google.common.base.MoreObjects;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.attack.MobilityType;
import net.arna.jcraft.common.attack.core.IAttacker;
import net.arna.jcraft.common.attack.core.MoveInputType;
import net.arna.jcraft.common.attack.core.MoveMap;
import net.arna.jcraft.common.attack.core.MoveType;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractBarrageAttack;
import net.arna.jcraft.common.attack.moves.base.AbstractCounterAttack;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.attack.moves.base.AbstractSimpleAttack;
import net.arna.jcraft.common.attack.moves.shared.MainBarrageAttack;
import net.arna.jcraft.common.component.living.CommonCooldownsComponent;
import net.arna.jcraft.common.component.living.CommonHitPropertyComponent;
import net.arna.jcraft.common.config.JServerConfig;
import net.arna.jcraft.common.entity.damage.JDamageSources;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.network.c2s.PlayerInputPacket;
import net.arna.jcraft.common.network.s2c.ComboCounterPacket;
import net.arna.jcraft.common.spec.JSpec;
import net.arna.jcraft.common.tickable.MoveTickQueue;
import net.arna.jcraft.common.util.*;
import net.arna.jcraft.mixin.LivingEntityInvoker;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.arna.jcraft.registry.JPacketRegistry;
import net.arna.jcraft.registry.JSoundRegistry;
import net.arna.jcraft.registry.JStatusRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.JumpControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import mod.azure.azurelib.animatable.GeoEntity;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.core.animation.RawAnimation;
import mod.azure.azurelib.core.object.PlayState;
import mod.azure.azurelib.util.AzureLibUtil;

import java.util.List;

import static net.arna.jcraft.JCraft.comboBreak;
import static net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;

public abstract class StandEntity<E extends StandEntity<E, S>, S extends Enum<S> & StandAnimationState<E>>
        extends Mob implements GeoEntity, IAttacker<E, S>, ICustomDamageHandler {

    // TODO: finish custom player idle poses for all stands

    @SuppressWarnings("NotNullFieldNotInitialized") // It does get initialized by a method called in the constructor.
    @Getter
    private @NonNull MoveMap<E, S> moveMap;
    @Getter
    protected final MoveContext moveContext = new MoveContext();

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

    // In meters and degrees
    protected float idleDistance = 1.25f;
    protected float idleRotation = -45f;
    public final float attackRotation = 90f;
    protected float blockDistance = 0.75f;

    protected float maxStandGauge = 90f;

    protected MoveInputType queuedMove;
    private MoveInputType holdingType;
    private AbstractMove<?, ? super E> curMove;
    public AbstractMove<?, ? super E> prevMove;
    public int armorPoints;

    // Info
    @Getter
    protected int proCount;
    @Getter
    protected int conCount;
    public String freespace;

    // Player Movement Input
    public int lastRemoteInputTime;
    public Vec3 remoteSpeed = Vec3.ZERO;
    @Getter
    private double remoteForwardInput = 0;
    @Getter
    private double remoteSideInput = 0;
    @Setter
    private boolean remoteJumpInput = false, remoteSneakInput = false;

    // Summoning
    @Getter
    @Nullable
    private final SoundEvent summonSound;
    private final boolean playGenericSummonSound;
    protected int summonAnimDuration = 19;
    private boolean playSummonAnim = true;
    @Setter
    private boolean playSummonSound = true;
    @Setter
    private boolean playDesummonSound = true;

    // Data
    @Getter
    private final StandType standType;
    private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);
    protected Vector3f[] auraColors = {new Vector3f(), new Vector3f(1f, 0f, 0f), new Vector3f(0f, 1f, 0f), new Vector3f(0f, 0f, 1f)};

    protected StandEntity(StandType type, Level world) {
        this(type, world, null, true);
    }

    protected StandEntity(StandType type, Level world, @Nullable SoundEvent summonSound) {
        this(type, world, summonSound, false);
    }

    protected StandEntity(StandType type, Level world, @Nullable SoundEvent summonSound, boolean playGenericSummonSound) {
        super(type.getEntityType(), world);
        noPhysics = true;
        standType = type;
        this.noCulling = true;
        this.summonSound = summonSound;
        this.playGenericSummonSound = playGenericSummonSound;

        assert getThis() == this;

        registerMoves();
    }

    // State controls
    static {
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
    }

    public boolean allowMoveHandling() {
        return true;
    }

    @NotNull
    public LivingEntity getUserOrThrow() {
        if (user == null) {
            throw new NullPointerException("No user set");
        }
        return user;
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
        return entityData.get(SKIN);
    }

    public void setSkin(int skin) {
        if (skin < 0 || skin >= getStandType().getSkinCount()) {
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
     * Called in the constructor of this class. Registers all moves by calling {@link #registerMoves(MoveMap)}.
     * Call this if you wish to re-register the moves for some reason. Doing so will reset the {@link MoveMap}.
     */
    protected final void registerMoves() {
        registerMoves(moveMap = new MoveMap<>());
        moveMap.freeze();
        moveMap.forEach(entry -> entry.getMove().registerContextEntries(moveContext));
    }

    protected abstract void registerMoves(MoveMap<E, S> moves);

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
        registerMoves(); // Switching movesets
    }

    /**
     * Puts the stand into remote mode.
     * USE {@link #setRemote(boolean)} FOR PRACTICAL APPLICATION
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
        startRiding(user);
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
     * Gets the stands position while detached
     */
    public Vector3f getFreePos() {
        return new Vector3f(this.entityData.get(FREEX), this.entityData.get(FREEY), this.entityData.get(FREEZ));
    }

    /**
     * Sets the stands position while detached
     *
     * @param freePos new position
     */
    public void setFreePos(Vector3f freePos) {
        this.entityData.set(FREEX, freePos.x());
        this.entityData.set(FREEY, freePos.y());
        this.entityData.set(FREEZ, freePos.z());
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
    public boolean tryFollowUp(MoveType in, MoveType followupType) {
        if (in == followupType && curMove != null && curMove.getMoveType() == followupType && getMoveStun() < curMove.getWindupPoint()) {
            AbstractMove<?, ? super E> followup = curMove.getFollowup();
            if (followup != null) {
                setMove(followup, (S) followup.getAnimation());
                return true;
            }
        }
        return false;
    }

    @Override
    public void setCurrentMove(AbstractMove<?, ? super E> move) {
        prevMove = curMove;
        curMove = move;
    }

    @Override
    public Vec3 getLookAngle() {
        // Ignore pitch in rotation vectors.
        return calculateViewVector(0, getYRot());
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.PLAYERS;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
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
    }

    // Attack controls

    /**
     * @return whether the stand should be able to attack
     */
    public boolean canAttack() {
        if (isRemote() && hasEffect(JStatusRegistry.DAZED.get())) {
            return false;
        }
        return hasUser() && getMoveStun() <= 0 && !JUtils.isAffectedByTimeStop(user) && !getUserOrThrow().hasEffect(JStatusRegistry.DAZED.get());
    }

    /**
     * As a general rule, low-hitbox moves should modify this to false, since otherwise players may move the hitbox into the ground.
     * This should also usually use client-visible values to prevent desync.
     * @return whether the stand should change its height depending on the user's look pitch.
     */
    public boolean shouldOffsetHeight() {
        return getState().ordinal() > 0;
    }

    public boolean handleMove(MoveType type) {
        MoveMap.Entry<E, S> entry = getMoveMap().getFirstValidEntry(type, getThis());
        if (entry == null) {
            return false;
        }

        if (hasUser() && !getUserOrThrow().onGround() && entry.getAerialVariant() != null) {
            entry = entry.getAerialVariant();
        }
        // This means crouching aerial variants are also supported. :O
        if (hasUser() && getUserOrThrow().isShiftKeyDown() && entry.getCrouchingVariant() != null) {
            entry = entry.getCrouchingVariant();
        }
        // Ensure a crouching variant of an aerial variant and an aerial variant of a crouching variant both work.
        if (hasUser() && !getUserOrThrow().onGround() && entry.getAerialVariant() != null) {
            entry = entry.getAerialVariant();
        }

        AbstractMove<?, ? super E> move = entry.getMove();
        return handleMove(move.shouldCopyOnUse() ? move.copy() : move, entry.getCooldownType(), entry.getAnimState());
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

            cooldowns.setCooldown(cooldownType, move.getCooldown());
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
        move = moveMap.getRegisteredMoveFor(move);
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

    /**
     * Basic damage method, you likely want to use baseDamageLogic or damageLogic instead
     *
     * @param damage       damage in half hearts
     * @param damageSource source of damage
     * @param ent          entity to harm
     */
    public static void damage(float damage, DamageSource damageSource, LivingEntity ent) {
        if (!JUtils.canDamage(damageSource, ent)) {
            return;
        }

        float scaling = ((IDamageScaler) ent).jcraft$getDamageScaling();
        //JCraft.LOGGER.info("Damaging entity: " + ent + " with damage: " + damage + " and scaling: " + scaling);
        damage *= scaling;

        // All stands ignore 10% of armor & armor toughness
        damage = CombatRules.getDamageAfterAbsorb(damage, (float) ent.getArmorValue() * 0.9f, (float) ent.getAttributeValue(Attributes.ARMOR_TOUGHNESS) * 0.9f);
        damage = ((LivingEntityInvoker) ent).invokeModifyAppliedDamage(damageSource, damage);

        // Apply absorption
        applyAbsorptionAndStats(damage, damageSource, ent);
    }

    private static void applyAbsorptionAndStats(float damage, DamageSource damageSource, LivingEntity ent) {
        float f = damage;
        damage = Math.max(damage - ent.getAbsorptionAmount(), 0.0F);
        ent.setAbsorptionAmount(ent.getAbsorptionAmount() - (f - damage));

        if (damage <= 0) {
            return;
        }

        float h = ent.getHealth();

        LivingEntityInvoker invoker = (LivingEntityInvoker) ent;

        // Statistics
        Level world = ent.level();
        if (ent instanceof Player) {
            if (world instanceof ServerLevel serverWorld) {
                var packet = NetworkManager.toPacket(NetworkManager.Side.S2C, JPacketRegistry.S2C_STAND_HURT, new FriendlyByteBuf(Unpooled.buffer()).writeVarInt(ent.getId()));
                serverWorld.getChunkSource().broadcastAndSend(ent, packet);
            }

        } else {
            world.broadcastEntityEvent(ent, (byte) 2);
        }

        ent.level().broadcastDamageEvent(ent, damageSource);
        invoker.callPlayHurtSound(damageSource);
        invoker.setLastDamageTaken(damage);
        invoker.setLastDamageSource(damageSource);
        invoker.setLastDamageTime(world.getGameTime());

        ent.invulnerableTime = 20;
        ent.hurtDuration = ent.hurtTime = 10;

        ent.setHealth(h - damage);
        ent.getCombatTracker().recordDamage(damageSource, damage);
        ent.gameEvent(GameEvent.ENTITY_DAMAGE);
        if (damageSource.getEntity() instanceof LivingEntity livingAttacker) {
            ent.setLastHurtByMob(livingAttacker);
        }
        if (ent.isDeadOrDying()) {
            ent.die(damageSource);
        }
    }

    /**
     * Basic damage method, ignores potion effects and enchantments, accounts for armor and damage scaling
     *
     * @param damage       damage in half hearts
     * @param damageSource source of damage
     * @param ent          entity to harm
     */
    public static void trueDamage(float damage, DamageSource damageSource, LivingEntity ent) {
        if (ent == null || ent.isRemoved() || ent.isDeadOrDying()) {
            return;
        }

        float scaling = ((IDamageScaler) ent).jcraft$getDamageScaling();
        //JCraft.LOGGER.info("True damaging entity: " + ent + " with damage: " + damage + " and scaling: " + scaling);
        damage *= scaling;

        // All stands ignore 10% of armor & armor toughness
        damage = CombatRules.getDamageAfterAbsorb(damage, (float) ent.getArmorValue() * 0.9f, (float) ent.getAttributeValue(Attributes.ARMOR_TOUGHNESS) * 0.9f);

        // Apply absorption
        applyAbsorptionAndStats(damage, damageSource, ent);
    }

    // Stock attacks to define

    /**
     * Initiates a move of the specified type.
     *
     * @param type The type of move to initiate.
     * @return Whether the move was initiated.
     */
    public boolean initMove(MoveType type) {
        return handleMove(type);
    }

    public boolean canHoldMove(@Nullable MoveInputType type) {
        if (type == null || type.getMoveType() == null) {
            return false;
        }

        MoveMap.Entry<E, S> entry = moveMap.getFirstValidEntry(type.getMoveType(), getThis());
        return entry == null ? type.isHoldable() : MoreObjects.firstNonNull(entry.getMove().getIsHoldable(), type.isHoldable());
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

    /**
     * Cancels the stand's move instantly
     */
    public void cancelMove() {
        if (getCurrentMove() != null) {
            getCurrentMove().onCancel(getThis());
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

        if (tickCount == 1) {
            playSummonSound();
        }
        final boolean client = level().isClientSide;
        prevAlpha = getAlphaOverride();

        int moveStun = getMoveStun();
        if (moveStun > 0 && !(blocking && wantToBlock && moveStun == 1)) {
            setMoveStun(--moveStun); // Counting down animation time or similar
        }
        if (playSummonAnim && (moveStun > 0 || tickCount > summonAnimDuration || getState() == getBlockState())) {
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
            if (user.isAlive()) {
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
            startRiding(user, true);
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
        } else {
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
                MoveInputType curMoveInputType = MoveInputType.fromMoveType(move.getMoveType());
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

            if (wantToBlock && !blocking && (user == null || !DashData.isDashing(user)) && canAttack()) {
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
                    setCurrentMove(null);

                    setStandGauge(Mth.clamp(this.getStandGauge() + 0.5f, 0, maxStandGauge));

                    if (getRawState() != 0 || isReset()) {
                        setRawState(0);
                        setReset(false);

                        setDistanceOffset(idleDistance);
                        setRotationOffset(idleRotation);
                    }
                } else {
                    idleOverride();
                }
            } else if (blocking) { // Process block
                if (wantToBlock) {
                    setCurrentMove(null);

                    if (moveStun < 1) {
                        setMoveStun(1);
                    }

                    setDistanceOffset(blockDistance);
                    setRotationOffset(attackRotation);
                    standBlock();
                    setStateNoReset(getBlockState()); // Set after standBlock() so blocking logic can account for previous state
                } else {
                    tryUnblock();
                }
            }

            tsTime--;
        }

        // JCraft.LOGGER.info( "State: " + this.getState() + " Movestun: " + curMoveStun + " Currently attacking: " + (this.curAttack != null)); // Massive debug log

        if (getCurrentMove() != prevMove && getCurrentMove() != null)
        //JCraft.LOGGER.info("Logged previous attack change: " + this.curAttack + " " + this.previousAttack);
        {
            prevMove = getCurrentMove();
        }
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
            initMove(queuedMove.getMoveType());
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
    public void setAttackRotationOffset() {
        setRotationOffset(attackRotation);
    }

    /**
     * Highest level damage method, handles combo counting, DEFAULTS unblockable TO FALSE
     *
     * @param world        world to process damage in
     * @param ent          victim
     * @param kbVec        knockback vector to apply
     * @param stunTicks    stun duration in ticks
     * @param overrideStun will the attack override all other types of stun?
     * @param damage       damage in half hearts
     * @param lift         will the attack lift the victim upon an aerial hit?
     */
    public static void damageLogic(Level world, LivingEntity ent, Vec3 kbVec, int stunTicks, int stunLevel,
                                   boolean overrideStun, float damage, boolean lift, int blockstun, DamageSource source,
                                   @Nullable Entity attacker, CommonHitPropertyComponent.HitAnimation hitAnimation, boolean canBackstab, boolean unblockable) {
        if (world == null || world.isClientSide || ent == null || !ent.canBeSeenAsEnemy()) {
            return;
        }
        if (world.getGameRules().getBoolean(JCraft.COMBO_COUNTER) && attacker instanceof ServerPlayer playerEntity) {
            comboCounterLogic(playerEntity, ent);
        }

        baseDamageLogic(ent, kbVec, stunTicks, stunLevel, overrideStun, damage, lift, blockstun, source, attacker, hitAnimation, canBackstab, unblockable);
    }

    /**
     * Highest level damage method, handles combo counting, DEFAULTS unblockable TO FALSE
     *
     * @param world        world to process damage in
     * @param ent          victim
     * @param kbVec        knockback vector to apply
     * @param stunTicks    stun duration in ticks
     * @param overrideStun will the attack override all other types of stun?
     * @param damage       damage in half hearts
     * @param lift         will the attack lift the victim upon an aerial hit?
     */
    public static void damageLogic(Level world, LivingEntity ent, Vec3 kbVec, int stunTicks, int stunLevel,
                                   boolean overrideStun, float damage, boolean lift, int blockstun, DamageSource source,
                                   @Nullable Entity attacker, CommonHitPropertyComponent.HitAnimation hitAnimation, boolean canBackstab) {
        if (world == null || world.isClientSide || ent == null || !ent.canBeSeenAsEnemy()) {
            return;
        }
        if (world.getGameRules().getBoolean(JCraft.COMBO_COUNTER) && attacker instanceof ServerPlayer playerEntity) {
            comboCounterLogic(playerEntity, ent);
        }

        baseDamageLogic(ent, kbVec, stunTicks, stunLevel, overrideStun, damage, lift, blockstun, source, attacker, hitAnimation, canBackstab, false);
    }

    /**
     * Highest level damage method, handles combo counting, DEFAULTS canBackstab and unblockable TO FALSE
     *
     * @param world        world to process damage in
     * @param ent          victim
     * @param kbVec        knockback vector to apply
     * @param stunTicks    stun duration in ticks
     * @param overrideStun will the attack override all other types of stun?
     * @param damage       damage in half hearts
     * @param lift         will the attack lift the victim upon an aerial hit?
     */
    public static void damageLogic(Level world, LivingEntity ent, Vec3 kbVec, int stunTicks, int stunLevel,
                                   boolean overrideStun, float damage, boolean lift, int blockstun, DamageSource source,
                                   @Nullable Entity attacker, CommonHitPropertyComponent.HitAnimation hitAnimation) {
        if (world == null || world.isClientSide || ent == null || !ent.canBeSeenAsEnemy()) {
            return;
        }
        if (world.getGameRules().getBoolean(JCraft.COMBO_COUNTER) && attacker instanceof ServerPlayer playerEntity) {
            comboCounterLogic(playerEntity, ent);
        }
        baseDamageLogic(ent, kbVec, stunTicks, stunLevel, overrideStun, damage, lift, blockstun, source, attacker, hitAnimation, false, false);
    }

    /**
     * Handles combo counting for specific player
     *
     * @param playerEntity attacker
     */
    private static void comboCounterLogic(ServerPlayer playerEntity, LivingEntity victim) {
        if (victim instanceof IOwnable ownable && ownable.getMaster() == playerEntity) {
            return;
        }
        if (victim != null && !JServerConfig.ENABLE_FRIENDLY_FIRE.getValue() && victim.isAlliedTo(playerEntity)) {
            return;
        }

        IComboCounter comboCounter = (IComboCounter) playerEntity;

        if (comboCounter.jcraft$getLastAttacked() != victim) {
            comboCounter.jcraft$setComboCount(1);
        } else {
            MobEffectInstance stun = comboCounter.jcraft$getLastAttacked().getEffect(JStatusRegistry.DAZED.get());
            if (stun != null && stun.getAmplifier() != 2) {
                comboCounter.jcraft$incrementComboCount();
            } else {
                comboCounter.jcraft$setComboCount(1);
            }

            ComboCounterPacket.send(playerEntity, comboCounter.jcraft$getComboCount(), ((IDamageScaler) victim).jcraft$getDamageScaling());
        }

        comboCounter.jcraft$setLastAttacked(victim);
    }

    /**
     * Mid-level damage method, handles blocking, lifting, counters, velocity modification
     *
     * @param ent          victim
     * @param kbVec        knockback vector to apply
     * @param stunTicks    stun duration in ticks
     * @param overrideStun will the attack override all other types of stun?
     * @param damage       damage in half hearts
     * @param lift         will the attack lift the victim upon an aerial hit?
     * @param hitAnimation animation the opponent will do when they are hit
     */
    private static void baseDamageLogic(LivingEntity ent, Vec3 kbVec, int stunTicks, int stunLevel, boolean overrideStun,
                                        float damage, boolean lift, int blockstun, DamageSource source, @Nullable Entity attacker,
                                        CommonHitPropertyComponent.HitAnimation hitAnimation, boolean canBackstab, boolean unblockable) {
        if (ent instanceof ICustomDamageHandler customDamageHandler) {
            if (!customDamageHandler.handleDamage(kbVec, stunTicks, stunLevel, overrideStun, damage, lift, blockstun, source, attacker, hitAnimation, canBackstab, unblockable)) {
                return;
            }
        }

        if (ent != null && !JServerConfig.ENABLE_FRIENDLY_FIRE.getValue() && attacker != null && ent.isAlliedTo(attacker)) {
            return;
        }

        boolean hit = true;
        boolean tsHit = JUtils.isAffectedByTimeStop(ent);

        StandEntity<?, ?> stand = JUtils.getStand(ent);
        if (stand != null) {
            AbstractMove<?, ?> standAttack = stand.getCurrentMove();
            if (standAttack != null) {
                // Counter check
                if (!tsHit && standAttack.isCounter() && stand.getMoveStun() < standAttack.getWindupPoint()) {
                    //noinspection unchecked
                    ((AbstractCounterAttack<?, StandEntity<?, ?>>) standAttack).counter(stand, attacker, source);
                    ent.removeEffect(JStatusRegistry.DAZED.get());
                    return;
                }

                if (--stand.armorPoints < 0) {
                    stand.cancelMove();
                } else {
                    JComponentPlatformUtils.getMiscData(ent).displayArmoredHit();
                }
            }

            if (stand.blocking && !stand.isRemote()) {
                boolean backstabbed = false;
                if (attacker != null) {
                    double delta = Math.abs((ent.yHeadRot + 90.0f) % 360.0f - (attacker.getYHeadRot() + 90.0f) % 360.0f);
                    if (canBackstab && (360.0 - delta % 360.0 < 45 || delta % 360.0 < 45) && ent.distanceToSqr(attacker.position()) >= 1.5625) { // Backstab logic
                        JCraft.createParticle((ServerLevel) attacker.level(), ent.getX(), attacker.getEyeY(), ent.getZ(), JParticleType.BACK_STAB);
                        stand.playSound(JSoundRegistry.BACKSTAB.get(), 1, 1);
                        stand.blocking = false;
                        overrideStun = true;
                        backstabbed = true;
                    }
                }

                if (!backstabbed && !unblockable) { // Didn't backstab, not unblockable
                    //JCraft.LOGGER.info("Enemy blocked attack, setting blockstun to: " + blockstun);
                    stand.setMoveStun(blockstun);
                    stand.setStandGauge(stand.getStandGauge() - 2 * damage);
                    stand.playSound(JSoundRegistry.STAND_BLOCK.get(), 1, 1);
                    hit = false;
                    overrideStun = false;
                } else {
                    stand.blocking = false;
                }
            }
        }

        if (tsHit) {
            stunLevel = 3;
            if (stunTicks > 20) {
                stunTicks = 20;
            }
            lift = false;
        }

        // Stun application & overriding
        IDamageScaler damageScaler = (IDamageScaler) ent;

        if (JServerConfig.ENABLE_IPS.getValue()) {
            float scaling = damageScaler.jcraft$getDamageScaling();
            stunTicks *= scaling * 0.2 + 0.8;
        }

        if (hit) {
            damageScaler.jcraft$increaseHitCount();

            MobEffectInstance stun = ent.getEffect(JStatusRegistry.DAZED.get());
            if (stun != null) {
                if (overrideStun) {
                    ent.removeEffect(JStatusRegistry.DAZED.get());
                }
            }

            JCraft.stun(ent, stunTicks, stunLevel, attacker);

            if (hitAnimation != null) {
                JComponentPlatformUtils.getHitProperties(ent).setHitAnimation(hitAnimation, stunTicks);
            }

            if (!tsHit) {
                ent.push(kbVec.x, kbVec.y, kbVec.z);
            }
        }

        // Interrupting spec moves
        if (ent instanceof Player playerEntity) {
            JSpec<?, ?> spec = JUtils.getSpec(playerEntity);
            if (spec != null && spec.curMove != null) {
                if (--spec.armorPoints < 0) {
                    spec.cancelMove();
                } else {
                    JComponentPlatformUtils.getMiscData(playerEntity).displayArmoredHit();
                }
            }
        }

        // Aerial hits keep the victim up
        if (lift) {
            Vec3 vel = ent.getDeltaMovement();
            double finalY = vel.y;

            if (!ent.onGround()) {
                finalY = Mth.clamp(vel.y / 2, 0.085, 0.25);
            }

            GravityChangerAPI.setWorldVelocity(ent,
                    new Vec3(
                            Mth.clamp(vel.x, -1, 1),
                            Mth.clamp(finalY, -0.25, 0.25),
                            Mth.clamp(vel.z, -1, 1)
                    ));
        }

        damage(damage, source, ent);
        if ((ent.isDeadOrDying() || ent.getHealth() <= 0f) && attacker instanceof final LivingEntity livingAttacker) {
            final StandEntity<?, ?> standAttacker = JUtils.getStand(livingAttacker);
            if (standAttacker != null) {
                standAttacker.freshKill(ent);
            }
        }

        if (tsHit) {
            JComponentPlatformUtils.getTimeStopData(ent).get().addTotalVelocity(kbVec);
        } else {
            JUtils.syncVelocityUpdate(ent);
        }
    }

    protected boolean shouldNotPlaySummonSound() {
        return user instanceof ArmorStand || !playSummonSound;
    }

    protected void playSummonSound() {
        if (shouldNotPlaySummonSound()) {
            return;
        }

        if (summonSound != null) {
            playSound(summonSound, 1f, 1f);
        }
        if (summonSound == null || playGenericSummonSound) {
            playSound(JSoundRegistry.STAND_SUMMON.get(), 1f, 1f);
        }
    }

    @Override
    public void stopRiding() {
        if (tickCount == 0 && getRemovalReason() == null) {
            // This may be necessary because the packet that sets passengers arrives early on Forge
            JCraft.LOGGER.warn("Prevented stopRiding() call for recently created " + this);
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
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);

        setSkin(nbt.getInt("Skin"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);

        nbt.putInt("Skin", getSkin());
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (user == null ||
                source.getEntity() == user ||
                user.isInvulnerableTo(source) ||
                source.is(DamageTypes.FALLING_BLOCK) ||
                source.is(DamageTypes.DROWN)) {
            return false;
        }

        if (blocking && source.is(DamageTypes.MOB_PROJECTILE)) {
            return false;
        }

        if (source.is(DamageTypes.MAGIC) || source.is(DamageTypes.EXPLOSION)) // AoE effects have damage nerfed
        {
            amount /= 2.0F;
        }

        if (getStandGauge() <= 0.0F || source.is(DamageTypes.FELL_OUT_OF_WORLD) || source.is(DamageTypes.GENERIC_KILL)) {
            return super.hurt(source, amount);
        }
        return user.hurt(source, amount);
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

    public abstract @NonNull E getThis();

    // Physical properties
    @Override
    public void push(Entity entity) {
    }

    @Override
    public boolean canCollideWith(Entity other) {
        return false;
    }

    @Override
    public boolean addEffect(MobEffectInstance effect, @Nullable Entity source) {
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

    @Override
    public boolean isAttackable() {
        return true;
    }

    public void standUserPassiveAI() {
        // Guaranteed cast due to being called in JEnemies, which only handles MobEntities
        Mob user = (Mob) getUser();
        if (user == null) {
            JCraft.LOGGER.error("standUserPassiveAI called with no Stand user for: " + this);
            return;
        }

        boolean wantToBlock = false;
        if (user.fallDistance > 3) wantToBlock = true;
        if (user.getNavigation().isInProgress()) DashData.tryDash(1, 0, user);
        this.wantToBlock = wantToBlock;
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
            if (!stand.blocking && stand.canAttack() && !DashData.isDashing(mob)) {
                stand.tryBlock();
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

            Tuple<AbstractMove<?, ?>, Boolean> attackData = null;
            // Only select or buffer attacks when necessary
            if (stand.getMoveStun() <= 1) {
                attackData = stand.doMoveSelection(mob, target, mobJumpControl, enemyStand, enemyAttack, distance, enemyMoveStun, stunTicks);
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
    protected boolean doEvasion(PathNavigation entityNavigation, double distance, StandEntity<?,?> enemyStand, AbstractMove<?,?> enemyAttack) {
        boolean evade = enemyAttack != null;
        if ( // in range (to get hit)
                (enemyAttack instanceof AbstractSimpleAttack<?, ?> simpleEnemyAttack && !enemyAttack.isRanged() &&
                        distance < enemyAttack.getMoveDistance() + simpleEnemyAttack.getHitboxSize() * 1.5)
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
                              double distance, PathNavigation entityNavigation, boolean evade, int stunTicks, @Nullable Tuple<AbstractMove<?, ?>, Boolean> attackData) {
        if (attackData != null) {
            AbstractMove<?, ?> selectedAttack = attackData.getA();
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

            // Jump if extremely close to opponent in attempt to sideswitch
            if (distance < sideswitchDistance) {
                fStrafe = 1;
                mobJumpControl.jump();
            }

            mobMoveControl.strafe(fStrafe, sStrafe);
        }
    }

    /**
     * @return A Tuple containing the selected move, and a boolean of whather the move is a crouching variant. Null if no selection.
     */
    protected @Nullable Tuple<AbstractMove<?, ?>, Boolean> doMoveSelection(
            Mob mob, LivingEntity target, JumpControl mobJumpControl, StandEntity<?, ?> enemyStand,
            AbstractMove<?, ?> enemyAttack, double distance, int enemyMoveStun, int stunTicks) {
        // Ensures the cooldowns are read/written to the correct entity.
        Tuple<AbstractMove<?, ?>, Boolean> selectedAttackData;
        if (mob instanceof StandEntity<?, ?> standEntity && standEntity.hasUser()) {
            selectedAttackData = this.selectAttack(
                    JComponentPlatformUtils.getCooldowns(standEntity.getUser()),
                    mob, target, stunTicks, enemyMoveStun, distance, enemyStand, enemyAttack);
        } else {
            selectedAttackData = this.selectAttack(
                    JComponentPlatformUtils.getCooldowns(mob),
                    mob, target, stunTicks, enemyMoveStun, distance, enemyStand, enemyAttack);
        }

        if (selectedAttackData != null) {
            AbstractMove<?, ?> selectedAttack = selectedAttackData.getA();

            if (selectedAttack != null) {
                boolean shouldPerformMove = this.getMoveStun() < 1;

                if (this.getCurrentMove() != null && this.getCurrentMove().getFollowup() != null) {
                    shouldPerformMove = true;
                }

                mob.setShiftKeyDown(selectedAttackData.getB());
                if (selectedAttack.isAerialVariant()) {
                    mobJumpControl.jump();
                    mob.setOnGround(false);
                }

                if (shouldPerformMove) {
                    //JCraft.LOGGER.info("Stand User AI: Performing attack " + selectedAttack);
                    if (selectedAttack.getMoveType() == null) {
                        JCraft.LOGGER.error("Attempting to use attack with unset MoveType: " + selectedAttack.getName().getString() + ", stand: " + this);
                    } else {
                        this.initMove(selectedAttack.getMoveType());
                    }
                } else {
                    this.queueMove(MoveInputType.fromMoveType(selectedAttack.getMoveType()));
                }
            }
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
            if (enemyAttack instanceof AbstractSimpleAttack<?, ?> simpleEnemyAttack &&
                    enemyAttack.getMoveDistance() + simpleEnemyAttack.getHitboxSize() * 0.66 > distance &&
                    simpleEnemyAttack.getDamage() * 2 < this.getStandGauge() && !simpleEnemyAttack.getBlockableType().isNonBlockable()) {
                wantToBlock = true;
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
        // This check helps users intuitively use light and its followup without mis-inputting
        // Such a check should be applied to any quick move with a followup
        if (type != MoveInputType.LIGHT || JComponentPlatformUtils.getCooldowns(user).getCooldown(CooldownType.STAND_LIGHT) <= 0) {
            queuedMove = type;
        }
    }

    public Vector3f getAuraColor() {
        return auraColors[getSkin()];
    }

    public enum MoveSelectionResult {
        /**
         * Continues current move evaluation
         */
        PASS,
        /**
         * Stops the evaluation and uses the move
         */
        USE,
        /**
         * Skips to the next move evaluation
         */
        STOP
    }

    /**
     * Used to help AIs that use stands with unique moves
     */
    public MoveSelectionResult specificMoveSelectionCriterion(AbstractMove<?, ? super E> attack, LivingEntity mob, LivingEntity target, int stunTicks,
                                                              int enemyMoveStun, double distance, StandEntity<?, ?> enemyStand, AbstractMove<?, ?> enemyAttack) {
        return MoveSelectionResult.PASS;
    }

    private @Nullable Tuple<AbstractMove<?, ?>, Boolean> selectAttack(CommonCooldownsComponent cooldowns, LivingEntity mob, LivingEntity target, int stunTicks, int enemyMoveStun, double distance, StandEntity<?, ?> enemyStand, AbstractMove<?, ?> enemyAttack) {
        AbstractMove<?, ? super E> selectedAttack;
        boolean needsCrouch = false;
        boolean doFinalChecks = true; // Refuses to run the move if certain conditions are met
        boolean enemyIsAttacking = enemyAttack != null;

        // If the opponent is countering, don't attack
        if (enemyIsAttacking && enemyAttack.isCounter()) {
            return null;
        }
        int movesOnCooldown = 0;

        MoveMap.Entry<E, S> lightEntry = getMoveMap().getFirstValidEntry(MoveType.LIGHT, getThis());
        if (lightEntry == null) {
            MoveMap.Entry<E, S> heavyEntry = getMoveMap().getFirstValidEntry(MoveType.HEAVY, getThis());
            if (heavyEntry == null) {
                JCraft.LOGGER.warn("Couldn't find light or heavy attack entry while running selectAttack on stand: " + this);
                return null;
            } else {
                selectedAttack = heavyEntry.getMove();
            }
        } else {
            selectedAttack = lightEntry.getMove();
        }

        int selectedAttackInitTime = selectedAttack.getDuration() - selectedAttack.getWindup();

        for (AbstractMove<?, ? super E> attack : getMoveMap().asMovesList()) {
            needsCrouch = attack.isCrouchingVariant();
            int windupPoint = attack.getWindupPoint();

            if (attack.isFollowup()) {
                // Discount any followup attacks when there is no move to follow up from
                if (curMove == null || curMove.getFollowup() != null) continue;
            } else if (cooldowns.getCooldown(attack.getMoveType().getDefaultCooldownType()) > 0) {
                // Discount any on-cooldown non-followup attacks
                movesOnCooldown++;
                continue;
            }

            // Selection of characteristic moves with custom usage logic
            MoveSelectionResult result = specificMoveSelectionCriterion(attack, mob, target, stunTicks, enemyMoveStun, distance, enemyStand, enemyAttack);
            if (result == MoveSelectionResult.USE) {
                selectedAttack = attack;
                break;
            }
            if (result == MoveSelectionResult.STOP) {
                continue;
            }

            // Use mobility if opponent is far away
            if (attack.getMobilityType() != null) {
                // ...and isn't being comboed or is blocking
                if (stunTicks > 0) {
                    continue;
                }

                if (attack.getMobilityType() != MobilityType.HIGHJUMP && distance > 6) {
                    if (target.onGround()) {
                        if (attack.getMobilityType() == MobilityType.TELEPORT) {
                            // Intentionally looks at target's feet as to hit the ground exactly at it
                            mob.lookAt(Anchor.EYES, target.position());
                        } else if (attack.getMobilityType() == MobilityType.DASH) {
                            // Look at target itself as a dash works best at that angle
                            mob.lookAt(Anchor.EYES, target.getEyePosition().add(0, 0.5, 0));
                        }
                    }

                    if (attack.getMobilityType() == MobilityType.FLIGHT) {
                        mob.lookAt(Anchor.EYES, target.getEyePosition());
                    }

                    selectedAttack = attack;
                    break;
                } // If target is considerably above the mob, or the mob is going to get hit
                else if (target.getY() > mob.getY() + 2 || (enemyAttack != null && enemyStand != null && enemyAttack.hasWindupPassed(enemyStand))) {
                    selectedAttack = attack;
                    break;
                }
            }

            // Use counter if opponent is using a non-ranged move
            if (enemyIsAttacking && enemyAttack != null && !enemyAttack.isRanged() && attack.isCounter()) {
                if (enemyStand != null && !enemyStand.blocking && enemyMoveStun > 0) {
                    selectedAttack = attack;
                    break;
                }
                continue;
            }

            boolean isBarrage = attack.isBarrage();
            boolean isCharge = attack.isCharge();
            if (distance <= 5) {
                //todo: expand on mob.canSee(target), because placing fences down doesn't cause them to want to break through
                if (isBarrage && !isCharge && !mob.hasLineOfSight(target)) // Mine towards target if possible
                {
                    if (attack instanceof MainBarrageAttack<?>) {
                        selectedAttack = attack;
                        needsCrouch = true;
                        doFinalChecks = false; // Disregards range limitation
                        break;
                    }
                }

            /*
            Use a barrage (or variant thereof) if the opponent is stunned, not blocking, and it's off cooldown,
            because it's a free combo extender and has a lower windup than light
             */
                if (distance <= 2) {
                    if (isBarrage || (attack.isMultiHit() && attack.hasWindupPassed(this))) {
                        // Combo extend
                        if (enemyStand == null || !enemyStand.blocking) {
                            selectedAttack = attack;
                            break;
                        }
                        continue;
                    }
                }
            }

            // If the opponent is out of exactly twice the range it would take him to get to the user within the move being complete, use a projectile
            if (attack.isRanged() && distance > attack.getDuration() * target.getAttributeValue(Attributes.MOVEMENT_SPEED) * 2) {
                mob.lookAt(Anchor.EYES, target.getEyePosition());
                selectedAttack = attack;
                break;
            }

            // If the opponent isn't using a move, prioritize attack with higher or equal initiation time
            if (windupPoint <= stunTicks && windupPoint >= selectedAttackInitTime) {
                selectedAttackInitTime = windupPoint;
                selectedAttack = attack;
            }
        }

        if (movesOnCooldown > 5 && !(mob instanceof StandEntity<?, ?>)) {
            cooldowns.cooldownCancel(); // >5 = 80+%
        }

        if (doFinalChecks) {
            if (selectedAttack.isCounter()) {
                if (stunTicks > 0) {
                    selectedAttack = null; // You can't combo into a counter
                }
            } else {
                if ( // Non-ranged offensive attacks aren't chosen if the opponent is too far
                        selectedAttack.getMobilityType() == null &&
                                selectedAttack instanceof AbstractSimpleAttack<?, ?> boxAttack &&
                                boxAttack.getHitboxSize() > 0 &&
                                !selectedAttack.isRanged() &&
                                distance > selectedAttack.getMoveDistance() + boxAttack.getHitboxSize()) {
                    selectedAttack = null;
                }
            }
        }

        return new Tuple<>(selectedAttack, needsCrouch);
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
    public boolean handleDamage(Vec3 kbVec, int stunTicks, int stunLevel, boolean overrideStun, float damage, boolean lift, int blockstun, DamageSource source, Entity attacker, CommonHitPropertyComponent.HitAnimation hitAnimation, boolean canBackstab, boolean unblockable) {
        if (hasUser()) {
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
                damageLogic(level(), user, kbVec, stunTicks, stunLevel, overrideStun, damage, lift, blockstun, source, attacker, hitAnimation, canBackstab, unblockable);
            }
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
    protected void freshKill(@Nullable LivingEntity entity) {
    }
}
