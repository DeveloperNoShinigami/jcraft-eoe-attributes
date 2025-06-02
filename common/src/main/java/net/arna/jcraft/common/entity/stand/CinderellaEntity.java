package net.arna.jcraft.common.entity.stand;

import lombok.NonNull;
import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.core.animation.RawAnimation;
import net.arna.jcraft.api.StandData;
import net.arna.jcraft.api.StandInfo;
import net.arna.jcraft.common.attack.core.MoveClass;
import net.arna.jcraft.common.attack.core.MoveMap;
import net.arna.jcraft.common.attack.core.data.MoveSet;
import net.arna.jcraft.common.attack.moves.shared.SimpleAttack;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.common.util.StandAnimationState;
import net.arna.jcraft.registry.JSoundRegistry;
import net.arna.jcraft.registry.JStandTypeRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * The {@link StandEntity} for <a href="https://jojowiki.com/Cinderella">Cinderella</a>.
 * @see StandType#CINDERELLA
 * @see net.arna.jcraft.client.model.entity.stand.CinderellaModel CinderellaModel
 * @see net.arna.jcraft.client.renderer.entity.stands.CinderellaRenderer CinderellaRenderer
 * @see net.arna.jcraft.common.entity.npc.AyaTsujiEntity AyaTsujiEntity
 */
public class CinderellaEntity extends StandEntity<CinderellaEntity, CinderellaEntity.State> {
    public static final MoveSet<CinderellaEntity, State> MOVE_SET = MoveSet.create(JStandTypeRegistry.CINDERELLA,
            CinderellaEntity::registerMoves, State.class);
    public static final StandData DATA = StandData.of(StandInfo.of(Component.translatable("entity.jcraft.cinderella")));

    public static final SimpleAttack<CinderellaEntity> LIGHT = SimpleAttack.<CinderellaEntity>lightAttack(
                    7, 11, 0.75f, 4f, 11, 0.15f, 0.2f)
            // .withFollowup(LIGHT_FOLLOWUP)
            // .withCrouchingVariant(CROUCHING_LIGHT)
            // .withAerialVariant(AIR_LIGHT)
            .withImpactSound(JSoundRegistry.IMPACT_2)
            .withInfo(
                    Component.literal("Punch"),
                    Component.literal("quick combo starter")
            );

    public CinderellaEntity(Level world) {
        super(JStandTypeRegistry.CINDERELLA.get(), world);
    }

    private static void registerMoves(MoveMap<CinderellaEntity, State> moves) {
        moves.registerImmediate(MoveClass.LIGHT, LIGHT, State.LIGHT);
    }

    @Override
    public void tick() {
        super.tick();
        // still ghetto, makes a ghost entity where aya spawns (but only if she generates naturally)
        if (!hasUser() || JUtils.getStand(getUserOrThrow()) != this || getUserOrThrow().getFirstPassenger() != this) {
            discard();
        }
    }

    @Override
    public @NonNull CinderellaEntity getThis() {
        return this;
    }

    public enum State implements StandAnimationState<CinderellaEntity> {
        IDLE(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.cinderella.idle"))),
        LIGHT(builder -> builder.setAnimation(RawAnimation.begin().thenPlayAndHold("animation.cinderella.light"))),
        BLOCK(builder -> builder.setAnimation(RawAnimation.begin().thenLoop("animation.cinderella.block"))),
        ;

        private final Consumer<AnimationState<CinderellaEntity>> animator;

        State(Consumer<AnimationState<CinderellaEntity>> animator) {
            this.animator = animator;
        }

        @Override
        public void playAnimation(CinderellaEntity attacker, AnimationState<CinderellaEntity> state) {
            animator.accept(state);
        }
    }

    @Override
    protected State[] getStateValues() {
        return State.values();
    }

    @Override
    public State getBlockState() {
        return State.BLOCK;
    }

    @Override
    protected @Nullable String getSummonAnimation() {
        return "animation.cinderella.summon";
    }
}
