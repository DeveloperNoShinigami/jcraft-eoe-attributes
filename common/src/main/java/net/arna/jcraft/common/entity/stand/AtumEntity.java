package net.arna.jcraft.common.entity.stand;

import lombok.NonNull;
import net.arna.jcraft.common.attack.core.MoveMap;
import net.arna.jcraft.common.util.StandAnimationState;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import mod.azure.azurelib.animatable.GeoEntity;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.core.animation.RawAnimation;
import mod.azure.azurelib.core.object.PlayState;
import mod.azure.azurelib.util.AzureLibUtil;
import mod.azure.azurelib.core.animatable.GeoAnimatable;

public class AtumEntity extends StandEntity<AtumEntity, AtumEntity.State> {

    public AtumEntity(Level world) {
        super(StandType.ATUM, world);
    }

    @Override
    protected void registerMoves(MoveMap<AtumEntity, State> moves) {
        // TODO Arna
    }

    @Override
    public @NonNull AtumEntity getThis() {
        return this;
    }

    public enum State implements StandAnimationState<AtumEntity> {
        IDLE,
        BLOCK;

        @Override
        public void playAnimation(AtumEntity attacker, AnimationState state) {
            // TODO Arna
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
        return "animation.atum.summon";
    }

}
