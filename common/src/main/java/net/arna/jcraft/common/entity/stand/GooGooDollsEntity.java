package net.arna.jcraft.common.entity.stand;

import lombok.NonNull;
import mod.azure.azurelib.core.animation.AnimationState;
import net.arna.jcraft.api.stand.StandData;
import net.arna.jcraft.api.stand.StandInfo;
import net.arna.jcraft.api.attack.MoveSetManager;
import net.arna.jcraft.common.attack.core.MoveMap;
import net.arna.jcraft.api.attack.MoveSet;
import net.arna.jcraft.common.util.StandAnimationState;
import net.arna.jcraft.registry.JStandTypeRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * The {@link StandEntity} for <a href="https://jojowiki.com/Goo_Goo_Dolls">Goo Goo Dolls</a>.
 * @see JStandTypeRegistry#GOO_GOO_DOLLS
 * @see net.arna.jcraft.client.model.entity.stand.GooGooDollsModel GooGooDollsModel
 * @see net.arna.jcraft.client.renderer.entity.stands.GooGooDollsRenderer GooGooDollsRenderer
 */
public class GooGooDollsEntity extends StandEntity<GooGooDollsEntity, GooGooDollsEntity.State> {
    public static final MoveSet<GooGooDollsEntity, State> MOVE_SET = MoveSetManager.create(JStandTypeRegistry.GOO_GOO_DOLLS,
            GooGooDollsEntity::registerMoves, State.class);
    public static final StandData DATA = StandData.of(StandInfo.of(Component.translatable("entity.jcraft.goo_goo_dolls")))
            .withObtainable(false);

    public GooGooDollsEntity(Level world) {
        super(JStandTypeRegistry.GOO_GOO_DOLLS.get(), world);
    }

    private static void registerMoves(MoveMap<GooGooDollsEntity, State> moves) {
        // TODO Arna
    }

    public enum State implements StandAnimationState<GooGooDollsEntity> {
        IDLE,
        BLOCK;

        @Override
        public void playAnimation(GooGooDollsEntity attacker, AnimationState<GooGooDollsEntity> state) {
            // TODO Arna
        }
    }

    @Override
    public @NonNull GooGooDollsEntity getThis() {
        return this;
    }

    @Override
    protected State[] getStateValues() {
        return State.values();
    }

    @Override
    protected @Nullable String getSummonAnimation() {
        return "animation.goo_goo_dolls.summon";
    }

    @Override
    public State getBlockState() {
        return State.BLOCK;
    }
}
