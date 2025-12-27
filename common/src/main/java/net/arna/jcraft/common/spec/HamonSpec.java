package net.arna.jcraft.common.spec;

import lombok.Getter;
import lombok.NonNull;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.api.attack.MoveMap;
import net.arna.jcraft.api.attack.MoveSet;
import net.arna.jcraft.api.attack.MoveSetManager;
import net.arna.jcraft.api.attack.enums.MoveClass;
import net.arna.jcraft.api.component.living.CommonMiscComponent;
import net.arna.jcraft.api.registry.JParticleTypeRegistry;
import net.arna.jcraft.api.registry.JSoundRegistry;
import net.arna.jcraft.api.registry.JSpecTypeRegistry;
import net.arna.jcraft.api.spec.JSpec;
import net.arna.jcraft.api.spec.SpecData;
import net.arna.jcraft.common.attack.actions.LaunchUpAction;
import net.arna.jcraft.common.attack.actions.LungeAction;
import net.arna.jcraft.common.attack.conditions.HamonChargeCondition;
import net.arna.jcraft.common.attack.core.MoveMapImpl;
import net.arna.jcraft.common.attack.moves.hamon.RippleAttack;
import net.arna.jcraft.common.attack.moves.hamon.SendoAttack;
import net.arna.jcraft.common.attack.moves.hamon.ZoomPunchAttack;
import net.arna.jcraft.common.attack.moves.shared.SimpleAttack;
import net.arna.jcraft.common.config.JServerConfig;
import net.arna.jcraft.common.util.CooldownType;
import net.arna.jcraft.common.util.JParticleType;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.common.util.SpecAnimationState;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

import java.util.Map;

public class HamonSpec extends JSpec<HamonSpec, HamonSpec.State> {
    public static final MoveSet<HamonSpec, HamonSpec.State> MOVE_SET = MoveSetManager.create(JSpecTypeRegistry.HAMON, HamonSpec::registerMoves, HamonSpec.State.class);
    public static final SpecData DATA = SpecData.builder()
            .name(Component.translatable("spec.jcraft.hamon"))
            .description(Component.literal("Supernatural all-ranger"))
            .details(Component.literal("""
                    PASSIVES: Burns in sunlight, Replaces hunger with blood, Night vision
                    Excellent frametraps with Sweep or Axe Kick.
                    Bloodsuck is extremely rewarding and allows linking into almost any move."""))
            .build();
    public static final float MAX_CHARGE = 20.0f;

    private boolean useHamonNext = false;
    private int ticksSinceBreathSound = 0;
    @Getter
    private float charge = 0.0f;
    private final CommonMiscComponent misc;

    public HamonSpec(LivingEntity livingEntity) {
        super(JSpecTypeRegistry.HAMON.get(), livingEntity);
        misc = JComponentPlatformUtils.getMiscData(user);
    }

    public static final SimpleAttack<HamonSpec> FOCUS_STRIKE = new SimpleAttack<HamonSpec>(0, 8,
            14, 1.5f, 4f, 9, 1.5f, 1.0f, 0f)
            .withImpactSound(JSoundRegistry.IMPACT_6)
            .withHitSpark(JParticleType.HIT_SPARK_2)
            .withInfo(
                    Component.literal("Focus Strike"),
                    Component.literal("Charge with hamon for Zoom Punch, a slow yet far-reaching, launching strike. Can take one hit without being stopped.")
            );
    public static final ZoomPunchAttack ZOOM_PUNCH = new ZoomPunchAttack(0, 18,
            24, 1f, 6f, 13, 1.5f, 1.5f, -0.5f)
            .withSound(JSoundRegistry.HAMON_CRASH)
            .withImpactSound(JSoundRegistry.IMPACT_1)
            .withImpactSound(JSoundRegistry.HAMON_CRACKLE_IMPACT)
            .withHitSpark(JParticleType.HIT_SPARK_2)
            .withLaunch()
            .withCondition(HamonChargeCondition.atLeast(ZoomPunchAttack.CHARGE_COST))
            .withExtraHitBox(1.5)
            .withArmor(1)
            .withInfo(
                    Component.literal("Zoom Punch"),
                    Component.literal("")
            );

    public static final SimpleAttack<HamonSpec> STOMP = new SimpleAttack<HamonSpec>(0, 7,
            13, 1.0f, 3f, 8, 1.25f, 0.5f, 0.4f)
            .withImpactSound(JSoundRegistry.IMPACT_4)
            .withHitSpark(JParticleType.HIT_SPARK_1)
            .withStaticY()
            .withInfo(
                    Component.literal("Stomp"),
                    Component.literal("Charge with hamon for Ripple, a powerful stomp that creates a Hamon Wave.")
            );
    public static final RippleAttack RIPPLE_ATTACK = new RippleAttack(0, 9,
            17, 1.1f, 6f, 8, 1.6f, 1.0f, 0.4f)
            .withSound(JSoundRegistry.HAMON_ECHO)
            .withImpactSound(JSoundRegistry.IMPACT_1)
            .withStaticY()
            .withCondition(HamonChargeCondition.atLeast(RippleAttack.CHARGE_COST))
            .withInfo(
                    Component.literal("Ripple"),
                    Component.literal("")
            );

    public static final SimpleAttack<HamonSpec> KNEE_THRUST = new SimpleAttack<HamonSpec>(100, 12,
            19, 1.0f, 5f, 20, 1.5f, 1.0f, -0.1f)
            .withImpactSound(JSoundRegistry.IMPACT_3)
            .withHitSpark(JParticleType.HIT_SPARK_1)
            .withInitAction(LungeAction.lunge(0.5f, 0.25f))
            .withAnim(State.SENDO)
            .withInfo(
                    Component.literal("Knee Thrust"),
                    Component.literal("Charge with hamon for Sendo Wave Kick, which knocks the enemy down, and then props them back up with an aftershock of hamon.")
            );
    public static final SendoAttack SENDO_KICK = new SendoAttack(100, 12,
            19, 1.0f, 6.5f, 20, 1.6f, 2.0f, -0.1f)
            .withSound(JSoundRegistry.HAMON_SWOOSH)
            .withImpactSound(JSoundRegistry.HAMON_CRACKLE_IMPACT)
            .withCondition(HamonChargeCondition.atLeast(SendoAttack.CHARGE_COST))
            .withInitAction(LungeAction.lunge(0.5f, 0.25f))
            .withLaunch()
            .withAnim(State.SENDO)
            .withInfo(
                    Component.literal("Sendo Wave Kick"),
                    Component.literal("")
            );

    public static final SimpleAttack<HamonSpec> UPPERCUT = new SimpleAttack<HamonSpec>(0, 10,
            16, 1.0f, 6f, 18, 1.5f, 1.0f, -0.4f)
            .withImpactSound(JSoundRegistry.IMPACT_3)
            .withHitSpark(JParticleType.HIT_SPARK_1)
            .withAction(LaunchUpAction.launchUp(1.0f))
            .withAerialVariant(KNEE_THRUST)
            .withInfo(
                    Component.literal("Uppercut"),
                    Component.literal("Charge with hamon for Sendo Punch, which knocks the enemy down, and then props them back up with an aftershock of hamon.")
            );
    public static final SendoAttack SENDO_UPPERCUT = new SendoAttack(0, 10,
            16, 1.0f, 6f, 18, 1.5f, 1.0f, -0.4f)
            .withSound(JSoundRegistry.HAMON_SWOOSH)
            .withImpactSound(JSoundRegistry.HAMON_CRACKLE_IMPACT)
            .withHitSpark(JParticleType.HIT_SPARK_2)
            .withCondition(HamonChargeCondition.atLeast(SendoAttack.CHARGE_COST))
            .withAction(LaunchUpAction.launchUp(1.0f))
            .withAerialVariant(SENDO_KICK)
            .withInfo(
                    Component.literal("Uppercut"),
                    Component.literal("Charge with hamon for Sendo Punch, which knocks the enemy down, and then props them back up with an aftershock of hamon.")
            );

    public static final Map<MoveClass, MoveMap.Entry<HamonSpec, State>> HAMONIZED_MOVES = Map.ofEntries(
            Map.entry(MoveClass.HEAVY,
                    new MoveMapImpl.EntryImpl<>(
                            MoveClass.HEAVY,
                            ZOOM_PUNCH,
                            CooldownType.HEAVY,
                            null
                    )
            ),
            Map.entry(MoveClass.SPECIAL1,
                    new MoveMapImpl.EntryImpl<>(
                            MoveClass.SPECIAL1,
                            RIPPLE_ATTACK,
                            CooldownType.SPECIAL1,
                            State.RIPPLE
                    )
            ),
            Map.entry(MoveClass.SPECIAL2,
                    new MoveMapImpl.EntryImpl<>(
                            MoveClass.SPECIAL2,
                            SENDO_UPPERCUT,
                            CooldownType.SPECIAL2,
                            State.SENDO
                    )
            )
    );

    private static void registerMoves(MoveMap<HamonSpec, HamonSpec.State> moves) {
        // moves.register(MoveClass.BARRAGE, COMBO, CooldownType.BARRAGE, VampireSpec.State.COMBO);
        moves.register(MoveClass.HEAVY, FOCUS_STRIKE, CooldownType.HEAVY, State.FOCUS_STRIKE);
        moves.register(MoveClass.SPECIAL1, STOMP, CooldownType.SPECIAL1, State.STOMP);
        moves.register(MoveClass.SPECIAL2, UPPERCUT, CooldownType.SPECIAL2, State.UPPERCUT);
    }

    @Override
    public boolean initMove(MoveClass moveClass) {
        if (moveClass == MoveClass.BARRAGE) {
            setUseHamonNext(!useHamonNext);

            if (useHamonNext) {
                final ServerLevel level = (ServerLevel) user.level();

                JCraft.createParticle(level, user.getX(), user.getY(), user.getZ(), JParticleType.FLASH);

                var packet = new ClientboundLevelParticlesPacket(JParticleTypeRegistry.HAMON_SPARK.get(),
                        false,
                        user.getX(), user.getY(), user.getZ(),
                        1, 1, 1,
                        1.0f, 10);

                for (ServerPlayer tracker : JUtils.around(level, user.position(), 128)) {
                    tracker.connection.send(packet);
                }

                playAttackerSound(JSoundRegistry.HAMON_SURGE.get(), 1.0f, 1.0f);
            }

            return true;
        }

        return super.initMove(moveClass);
    }

    @Override
    public MoveMap.Entry<HamonSpec, State> getMoveEntry(MoveClass moveClass, boolean crouching, boolean aerial) {
        if (useHamonNext) {
            if (HAMONIZED_MOVES.containsKey(moveClass)) {
                return HAMONIZED_MOVES.get(moveClass);
            }
        }

        return super.getMoveEntry(moveClass, crouching, aerial);
    }

    @Override
    public void tickSpec() {
        super.tickSpec();

        if (getEntityWorld().isClientSide) {
            return;
        }

        if (moveStun <= 0) {
            if (charge < MAX_CHARGE) {
                float add = 0.2f;

                if (user != null) {
                    float healthRatio = user.getHealth() / user.getMaxHealth();
                    add *= healthRatio;

                    if (user.getRandom().nextFloat() <= 0.05f && ticksSinceBreathSound > 40) {
                        playAttackerSound(JSoundRegistry.HAMON_BREATHE.get(), 0.5f, 0.9f);
                        ticksSinceBreathSound = 0;
                    }

                    ticksSinceBreathSound++;
                }

                charge += add;
            }

            misc.setHamonCharge(charge);
        }

        ZOOM_PUNCH.tick(this);
        RIPPLE_ATTACK.tick(this);
        SENDO_KICK.tick(this);
    }

    public void processTarget(@NonNull LivingEntity target) {
        if (
                !target.isInvertedHealAndHarm() &&
                !(
                        JServerConfig.PLAYER_VAMPS_DIE_TO_HAMON.getValue() &&
                        target instanceof ServerPlayer serverPlayer &&
                        JComponentPlatformUtils.getVampirism(serverPlayer).isVampire()
                )
        ) {
            return;
        }

        target.hurt(target.damageSources().indirectMagic(user, null), 1.5f);
        target.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 10, 0, false, true));
        target.setRemainingFireTicks(10);
    }

    public void drainCharge(float reduction) {
        this.charge -= reduction;
        if (this.charge < 0.0F) this.charge = 0.0F;

        misc.setHamonCharge(this.charge);
    }

    public void setUseHamonNext(boolean use) {
        useHamonNext = use;
        misc.setHamonizeReady(useHamonNext);
    }

    @Override
    public HamonSpec getThis() {
        return this;
    }

    public enum State implements SpecAnimationState<HamonSpec> {
        FOCUS_STRIKE("hm.fcs"),
        ZOOM_PUNCH_HIGH("hm.zp.hi"),
        ZOOM_PUNCH_MID("hm.zp.mi"),
        ZOOM_PUNCH_LOW("hm.zp.lo"),

        SENDO("hm.snd"),
        UPPERCUT("hm.uct"),

        STOMP("hm.stm"),
        RIPPLE("hm.rpl"),

        TOSS("hm.toss"),
        ;

        private final String key;

        State(String key) {
            this.key = key;
        }

        @Override
        public String getKey(HamonSpec spec) {
            return key;
        }
    }
}
