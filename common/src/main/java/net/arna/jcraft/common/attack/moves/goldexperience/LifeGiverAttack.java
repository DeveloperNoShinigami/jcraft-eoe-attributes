package net.arna.jcraft.common.attack.moves.goldexperience;

import lombok.NonNull;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.core.ctx.MoveVariable;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.entity.GEButterflyEntity;
import net.arna.jcraft.common.entity.GEFrogEntity;
import net.arna.jcraft.common.entity.GESnakeEntity;
import net.arna.jcraft.common.entity.stand.GoldExperienceEntity;
import net.arna.jcraft.registry.JEntityTypeRegistry;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import java.util.Set;

public class LifeGiverAttack extends AbstractMove<LifeGiverAttack, GoldExperienceEntity> {
    public static final MoveVariable<LifeGiverType> TYPE_TO_SUMMON = new MoveVariable<>(LifeGiverType.class);

    public LifeGiverAttack(int cooldown, int windup, int duration, float moveDistance) {
        super(cooldown, windup, duration, moveDistance);
        ranged = true;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(GoldExperienceEntity attacker, LivingEntity user, MoveContext ctx) {
        ItemStack item = user.getOffhandItem(); // Get offhand, or if unavailable main hand stack
        if (item.isEmpty()) {
            item = user.getMainHandItem();
        }
        if (item.isEmpty()) {
            return Set.of();
        }

        LivingEntity animal = null;
        ItemStack animalItem = item.copy();
        animalItem.setCount(1);
        switch (ctx.get(TYPE_TO_SUMMON)) {
            case SNAKE -> {
                if (item.getMaxStackSize() <= 1) {
                    return Set.of();
                }

                GESnakeEntity snake = new GESnakeEntity(JEntityTypeRegistry.GE_SNAKE.get(), attacker.level());
                //todo: fix snake not working for mobs
                if (user instanceof Player playerEntity) {
                    snake.tame(playerEntity);
                }
                animal = snake;
            }
            case FROG -> {
                if (item.getMaxStackSize() <= 1) {
                    return Set.of();
                }

                GEFrogEntity frog = new GEFrogEntity(JEntityTypeRegistry.GE_FROG.get(), attacker.level());
                frog.setMaster(user);
                animal = frog;
            }
            case BUTTERFLY -> {
                GEButterflyEntity butterfly = new GEButterflyEntity(JEntityTypeRegistry.GE_BUTTERFLY.get(), attacker.level());
                butterfly.setMaster(user);
                animal = butterfly;
            }
            default -> JCraft.LOGGER.error("Attempted to create Life Giver entity with invalid LifeGiverType: " + this);
        }

        if (animal == null) {
            JCraft.LOGGER.error("Failed to create animal of type " + ctx.get(TYPE_TO_SUMMON) + " from item " + animalItem);
            return Set.of();
        }
        item.shrink(1);
        animal.moveTo(attacker.getX(), attacker.getY() + 0.5f, attacker.getZ(), attacker.getYRot(), attacker.getXRot());
        animal.setItemInHand(InteractionHand.MAIN_HAND, animalItem);
        attacker.level().addFreshEntity(animal);

        return Set.of();
    }

    @Override
    public void registerContextEntries(MoveContext ctx) {
        ctx.register(TYPE_TO_SUMMON);
    }

    @Override
    protected @NonNull LifeGiverAttack getThis() {
        return this;
    }

    @Override
    public @NonNull LifeGiverAttack copy() {
        return copyExtras(new LifeGiverAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance()));
    }

    public enum LifeGiverType {
        SNAKE,
        FROG,
        BUTTERFLY
    }
}
