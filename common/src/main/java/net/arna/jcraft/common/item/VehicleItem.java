package net.arna.jcraft.common.item;

import lombok.NonNull;
import net.arna.jcraft.common.entity.vehicle.AbstractGroundVehicleEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public final class VehicleItem<T extends AbstractGroundVehicleEntity> extends Item {
    public static final int PLACEMENT_COOLDOWN = 10;

    @FunctionalInterface
    public interface VehicleCreator<T> {
        T create(Level level, Entity owner);
    }

    private final VehicleCreator<T> creator;

    public VehicleItem(Properties properties, VehicleCreator<T> creator) {
        super(properties);
        this.creator = creator;
    }

    public boolean create(@NonNull Level level, @Nullable Player owner, @NonNull Vec3 position, @NonNull ItemStack itemStack) {
        if (level.isClientSide) return false;

        final boolean hasOwner = owner != null;
        final T vehicle = creator.create(level, owner);

        vehicle.setPos(position);

        if (hasOwner) {
            vehicle.setXRot(owner.getXRot());
        }

        if (level.addFreshEntity(vehicle)) {
            if (hasOwner) {
                owner.getCooldowns().addCooldown(this, PLACEMENT_COOLDOWN);
                itemStack.shrink(1);
            }
            return true;
        }

        return false;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return create(context.getLevel(), context.getPlayer(), context.getClickLocation(), context.getItemInHand()) ?
                InteractionResult.CONSUME :
                InteractionResult.PASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        final ItemStack itemStack = player.getItemInHand(usedHand);

        return create(level, player, player.position(), itemStack) ?
                InteractionResultHolder.consume(itemStack) :
                InteractionResultHolder.pass(itemStack);
    }
}
