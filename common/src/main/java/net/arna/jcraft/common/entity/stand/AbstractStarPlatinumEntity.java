package net.arna.jcraft.common.entity.stand;

import net.arna.jcraft.common.attack.moves.shared.SimpleAttack;
import net.arna.jcraft.common.attack.moves.starplatinum.BlockBreakingAttack;
import net.arna.jcraft.common.util.JParticleType;
import net.arna.jcraft.common.util.StandAnimationState;
import net.arna.jcraft.registry.JSoundRegistry;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public abstract sealed class AbstractStarPlatinumEntity<E extends AbstractStarPlatinumEntity<E, S>, S extends Enum<S> & StandAnimationState<E>> extends StandEntity<E, S>
        permits StarPlatinumEntity, SPTWEntity {
    public static final BlockBreakingAttack GROUND_BREAKER = new BlockBreakingAttack(
            200, 20, 30, 1f, 10f, 12, 2f, 1.5f, 0.5f)
            .withSound(JSoundRegistry.STAR_BREAKER)
            .withImpactSound(JSoundRegistry.IMPACT_8)
            .withHitSpark(JParticleType.HIT_SPARK_3)
            .withExtraHitBox(1.5)
            .withBlockStun(9)
            .withHyperArmor()
            .withLaunch()
            .withInfo(
                    Text.literal("Ground Breaker"),
                    Text.literal("slow, uninterruptible launcher, breaks the ground")
            );
    public static final SimpleAttack<AbstractStarPlatinumEntity<?, ?>> STAR_BREAKER = new SimpleAttack<AbstractStarPlatinumEntity<?, ?>>(
            200, 20, 30, 1f, 10f, 14, 2f, 1.5f, 0f)
            .withSound(JSoundRegistry.STAR_BREAKER)
            .withImpactSound(JSoundRegistry.IMPACT_8)
            .withHitSpark(JParticleType.HIT_SPARK_3)
            .withExtraHitBox(1.5)
            .withBlockStun(9)
            .withHyperArmor()
            .withLaunch()
            .withCrouchingVariant(GROUND_BREAKER)
            .withInfo(
                    Text.literal("Star Breaker"),
                    Text.literal("slow, uninterruptible launcher")
            );

    protected AbstractStarPlatinumEntity(StandType type, World worldIn) {
        super(type, worldIn, JSoundRegistry.STAR_PLATINUM_SUMMON);
        idleRotation = 225f;

        proCount = 3;
        conCount = 3;

        freespace =
                """
                        BNBs:
                        ~ represents a queued attack
                                                
                            -the classic
                            Light>Barrage>Light>Knee>Advancing Barrage~Star Finger~Star Breaker
                            
                            -the rushdown
                            Light~Light>dash Barrage>cr.Light>Star Finger>Knee>Light>Advancing Barrage>Light~Light
                            
                            -the blowback
                            Inhale>...>Star Finger>Star Breaker>Barrage>...

                            -the poke
                            Star Finger>Knee>Light>Advancing Barrage~Light>Barrage>Light>Star Breaker""";

        //moves = List.of(light, heavy, barrage, starfinger, inhale, lowkick, starfinger, jump);
    }

    @Override
    public void desummon() {
        if (tsTime > 0) {
            return;
        }
        super.desummon();
    }
}
