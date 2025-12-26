package net.arna.jcraft.common.spec;

import lombok.Getter;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.api.attack.MoveMap;
import net.arna.jcraft.api.attack.MoveSet;
import net.arna.jcraft.api.attack.MoveSetManager;
import net.arna.jcraft.api.attack.enums.MoveClass;
import net.arna.jcraft.api.component.living.CommonMiscComponent;
import net.arna.jcraft.api.registry.JSoundRegistry;
import net.arna.jcraft.api.registry.JSpecTypeRegistry;
import net.arna.jcraft.api.spec.JSpec;
import net.arna.jcraft.api.spec.SpecData;
import net.arna.jcraft.common.attack.conditions.HamonChargeCondition;
import net.arna.jcraft.common.attack.core.MoveMapImpl;
import net.arna.jcraft.common.attack.moves.hamon.RippleAttack;
import net.arna.jcraft.common.attack.moves.hamon.ZoomPunchAttack;
import net.arna.jcraft.common.attack.moves.shared.SimpleAttack;
import net.arna.jcraft.common.util.CooldownType;
import net.arna.jcraft.common.util.JParticleType;
import net.arna.jcraft.common.util.SpecAnimationState;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
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

    public boolean useHamonNext = false;
    @Getter
    private float charge = 0.0f;

    public HamonSpec(LivingEntity livingEntity) {
        super(JSpecTypeRegistry.HAMON.get(), livingEntity);
    }

    public static final SimpleAttack<HamonSpec> FOCUS_STRIKE = new SimpleAttack<HamonSpec>(0, 8,
            14, 1.5f, 4f, 9, 1.5f, 1.0f, 0f)
            .withImpactSound(JSoundRegistry.IMPACT_5)
            .withHitSpark(JParticleType.HIT_SPARK_2)
            .withInfo(
                    Component.literal("Focus Strike"),
                    Component.literal("Charge with hamon for Zoom Punch, a slow yet far-reaching, launching strike.")
            );
    public static final ZoomPunchAttack ZOOM_PUNCH = new ZoomPunchAttack(0, 18,
            24, 1f, 6f, 13, 1.5f, 1.5f, -0.5f)
            .withImpactSound(JSoundRegistry.IMPACT_1)
            .withHitSpark(JParticleType.HIT_SPARK_2)
            .withLaunch()
            .withCondition(HamonChargeCondition.atLeast(ZoomPunchAttack.CHARGE_COST))
            .withExtraHitBox(1.0)
            .withInfo(
                    Component.literal("Zoom Punch"),
                    Component.literal("")
            );
    public static final SimpleAttack<HamonSpec> STOMP = new SimpleAttack<HamonSpec>(0, 7,
            13, 1.0f, 3f, 8, 1.25f, 0.5f, 0.4f)
            .withImpactSound(JSoundRegistry.IMPACT_5)
            .withHitSpark(JParticleType.HIT_SPARK_1)
            .withStaticY()
            .withInfo(
                    Component.literal("Stomp"),
                    Component.literal("Charge with hamon for Ripple, a powerful stomp that creates a Hamon Wave.")
            );
    public static final RippleAttack RIPPLE_ATTACK = new RippleAttack(0, 9,
            17, 1.1f, 6f, 8, 1.6f, 1.0f, 0.4f)
            .withImpactSound(JSoundRegistry.IMPACT_2)
            .withStaticY()
            .withInfo(
                    Component.literal("Ripple"),
                    Component.literal("")
            );

    public static final Map<MoveClass, MoveMap.Entry<HamonSpec, State>> HAMONIZED_MOVES = Map.ofEntries(
            Map.entry(
                    MoveClass.HEAVY,
                    new MoveMapImpl.EntryImpl<>(
                            MoveClass.HEAVY,
                            ZOOM_PUNCH,
                            CooldownType.HEAVY,
                            null
                    )
            ),
            Map.entry(
                    MoveClass.SPECIAL1,
                    new MoveMapImpl.EntryImpl<>(
                            MoveClass.SPECIAL1,
                            RIPPLE_ATTACK,
                            CooldownType.SPECIAL1,
                            State.RIPPLE
                    )
            )
    );

    private static void registerMoves(MoveMap<HamonSpec, HamonSpec.State> moves) {
        // moves.register(MoveClass.BARRAGE, COMBO, CooldownType.BARRAGE, VampireSpec.State.COMBO);
        moves.register(MoveClass.HEAVY, FOCUS_STRIKE, CooldownType.HEAVY, State.FOCUS_STRIKE);
        moves.register(MoveClass.SPECIAL1, STOMP, CooldownType.SPECIAL1, State.STOMP);
    }

    @Override
    public boolean initMove(MoveClass moveClass) {
        if (moveClass == MoveClass.BARRAGE) {
            useHamonNext = !useHamonNext;

            if (useHamonNext) {
                JCraft.createParticle((ServerLevel) user.level(), user.getX(), user.getY(), user.getZ(), JParticleType.FLASH);
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

        if (getEntityWorld().isClientSide) return;

        if (moveStun <= 0) {
            if (charge < MAX_CHARGE) {
                float add = 0.2f;

                if (user != null) {
                    float healthRatio = user.getHealth() / user.getMaxHealth();
                    add *= healthRatio;
                }

                charge += add;
            }

            CommonMiscComponent misc = JComponentPlatformUtils.getMiscData(user);
            misc.setHamonCharge(charge);
        }

        ZOOM_PUNCH.tick(this);
    }

    public void drainCharge(float charge) {
        this.charge -= charge;
        if (this.charge < 0.0F) this.charge = 0.0F;

        CommonMiscComponent misc = JComponentPlatformUtils.getMiscData(user);
        misc.setHamonCharge(charge);
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

        UPPERCUT("hm.uct"),
        SENDO("hm.snd"),

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
