package net.arna.jcraft.common.spec;

import it.unimi.dsi.fastutil.ints.IntSet;
import net.arna.jcraft.common.attack.core.MoveMap;
import net.arna.jcraft.common.attack.core.MoveType;
import net.arna.jcraft.common.attack.core.StunType;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.shared.*;
import net.arna.jcraft.common.attack.moves.vampire.BloodSuckAttack;
import net.arna.jcraft.common.attack.moves.vampire.ReviveMove;
import net.arna.jcraft.common.attack.moves.vampire.SpaceRipperAttack;
import net.arna.jcraft.common.component.living.CommonHitPropertyComponent;
import net.arna.jcraft.common.component.living.CommonVampireComponent;
import net.arna.jcraft.common.util.CooldownType;
import net.arna.jcraft.common.util.JParticleType;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.common.util.SpecAnimationState;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.arna.jcraft.registry.JSoundRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class VampireSpec extends JSpec<VampireSpec, VampireSpec.State> {
    public static final UppercutAttack<VampireSpec> AIR_KICK = new UppercutAttack<VampireSpec>(30, 6,
            12, 1f, 5f, 14, 1.5f, 0.2f, 0.5f, -0.5f)
            .withImpactSound(JSoundRegistry.IMPACT_1.get())
            .withStaticY()
            .withHitSpark(JParticleType.HIT_SPARK_2)
            .withHitAnimation(CommonHitPropertyComponent.HitAnimation.CRUSH)
            .withInfo(Component.literal("Axe Kick"), Component.literal("jab"));

    public static final UppercutAttack<VampireSpec> SWEEP = new UppercutAttack<VampireSpec>(30, 6,
            12, 1f, 5f, 12, 1.5f, 0.2f, 0.5f, 0.5f)
            .withImpactSound(JSoundRegistry.IMPACT_1.get())
            .withStaticY()
            .withHitAnimation(CommonHitPropertyComponent.HitAnimation.LOW)
            .withInfo(Component.literal("Sweep Kick"), Component.literal("fast launcher"));

    public static final SimpleAttack<VampireSpec> ROUNDHOUSE = new SimpleAttack<VampireSpec>(30, 8,
            15, 1f, 6f, 19, 1.5f, 1.5f, 0f)
            .withCrouchingVariant(SWEEP)
            .withAerialVariant(AIR_KICK)
            .withImpactSound(JSoundRegistry.TW_KICK_HIT.get())
            .withHitSpark(JParticleType.HIT_SPARK_2)
            .withLaunch()
            .withInfo(Component.literal("Wheel Kick"), Component.literal("fast launcher"));

    public static final SimpleMultiHitAttack<VampireSpec> COMBO = new SimpleMultiHitAttack<VampireSpec>(240,
            23, 1f, 2.5f, 12, 1.5f, 0.2f, -0.1f, IntSet.of(5, 8, 12, 16, 20))
            .withImpactSound(JSoundRegistry.IMPACT_1.get())
            .withBlockStun(5)
            .withInfo(Component.literal("Beatdown"), Component.literal("hits 5 times, combo starter/extender"));

    public static final SimpleMultiHitAttack<VampireSpec> BLOODSUCK_HITS = new SimpleMultiHitAttack<VampireSpec>(0,
            25, 1f, 4, 5, 1.5f, 0.6f, -0.1f, IntSet.of(8, 16, 24))
            .withAction((attacker, user, ctx, targets) -> {
                user.heal(1);
                float bloodMult = JUtils.getBloodMult(ctx.get(BloodSuckAttack.TARGET));
                if (bloodMult <= 0) {
                    return;
                }
                attacker.vampireComponent.setBlood(attacker.vampireComponent.getBlood() + 2 * bloodMult);
                JUtils.serverPlaySound(JSoundRegistry.VAMPIRE_SUCK.get(), (ServerLevel) user.level(), user.position(), 32);
            })
            .withStunType(StunType.LAUNCH)
            .withInfo(Component.literal("Blood Suck (Hit)"), Component.empty());
    public static final BloodSuckAttack<VampireSpec, State> BLOODSUCK = new BloodSuckAttack<>(240, 10, 18,
            1f, 1f, BLOODSUCK_HITS.getDuration(), 1.5f, 0f, 0f, BLOODSUCK_HITS, State.BLOODSUCK_HIT, BLOODSUCK_HITS.getDuration(), 2f)
            .withSound(JSoundRegistry.VAMPIRE_GRAB_HIT.get())
            .withImpactSound(JSoundRegistry.IMPACT_9.get())
            .withHitSpark(JParticleType.BACK_STAB) // todo: bloodsuck particles
            .withInfo(Component.literal("Blood Suck"), Component.literal("blockable grab"));

    public static final SpaceRipperAttack SPACE_RIPPER_ATTACK = new SpaceRipperAttack(300, 1, 10, 1f)
            .withAction((attacker, user, ctx, targets) -> JUtils.serverPlaySound(JSoundRegistry.VAMPIRE_LASER_FIRE.get(), (ServerLevel) user.level(), user.position(), 96))
            .withInfo(Component.literal("Space Ripper Stingy Eyes (Fire)"), Component.empty());
    public static final HoldableMove<VampireSpec, State> SPACE_RIPPER_CHARGE = new HoldableMove<>(
            300, 0, 32, 1f, SPACE_RIPPER_ATTACK, State.SPACE_RIPPERS, 14)
            .withInitAction(((attacker, user, ctx) -> ctx.setInt(SpaceRipperAttack.CHARGE_TIME, 0)))
            .withSound(JSoundRegistry.VAMPIRE_LASER.get())
            .shouldSetMoveStun()
            .withInfo(Component.literal("Space Ripper Stingy Eyes"), Component.literal("""
                    Chargable laser beam attack.
                    Laser velocity depends on charge time.
                    After charging for 1.2s, becomes unblockable.
                    """));

    public static final NoOpMove<VampireSpec> TOGGLE_NV = new NoOpMove<VampireSpec>(20, 0, 0)
            .withInitAction(VampireSpec::toggleNightVision)
            .withInfo(Component.literal("Toggle Night Vision"), Component.empty());

    public static final ReviveMove<VampireSpec> REVIVE_MOVE = new ReviveMove<VampireSpec>(300, 16, 20, 5)
            .withCrouchingVariant(TOGGLE_NV)
            .withSound(JSoundRegistry.VAMPIRE_REANIMATE.get())
            .withInfo(Component.literal("Resurrection"), Component.literal("revives humanoid/undead enemies within 5 meters, that died within the last 1 minute"));

    private final CommonVampireComponent vampireComponent;
    private boolean nightVision = true;

    private static void toggleNightVision(VampireSpec attacker, LivingEntity living, MoveContext moveContext) {
        attacker.nightVision = !attacker.nightVision;
        if (!attacker.nightVision) {
            living.removeEffect(MobEffects.NIGHT_VISION);
        }
    }

    public VampireSpec(LivingEntity livingEntity) {
        super(SpecType.VAMPIRE, livingEntity);
        vampireComponent = JComponentPlatformUtils.getVampirism(livingEntity);
    }

    @Override
    protected void registerMoves(MoveMap<VampireSpec, State> moves) {
        MoveMap.Entry<VampireSpec, State> hvy = moves.register(MoveType.HEAVY, ROUNDHOUSE, CooldownType.HEAVY, State.ROUNDHOUSE);
        hvy.withCrouchingVariant(State.SWEEP);
        hvy.withAerialVariant(State.AXE_KICK);

        moves.register(MoveType.BARRAGE, COMBO, CooldownType.BARRAGE, State.COMBO);

        moves.register(MoveType.SPECIAL1, SPACE_RIPPER_CHARGE, CooldownType.SPECIAL1, State.SPACE_RIPPER_CHARGE);
        moves.register(MoveType.SPECIAL2, BLOODSUCK, CooldownType.SPECIAL2, State.BLOODSUCK);
        moves.register(MoveType.SPECIAL3, REVIVE_MOVE, CooldownType.SPECIAL3, State.RESURRECT).withCrouchingVariant(null);
    }

    @Override
    public void tickSpec() {
        super.tickSpec();
        if (!hasUser() || getUserOrThrow().level().isClientSide) {
            return;
        }
        if (nightVision) {
            getUserOrThrow().addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 400, 0, true, false));
        }
        if (curMove != null && curMove.getOriginalMove() == SPACE_RIPPER_CHARGE) {
            getMoveContext().incrementInt(SpaceRipperAttack.CHARGE_TIME, 1);
        }
    }

    @Override
    public VampireSpec getThis() {
        return this;
    }

    public enum State implements SpecAnimationState<VampireSpec> {
        SWEEP("vm.swp"),
        ROUNDHOUSE("vm.rnd"),
        AXE_KICK("vm.axe"),

        COMBO("vm.5hit"),

        SPACE_RIPPER_CHARGE("vm.srsc"),
        SPACE_RIPPERS("vm.srse"),

        BLOODSUCK("vm.bsk"),
        BLOODSUCK_HIT("vm.bsh"),

        RESURRECT("vm.rsr");

        private final String key;

        State(String key) {
            this.key = key;
        }

        @Override
        public String getKey(VampireSpec spec) {
            return key;
        }
    }
}
