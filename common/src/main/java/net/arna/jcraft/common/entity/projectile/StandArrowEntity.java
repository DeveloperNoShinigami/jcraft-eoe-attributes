package net.arna.jcraft.common.entity.projectile;

import lombok.NonNull;
import mod.azure.azurelib.animatable.GeoEntity;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.util.AzureLibUtil;
import net.arna.jcraft.registry.JEntityTypeRegistry;
import net.arna.jcraft.registry.JItemRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class StandArrowEntity extends AbstractArrow implements GeoEntity {

    private final AnimatableInstanceCache geoCache = AzureLibUtil.createInstanceCache(this);

    public StandArrowEntity(Level level) {
        super(JEntityTypeRegistry.STAND_ARROW_PROJECTILE.get(), level);
    }

    public StandArrowEntity(LivingEntity shooter, Level level) {
        super(JEntityTypeRegistry.STAND_ARROW_PROJECTILE.get(), shooter, level);
    }

    @Override
    protected @NonNull ItemStack getPickupItem() {
        return new ItemStack(JItemRegistry.STAND_ARROW.get());
    }

    @Override
    public void registerControllers(final AnimatableManager.ControllerRegistrar controllers) {
        // TODO Arna
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
}
