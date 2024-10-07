package net.arna.jcraft.common.entity.npc;

import net.arna.jcraft.common.component.living.CommonStandComponent;
import net.arna.jcraft.common.entity.stand.StandType;
import net.arna.jcraft.common.tickable.JEnemies;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.arna.jcraft.registry.JEntityTypeRegistry;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import mod.azure.azurelib.animatable.GeoEntity;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.util.AzureLibUtil;

public class DarbyYoungerEntity extends PathfinderMob implements GeoEntity {

    private final AnimatableInstanceCache geoCache = AzureLibUtil.createInstanceCache(this);

    public DarbyYoungerEntity(Level world) {
        super(JEntityTypeRegistry.DARBY_YOUNGER.get(), world);
        final CommonStandComponent standData = JComponentPlatformUtils.getStandData(this);
        standData.setType(StandType.ATUM);
        standData.setSkin(0);

        if (world.isClientSide()) return;
        JEnemies.add(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // TODO Arna
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    public static AttributeSupplier.Builder createDarbyYoungerAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0).add(Attributes.MOVEMENT_SPEED, 0.5);
    }
}
