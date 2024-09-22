package net.arna.jcraft.common.entity.stand;

import it.unimi.dsi.fastutil.ints.IntSet;
import lombok.NonNull;
import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.core.animation.RawAnimation;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.attack.core.MoveInputType;
import net.arna.jcraft.common.attack.core.MoveMap;
import net.arna.jcraft.common.attack.core.MoveType;
import net.arna.jcraft.common.attack.core.StunType;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.dirtydeedsdonedirtcheap.*;
import net.arna.jcraft.common.attack.moves.shared.MainBarrageAttack;
import net.arna.jcraft.common.attack.moves.shared.SimpleAttack;
import net.arna.jcraft.common.attack.moves.shared.SimpleMultiHitAttack;
import net.arna.jcraft.common.component.living.CommonHitPropertyComponent;
import net.arna.jcraft.common.util.JParticleType;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.common.util.StandAnimationState;
import net.arna.jcraft.registry.JDimensionRegistry;
import net.arna.jcraft.registry.JItemRegistry;
import net.arna.jcraft.registry.JSoundRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.function.Consumer;

public class D4CEntity extends StandEntity<D4CEntity, D4CEntity.State> {
    public static final ItemPlaceMove ITEM_PLACE = new ItemPlaceMove(JCraft.LIGHT_COOLDOWN, 8, 12, 0.75f)
            .withAnim(State.ITEM_PLACE)
            .withInfo(
                    Component.literal("Item Place"),
                    Component.literal("places an item from an alternate universe on the ground, which attracts other such items"));
    public static final SimpleAttack<D4CEntity> LIGHT_FOLLOWUP = new SimpleAttack<D4CEntity>(
            0, 9, 14, 0.75f, 7f, 8, 1.75f, 1.25f, -0.1f)
            .withAnim(State.LIGHT_FOLLOWUP)
            .withSound(JSoundRegistry.D4C_LIGHT.get())
            .withImpactSound(JSoundRegistry.IMPACT_1.get())
            .withLaunch()
            .withBlockStun(4)
            .withExtraHitBox(0, 0, 1)
            .withHitSpark(JParticleType.HIT_SPARK_2)
            .withInfo(
                    Component.literal("Deadly Blow"),
                    Component.literal("combo finisher, more blockstun than other light followups"));
    public static final SimpleAttack<D4CEntity> CHOP = new SimpleAttack<D4CEntity>(JCraft.LIGHT_COOLDOWN,
            9, 15, 0.75f, 5f, 20, 1.5f, 0.25f, -0.1f)
            .withFollowup(LIGHT_FOLLOWUP)
            .withCrouchingVariant(ITEM_PLACE)
            .withImpactSound(JSoundRegistry.IMPACT_2.get())
            .withHitAnimation(CommonHitPropertyComponent.HitAnimation.HIGH)
            .withInfo(
                    Component.literal("Chop"),
                    Component.literal("relatively quick combo starter"));
    public static final MainBarrageAttack<D4CEntity> BARRAGE = new MainBarrageAttack<D4CEntity>(240, 0,
            40, 0.75f, 0.8f, 30, 2f, 0.25f, 0f, 3, Blocks.DEEPSLATE.defaultDestroyTime())
            .withSound(JSoundRegistry.D4C_BARRAGE.get())
            .withImpactSound(JSoundRegistry.IMPACT_2.get())
            .withInfo(
                    Component.literal("Barrage"),
                    Component.literal("fast reliable combo starter/extender, high stun"));
    public static final SimpleAttack<D4CEntity> CHARGE = new SimpleAttack<D4CEntity>(200, 14, 25,
            1f, 8f, 12, 2f, 1.5f, -0.2f)
            .withInitAction(D4CEntity::doCharge)
            .withSound(JSoundRegistry.D4C_HEAVY.get())
            .withImpactSound(JSoundRegistry.IMPACT_2.get())
            .withHitSpark(JParticleType.HIT_SPARK_3)
            .withHyperArmor()
            .withLaunch()
            .withInfo(
                    Component.literal("Charge"),
                    Component.literal("user & stand charge forward, uninterruptible launcher"));
    public static final DimensionalHopMove DIM_HOP = new DimensionalHopMove(1200, 40, 60,
            1f, 0f, 0, 1.75f, 0f, 0f)
            .withSound(JSoundRegistry.D4C_DIMHOP.get())
            .withInfo(
                    Component.literal("Dimensional Hop"),
                    Component.literal("travels to a random dimension at exact coordinates, " +
                            "if user was hit in the last 30s, he is forced back, certified death button"));
    public static final GiveGunMove GIVE_GUN = new GiveGunMove(280, 10, 14, 0.75f)
            .withSound(JSoundRegistry.D4C_THROW.get())
            .withInitAction(D4CEntity::equipRevolver)
            .withInfo(
                    Component.literal("Summon Gun"),
                    Component.literal("gives the user a revolver"));
    public static final SimpleAttack<D4CEntity> GRAB_HIT_FINAL = new SimpleAttack<D4CEntity>(0, 26,
            34, 0.75f, 4f, 9, 2f, 1.2f, 0f)
            .withImpactSound(JSoundRegistry.IMPACT_1.get())
            .withHitSpark(JParticleType.HIT_SPARK_2)
            .withLaunch()
            .withInfo(
                    Component.literal("Grab (Final Hit)"),
                    Component.empty());
    public static final SimpleMultiHitAttack<D4CEntity> GRAB_HIT = new SimpleMultiHitAttack<D4CEntity>(0,
            34, 0.75f, 4f, 10, 2f, 0f, 0f, IntSet.of(11, 17, 26))
            .withImpactSound(JSoundRegistry.IMPACT_1.get())
            // Play sound regardless of whether something hit.
            .withAction((attacker, user, ctx, targets) -> attacker.playSound(JSoundRegistry.REVOLVER_FIRE.get(), 1, 1))
            .withStunType(StunType.UNBURSTABLE)
            .withFinisher(17, GRAB_HIT_FINAL)
            .withInfo(
                    Component.literal("Grab (Final Hit)"),
                    Component.empty());
    public static final D4CGrabAttack GRAB = new D4CGrabAttack(280, 12, 21, 0.75f,
            0f, 40, 1.5f, 0f, 0f, GRAB_HIT, State.THROW_HIT, 25, 1)
            .withCrouchingVariant(GIVE_GUN)
            .withSound(JSoundRegistry.D4C_THROW.get())
            .withInitAction(D4CEntity::equipRevolver)
            .withInfo(
                    Component.literal("Grab"),
                    Component.literal("unblockable, combo finisher"));
    public static final D4CCounterAttack COUNTER = new D4CCounterAttack(400, 5, 35, 0.75f)
            .withInfo(
                    Component.literal("Counter"),
                    Component.literal("0.25s startup, 1.5s duration, high damage, knocks back when hit"));
    public static final CloneSpawnMove CLONE_SPAWN = new CloneSpawnMove(400, 40, 50, 1f)
            .withSound(JSoundRegistry.D4C_DIMHOP.get())
            .withInfo(
                    Component.literal("Dimensional Clone"),
                    Component.literal("""
                            summons an unlimited number of servants, crouch and interact to give/take items, press a special button to change their weapon
                            Servant types:
                            DEFAULT - Iron Sword
                            SPECIAL 1 - Wooden Axe
                            SPECIAL 2 - Bow
                            SPECIAL 3 - None"""));
    public static final FlagMove FLAG = new FlagMove(280, 10, 60, 0f)
            .withSound(JSoundRegistry.D4C_UTILITY.get())
            .withInfo(
                    Component.literal("Dimensional Phase"),
                    Component.literal("hides in a flag in an un-stunnable, floating state"));

    public D4CEntity(Level worldIn) {
        super(StandType.D4C, worldIn, JSoundRegistry.D4C_SUMMON.get(), true);

        idleRotation = -45f;

        proCount = 4;
        conCount = 2;

        freespace =
                """
                        BNBs:
                            -the lazy zoner
                            Light>Barrage>Light>Grab/Charge
                            
                            -the western
                            Light>Summon Gun>Barrage>Light~stand.OFF>M2>M2>M2>~s.ON+Light>Charge""";

        auraColors = new Vector3f[]{
                new Vector3f(0.9f, 0.5f, 0.7f),
                new Vector3f(0.5f, 0.8f, 0.9f),
                new Vector3f(0.4f, 0.4f, 1.0f),
                new Vector3f(1.0f, 0.5f, 0.2f)
        };
    }

    @Override
    protected void registerMoves(MoveMap<D4CEntity, State> moves) {
        moves.registerImmediate(MoveType.LIGHT, CHOP, State.LIGHT);

        moves.register(MoveType.HEAVY, CHARGE, State.HEAVY);
        moves.register(MoveType.BARRAGE, BARRAGE, State.BARRAGE);

        moves.register(MoveType.SPECIAL1, CLONE_SPAWN, State.DIM_HOP);
        moves.register(MoveType.SPECIAL2, GRAB, State.THROW).withCrouchingVariant(State.GIVE_GUN);
        moves.register(MoveType.SPECIAL3, COUNTER, State.COUNTER);
        moves.register(MoveType.ULTIMATE, DIM_HOP, State.DIM_HOP);

        moves.register(MoveType.UTILITY, FLAG, State.FLAG);
    }

    private static void doCharge(D4CEntity attacker, LivingEntity user, MoveContext ctx) {
        if (!user.onGround()) {
            return;
        }
        JUtils.addVelocity(user, attacker.getLookAngle().scale(0.75).add(0.0, 0.15, 0.0));
    }

    private static void equipRevolver(D4CEntity attacker, LivingEntity user, MoveContext ctx) {
        attacker.setItemSlot(EquipmentSlot.MAINHAND, JItemRegistry.FV_REVOLVER.get().getDefaultInstance());
    }

    @Override
    public boolean initMove(MoveType type) {
        switch (type) {
            case SPECIAL1 -> getMoveContext().set(CloneSpawnMove.CLONE_TYPE, CloneSpawnMove.CloneType.AXE);
            case SPECIAL2 -> getMoveContext().set(CloneSpawnMove.CLONE_TYPE, CloneSpawnMove.CloneType.BOW);
            case SPECIAL3 -> getMoveContext().set(CloneSpawnMove.CLONE_TYPE, CloneSpawnMove.CloneType.EMPTY);
            case ULTIMATE -> {
                if (getCurrentMove() != null && getCurrentMove().getOriginalMove() == DIM_HOP) {
                    setMoveStun(0);
                    setCurrentMove(null);
                }

                if (level().dimension().equals(JDimensionRegistry.AU_DIMENSION_KEY)) {
                    setMove(DIM_HOP, State.DIM_HOP);
                    playSound(JSoundRegistry.D4C_DIMHOP.get(), 1, 1);
                    return true;
                }
            }
            case LIGHT -> {
                if (tryFollowUp(type, MoveType.LIGHT)) return true;
            }
        }

        return super.initMove(type);
    }

    @Override
    public void queueMove(MoveInputType type) {
        if (getCurrentMove() != null && getCurrentMove().getOriginalMove() == CLONE_SPAWN) {
            return;
        }
        super.queueMove(type);
    }

    @Override
    protected @NotNull AABB makeBoundingBox() {
        if (getState() == State.FLAG) {
            final double x = getX(), y = getY(), z = getZ();
            return new AABB(x + 0.5, y + 0.5, z + 0.5, x - 0.5, y, z - 0.5);
        }
        return super.makeBoundingBox();
    }

    /* -- OLD GUN THROW CODE
                Vec3d rotVec = this.getRotationVector();
                Vec3d eyePos = this.getEyePos();

                ItemEntity revolver1 = new ItemEntity(EntityType.ITEM, world);
                revolver1.setStack(new ItemStack(JObjectRegistry.FVREVOLVER, 1));
                revolver1.setPickupDelay(100);
                revolver1.setPosition(eyePos.add(rotVec.rotateY(90)));
                revolver1.setVelocity(rotVec.rotateY(95).multiply(1.5));

                ItemEntity revolver2 = new ItemEntity(EntityType.ITEM, world);
                revolver2.setStack(new ItemStack(JObjectRegistry.FVREVOLVER, 1));
                revolver2.setPickupDelay(100);
                revolver2.setPosition(eyePos.add(rotVec.rotateY(-90)));
                revolver2.setVelocity(rotVec.rotateY(-95).multiply(1.5));

                world.spawnEntity(revolver1);
                world.spawnEntity(revolver2);
    */

    @Override
    @NonNull
    public D4CEntity getThis() {
        return this;
    }

    // Animation code
    public enum State implements StandAnimationState<D4CEntity> {
        IDLE(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.d4c.idle"))),
        LIGHT(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.d4c.light"))),
        BLOCK(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.d4c.block"))),
        HEAVY(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.d4c.heavy"))),
        BARRAGE(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.d4c.barrage"))),
        DIM_HOP(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.d4c.dimhop"))),
        THROW(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.d4c.throw"))),
        THROW_HIT(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.d4c.throwhit"))),
        COUNTER(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.d4c.counter"))),
        COUNTER_MISS(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.d4c.counter_miss"))),
        GIVE_GUN(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.d4c.givegun"))),
        FLAG(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.d4c.flag"))),
        ITEM_PLACE(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.d4c.itemplace"))),
        LIGHT_FOLLOWUP(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.d4c.light_followup")));

        private final Consumer<AnimationState<D4CEntity>> animator;

        State(Consumer<AnimationState<D4CEntity>> animator) {
            this.animator = animator;
        }

        @Override
        public void playAnimation(D4CEntity attacker, AnimationState<D4CEntity> state) {
            animator.accept(state);
        }
    }

    @Override
    protected State[] getStateValues() {
        return State.values();
    }

    @Override
    protected String getSummonAnimation() {
        return "animation.d4c.summon";
    }

    @Override
    public State getBlockState() {
        return State.BLOCK;
    }
}
