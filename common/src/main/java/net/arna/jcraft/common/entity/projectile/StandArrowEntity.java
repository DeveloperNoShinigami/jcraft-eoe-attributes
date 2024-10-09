package net.arna.jcraft.common.entity.projectile;

import lombok.NonNull;
import mod.azure.azurelib.animatable.GeoEntity;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.util.AzureLibUtil;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.component.living.CommonStandComponent;
import net.arna.jcraft.common.entity.stand.StandType;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.arna.jcraft.registry.JEntityTypeRegistry;
import net.arna.jcraft.registry.JItemRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

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

    @Override
    protected void onHitEntity(final @NonNull EntityHitResult result) {
        super.onHitEntity(result);
        if (result.getEntity() instanceof final LivingEntity mob) {
            if (JUtils.getStand(mob) == null) {
                final CommonStandComponent standData = JComponentPlatformUtils.getStandData(mob);
                standData.setType(StandType.getRandomRegular(random));
                mob.unRide();
                JCraft.summon(mob.level(), mob);
            }
            else if (mob instanceof final Player player) {
                player.addItem(new ItemStack(JItemRegistry.STAND_ARROW.get()));
            }
        }
    }
}
