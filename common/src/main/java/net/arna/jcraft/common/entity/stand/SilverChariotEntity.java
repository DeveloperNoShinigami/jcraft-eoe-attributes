package net.arna.jcraft.common.entity.stand;

import it.unimi.dsi.fastutil.ints.IntSet;
import lombok.NonNull;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.core.animation.RawAnimation;
import net.arna.jcraft.api.attack.MoveSetManager;
import net.arna.jcraft.common.attack.conditions.HoldingAnubisCondition;
import net.arna.jcraft.common.attack.core.MoveClass;
import net.arna.jcraft.common.attack.core.MoveMap;
import net.arna.jcraft.common.attack.core.StunType;
import net.arna.jcraft.api.attack.MoveSet;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.attack.moves.base.AbstractSimpleAttack;
import net.arna.jcraft.common.attack.moves.shared.*;
import net.arna.jcraft.common.attack.moves.silverchariot.*;
import net.arna.jcraft.common.component.living.CommonCooldownsComponent;
import net.arna.jcraft.common.util.CooldownType;
import net.arna.jcraft.common.util.JParticleType;
import net.arna.jcraft.common.util.StandAnimationState;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.arna.jcraft.registry.JItemRegistry;
import net.arna.jcraft.registry.JSoundRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * The {@link StandEntity} for <a href="https://jojowiki.com/Silver_Chariot">Silver Chariot</a>.
 * @see StandType#SILVER_CHARIOT
 * @see net.arna.jcraft.client.model.entity.stand.SilverChariotModel SilverChariotModel
 * @see net.arna.jcraft.client.renderer.entity.stands.SilverChariotRenderer SilverChariotRenderer
 * @see ArmorOffAttack
 * @see CircleSlashAttack
 * @see CleaveAttack
 * @see GodOfDeathAttack
 * @see GodOfDeathHitAttack
 * @see LastShotAttack
 * @see RayDartAttack
 * @see SCChargeAttack
 * @see SCCounterAttack
 * @see SpinBarrageAttack
 */
public class SilverChariotEntity extends StandEntity<SilverChariotEntity, SilverChariotEntity.State> {
    public static final MoveSet<SilverChariotEntity, State> DEFAULT_MOVE_SET = MoveSetManager.create(StandType.SILVER_CHARIOT,
            SilverChariotEntity::registerDefaultMoves, State.class);
    public static final MoveSet<SilverChariotEntity, State> POSSESSED_MOVE_SET = MoveSetManager.create(StandType.SILVER_CHARIOT,
            "possessed", SilverChariotEntity::registerPossessedMoves, State.class);

    public static final LastShotAttack LAST_SHOT = new LastShotAttack(140, 12, 15, 1f)
            .withAnim(State.LAST_SHOT)
            .withInfo(
                    Component.literal("Last Shot"),
                    Component.literal("Silver Chariot fires his rapier, which can bounce 5 times off walls, " +
                            "nerfs all hitboxes and damage by 25% until returned")
            );
    public static final SimpleAttack<SilverChariotEntity> LIGHT_FOLLOWUP = new SimpleAttack<SilverChariotEntity>(
            0, 6, 14, 0.65f, 6f, 12, 1.5f, 1.2f, -0.1f)
            .withAnim(State.LIGHT_FOLLOWUP)
            .withImpactSound(JSoundRegistry.IMPACT_1)
            .withLaunch()
            .withBlockStun(4)
            .withHitSpark(JParticleType.HIT_SPARK_2)
            .withInfo(
                    Component.literal("Slash"),
                    Component.literal("quick combo finisher")
            );
    public static final SimpleAttack<SilverChariotEntity> LIGHT = SimpleAttack.<SilverChariotEntity>lightAttack(5, 9, 0.65f, 5f,
                    11, 0.15f, -0.1f)
            .withFollowup(LIGHT_FOLLOWUP)
            .withCrouchingVariant(LAST_SHOT)
            .withSound(JSoundRegistry.SC_POKE)
            .withInfo(
                    Component.literal("Stab"),
                    Component.literal("quick combo starter, links into Spinning Blade while armor is off")
            );
    public static final MainBarrageAttack<SilverChariotEntity> BARRAGE = new MainBarrageAttack<SilverChariotEntity>(
            240, 0, 40, 0.65f, 0.9f, 25, 2.25f, 0.1f, 0f, 3, 1.25F)
            .withSound(JSoundRegistry.SC_BARRAGE)
            .withInfo(
                    Component.literal("Barrage"),
                    Component.literal("fast reliable combo starter/extender, high stun")
            );
    public static final SimpleAttack<SilverChariotEntity> HEAVY = new SimpleAttack<SilverChariotEntity>(
            200, 20, 28, 0.65f, 8f, 10, 2f, 1.5f, 0f)
            .withExtraHitBox(2, 0.1, 1)
            .withSound(JSoundRegistry.SC_HEAVY)
            .withHitSpark(JParticleType.HIT_SPARK_3)
            .withHyperArmor()
            .withLaunch()
            .withInfo(
                    Component.literal("Impaling Thrust"),
                    Component.literal("slow, uninterruptible launcher")
            );

    public static final SpinBarrageAttack ANUBIS_SPIN_BARRAGE = new SpinBarrageAttack(0, 7, 24,
            0.65f, 1f, 10, 2f, 0.1f, -0.2f, 2)
            .withAnim(State.SPIN_2)
            .withCondition(HoldingAnubisCondition.holdingAnubis())
            .withSound(JSoundRegistry.SC_SPIN)
            .withInfo(
                    Component.literal("Divine Blade"),
                    Component.literal("fast reliable combo starter/extender, low stun")
            );
    public static final BarrageAttack<SilverChariotEntity> SPIN_BARRAGE = new BarrageAttack<SilverChariotEntity>(240, 7, 24,
            0.65f, 1f, 10, 2f, 0.1f, -0.2f, 2)
            .withFollowup(ANUBIS_SPIN_BARRAGE)
            .withFollowupFrame(6)
            .withSound(JSoundRegistry.SC_SPIN)
            .withInfo(
                    Component.literal("Spinning Blade"),
                    Component.literal("fast reliable combo starter/extender, low stun")
            );

    public static final RayDartAttack RAY_DART_LOW = new RayDartAttack(100, 10, 18,
            0.65f, 6f, 20, 1.75f, 0.25f, 0.2f)
            .withSound(JSoundRegistry.SC_CHARGE)
            .withSound(SoundEvents.PLAYER_ATTACK_SWEEP)
            .withBlockStun(9)
            .withInfo(
                    Component.literal("Lacerate"),
                    Component.literal("Anubis Chariot and the user charge forward, high stun, low blockstun.")
            );
    public static final RayDartAttack RAY_DART_HIGH = new RayDartAttack(100, 12, 20,
            0.65f, 6f, 15, 2.0f, 0.25f, 0.2f)
            .withCrouchingVariant(RAY_DART_LOW)
            .withSound(JSoundRegistry.SC_CHARGE)
            .withImpactSound(JSoundRegistry.IMPACT_1)
            .withBlockStun(16)
            .withExtraHitBox(1, 1, 1)
            .withInfo(
                    Component.literal("Split"),
                    Component.literal("Anubis Chariot and the user charge forward, low stun, high blockstun.")
            );
    public static final CleaveAttack CLEAVE = new CleaveAttack(260, 12, 21, 0.75f, 9f,
            20, 2.5f, 0.8f, 0f)
            .withSound(JSoundRegistry.SC_CLEAVE)
            .withImpactSound(SoundEvents.PLAYER_ATTACK_SWEEP)
            .withHyperArmor()
            .withInfo(
                    Component.literal("Cleave"),
                    Component.literal("Silver Chariot detaches from the user, delivering an uninterruptible, combo-starting slice")
            );
    public static final SCChargeAttack CHARGE = new SCChargeAttack(280, 5, 19, 8f,
            5f, 17, 1.5f, 0.25f, 0f)
            .withSound(JSoundRegistry.SC_SUMMON)
            .withBackstab(false)
            .withInfo(
                    Component.literal("Shooting Star"),
                    Component.literal("Silver Chariot detaches from the user and charges in the looked direction, combo starter/extender")
            );
    public static final SCCounterAttack COUNTER = new SCCounterAttack(480, 4, 34, 0.5f)
            .withInfo(
                    Component.literal("Counter"),
                    Component.literal("0.2s windup, 1.5s duration, stuns when hit")
            );
    public static final SimpleMultiHitAttack<SilverChariotEntity> GOD_OF_DEATH_FINAL = new SimpleMultiHitAttack<SilverChariotEntity>(
            0, 59, 0.65f, 6f, 20, 2.5f, 1.25f, 0f,
            IntSet.of(54))
            .withImpactSound(JSoundRegistry.TW_KICK_HIT)
            .withLaunch()
            .withHitSpark(JParticleType.HIT_SPARK_3)
            .withInfo(
                    Component.literal("God of Death (Final Hit)"),
                    Component.empty()
            );
    public static final GodOfDeathHitAttack GOD_OF_DEATH_HIT = new GodOfDeathHitAttack(0, 59, 0.65f,
            4.5f, 32, 2f, 0.25f, 0f, IntSet.of(13, 23))
            .withFollowup(GOD_OF_DEATH_FINAL)
            .withImpactSound(JSoundRegistry.IMPACT_1)
            .withStunType(StunType.UNBURSTABLE)
            .withInfo(
                    Component.literal("God of Death (Hit)"),
                    Component.empty()
            );
    public static final GodOfDeathAttack GOD_OF_DEATH = new GodOfDeathAttack(1000, 23, 28,
            0.65f, 4f, 40, 1.75f, 0f, 0f)
            .withFollowup(GOD_OF_DEATH_HIT)
            .withStunType(StunType.UNBURSTABLE)
            .withInfo(
                    Component.literal("God of Death"),
                    Component.literal("high-damage beatdown, 1.5s stun on whiff, cannot be combo broken")
            );
    public static final ArmorOffAttack ARMOR_OFF = new ArmorOffAttack(1200, 6, 15, 0.65f,
            4f, 7, 1.75f, 0.75f, 0f)
            .withSound(JSoundRegistry.SC_ARMOROFF)
            .withLaunch()
            .withInfo(
                    Component.literal("Armor Off"),
                    Component.literal("25s of faster moves")
            );
    public static final CircleSlashAttack CIRCLE_SLASH = new CircleSlashAttack(0, 2, 20,
            0.65f, 5f, 20, 1.75f, 0f, 0f)
            .withExtraHitBox(-0.65, 0, 2)
            .withLaunch()
            .withHitSpark(JParticleType.HIT_SPARK_2)
            .withInfo(
                    Component.literal("Circle Slash (Hit)"),
                    Component.empty()
            );
    public static final SimpleHoldableMove<SilverChariotEntity> CIRCLE_CHARGE = new SimpleHoldableMove<SilverChariotEntity>(
            260, 101, 100, 0.65f, 15)
            .withFollowup(CIRCLE_SLASH.withAnim(State.CIRCLE_SLASH))
            .withArmor(2)
            .withInfo(
                    Component.literal("Circle Slash"),
                    Component.literal("""
                            2 armor points
                            Can be held, and released 0.75s in.
                            Depending on how much you hold, the damage and launch height increase."""
                    ));
    private static final EntityDataAccessor<Boolean> HAS_RAPIER;
    private static final EntityDataAccessor<Integer> MODE;

    static {
        MODE = SynchedEntityData.defineId(SilverChariotEntity.class, EntityDataSerializers.INT);
        HAS_RAPIER = SynchedEntityData.defineId(SilverChariotEntity.class, EntityDataSerializers.BOOLEAN);
    }

    public SilverChariotEntity(Level worldIn) {
        super(StandType.SILVER_CHARIOT, worldIn, JSoundRegistry.SC_SUMMON);
        idleRotation = 225f;

        proCount = 4;
        conCount = 3;

        setNormalDesc();

        auraColors = new Vector3f[]{
                new Vector3f(0.4f, 0.5f, 1f),
                new Vector3f(0.9f, 0.6f, 0.3f),
                new Vector3f(0.6f, 0.7f, 1f),
                new Vector3f(0.8f, 0.8f, 0.8f)
        };
    }

    @Override
    public Vector3f getAuraColor() {
        if (isPossessed()) {
            return new Vector3f(1.0f, 0f, 0f);
        }
        return super.getAuraColor();
    }

    private void setNormalDesc() {

        freespace =
                """
                        BNBs:
                            (Armor ON) Light>Barrage>Light>Cleave>Spinning Blade>Shooting Star>Light
                            (Armor ON) Shooting Star>Light>Barrage>Impaling Thrust
                            (Armor OFF) Shooting Star>Light>Spinning Blade>Barrage>Light>Cleave>Impaling Thrust
                            (Armor OFF) Light>Spinning Blade>Barrage>Shooting Star>Cleave>Light
                            (Armor OFF) Impaling Thrust>dash>Barrage>...
                        """;

        switchMoveSet(DEFAULT_MOVE_SET.getName());
    }

    private void setPossessedDesc() {

        freespace =
                """
                        BNBs:
                            (Light>)Charge~Barrage>Light>Spinning Blade>Light~Light
                            (Light>)Charge~Barrage>God of Death""";

        switchMoveSet(POSSESSED_MOVE_SET.getName());
    }

    @Override
    protected void switchMoveSet(String name) {
        final Vec3 lookDir = moveContext.hasEntry(SCChargeAttack.LOOK_DIR) ? moveContext.get(SCChargeAttack.LOOK_DIR) : null;
        super.switchMoveSet(name);
        if (!moveContext.hasEntry(SCChargeAttack.LOOK_DIR)) moveContext.register(SCChargeAttack.LOOK_DIR, lookDir);
        else moveContext.set(SCChargeAttack.LOOK_DIR, lookDir);
    }

    @Override
    public int getModeOrdinal() {
        return getMode().ordinal();
    }

    private static void registerDefaultMoves(MoveMap<SilverChariotEntity, State> moves) {
        moves.registerImmediate(MoveClass.LIGHT, LIGHT, State.STAB);

        moves.register(MoveClass.HEAVY, HEAVY, State.HEAVY);
        moves.register(MoveClass.BARRAGE, BARRAGE, State.BARRAGE);

        moves.register(MoveClass.SPECIAL1, SPIN_BARRAGE, State.SPIN);
        moves.register(MoveClass.SPECIAL2, CHARGE, State.P_CHARGE);
        moves.register(MoveClass.SPECIAL3, CLEAVE, State.CLEAVE);

        moves.register(MoveClass.ULTIMATE, ARMOR_OFF, State.ARMOR_OFF);

        moves.register(MoveClass.UTILITY, CIRCLE_CHARGE, State.CIRCLE_CHARGE).withFollowup(State.CIRCLE_SLASH);
    }

    private static void registerPossessedMoves(MoveMap<SilverChariotEntity, State> moves) {
        moves.registerImmediate(MoveClass.LIGHT, LIGHT, State.STAB);

        moves.register(MoveClass.HEAVY, HEAVY, State.HEAVY);
        moves.register(MoveClass.BARRAGE, BARRAGE, State.BARRAGE);

        moves.register(MoveClass.SPECIAL1, SPIN_BARRAGE, State.SPIN).withFollowup(State.SPIN_2);
        moves.register(MoveClass.SPECIAL2, RAY_DART_HIGH, State.CHARGE_HIGH).withCrouchingVariant(State.CHARGE_LOW);
        moves.register(MoveClass.SPECIAL3, COUNTER, State.COUNTER);

        moves.register(MoveClass.ULTIMATE, GOD_OF_DEATH, State.BEAT_DOWN_START);

        moves.register(MoveClass.UTILITY, CIRCLE_CHARGE, State.CIRCLE_CHARGE).withFollowup(State.CIRCLE_SLASH);
    }

    public Mode getMode() {
        return Mode.values()[entityData.get(MODE)];
    }

    public void setMode(Mode mode) {
        entityData.set(MODE, mode.ordinal());
    }

    public boolean hasRapier() {
        return entityData.get(HAS_RAPIER);
    }

    public void setHasRapier(boolean hasRapier) {
        entityData.set(HAS_RAPIER, hasRapier);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(HAS_RAPIER, true);
        entityData.define(MODE, Mode.REGULAR.ordinal());
    }

    @Override
    public boolean initMove(MoveClass moveClass) {
        return tryFollowUp(moveClass, MoveClass.LIGHT) || super.initMove(moveClass);
    }

    @Override
    public boolean handleMove(AbstractMove<?, ? super SilverChariotEntity> move, CooldownType cooldownType, State animState) {
        if (!move.canBeInitiated(this)) {
            return false;
        }

        final LivingEntity user = getUserOrThrow();
        final CommonCooldownsComponent cooldowns = JComponentPlatformUtils.getCooldowns(user);
        int cooldown = cooldowns.getCooldown(cooldownType);

        if (cooldown > 0) {
            return false;
        }

        final AbstractMove<?, ? super SilverChariotEntity> attackRef = move.copy();
        if (getMode() == Mode.ARMORLESS) {
            attackRef.withWindup((int) (attackRef.getWindup() * 0.67));
            attackRef.withDuration((int) (attackRef.getDuration() * 0.67));
        }
        if (!hasRapier() && attackRef instanceof AbstractSimpleAttack<?, ?> simpleAttackRef) {
            simpleAttackRef.withHitboxSize(simpleAttackRef.getHitboxSize() * 0.75f);
            simpleAttackRef.withDamage(simpleAttackRef.getDamage() * 0.75f);
        }
        setMove(attackRef, animState);

        cooldowns.setCooldown(cooldownType, move.getCooldown());
        return true;
    }

    public boolean isPossessed() {
        return getMode() == Mode.POSSESSED;
    }

    @Override
    public void tick() {
        super.tick();

        if (!hasUser()) {
            return;
        }
        final LivingEntity user = getUserOrThrow();
        final Mode mode = getMode();

        if (level().isClientSide) {
            // Possession particles
            if (mode == Mode.POSSESSED) {
                for (int i = 0; i < 16; i++) {
                    level().addParticle(
                            ParticleTypes.ASH,
                            getX() + random.nextDouble() - 0.5, getY() + random.nextDouble() * 0.25 + 0.5, getZ() + random.nextDouble() - 0.5,
                            0.0, 0.0, 0.0
                    );
                }
            }

            return;
        }

        // getOffHandStack() must be an AnubisItem
        boolean hasAnubis = getOffhandItem().is(JItemRegistry.ANUBIS.get()) || user.getMainHandItem().getItem() == JItemRegistry.ANUBIS.get();

        if (user instanceof Player player) {
            hasAnubis |= player.getInventory().contains(JItemRegistry.ANUBIS.get().getDefaultInstance());

            if (getCurrentMove() == null) {
                player.addItem(getOffhandItem());
                getOffhandItem().shrink(1);
            }
        } else if (!hasRapier() && random.nextFloat() < 0.1f) {
            desummon();
        }

        if (hasAnubis && mode != Mode.POSSESSED) {
            // Set possession state
            setMode(Mode.POSSESSED);
            setPossessedDesc();
        } else if (!hasAnubis && mode == Mode.POSSESSED) {
            // Reset
            setMode(Mode.REGULAR);
            setNormalDesc();
        }
    }

    @Override
    @NonNull
    public SilverChariotEntity getThis() {
        return this;
    }

    public enum Mode {
        REGULAR,
        ARMORLESS,
        POSSESSED
    }

    // Animation code
    public enum State implements StandAnimationState<SilverChariotEntity> {
        IDLE((silverChariot, builder) -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.silverchariot.idle" + switch (silverChariot.getMode()) {
            case REGULAR -> "";
            case ARMORLESS -> "_armorless";
            case POSSESSED -> "_possessed";
        }))),
        STAB(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.silverchariot.stab"))),
        BLOCK(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.silverchariot.block"))),
        HEAVY(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.silverchariot.heavy"))),
        BARRAGE(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.silverchariot.barrage"))),
        SPIN(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.silverchariot.spin"))),
        SPIN_2(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.silverchariot.spin_2"))),

        CHARGE_LOW(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.silverchariot.charge_low"))),
        CHARGE_HIGH(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.silverchariot.charge_high"))),

        P_CHARGE(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.silverchariot.pcharge"))),
        P_CHARGE_HIT(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.silverchariot.pchargehit"))),
        COUNTER(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.silverchariot.counter"))),
        BEAT_DOWN_START(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.silverchariot.beatdownstart"))),
        BEAT_DOWN(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.silverchariot.beatdown"))),
        CLEAVE(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.silverchariot.cleave"))),
        ARMOR_OFF(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.silverchariot.armor_off"))),
        COUNTER_MISS(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.silverchariot.counter_miss"))),
        LAST_SHOT(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.silverchariot.lastshot"))),
        CIRCLE_CHARGE(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.silverchariot.circle_charge"))),
        CIRCLE_SLASH(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.silverchariot.circle_slash"))),
        LIGHT_FOLLOWUP(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.silverchariot.light_followup")));

        private final BiConsumer<SilverChariotEntity, AnimationState<SilverChariotEntity>> animator;

        State(Consumer<AnimationState<SilverChariotEntity>> animator) {
            this((silverChariot, builder) -> animator.accept(builder));
        }

        State(BiConsumer<SilverChariotEntity, AnimationState<SilverChariotEntity>> animator) {
            this.animator = animator;
        }

        @Override
        public void playAnimation(SilverChariotEntity attacker, AnimationState<SilverChariotEntity> builder) {
            animator.accept(attacker, builder);
        }

        @Override
        public void configureController(SilverChariotEntity attacker, AnimationController<SilverChariotEntity> controller) {
            controller.setAnimationSpeed(attacker.getMode() == Mode.ARMORLESS ? 1.5f : 1f);
        }
    }

    @Override
    protected State[] getStateValues() {
        return State.values();
    }

    @Override
    protected @Nullable String getSummonAnimation() {
        return "animation.silverchariot.summon" + (isPossessed() ? "_possessed" : "");
    }

    @Override
    public State getBlockState() {
        return State.BLOCK;
    }
}
