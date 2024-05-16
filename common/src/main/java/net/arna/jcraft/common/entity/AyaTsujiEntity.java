package net.arna.jcraft.common.entity;

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
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.core.animation.RawAnimation;
import mod.azure.azurelib.core.object.PlayState;
import mod.azure.azurelib.util.AzureLibUtil;
import mod.azure.azurelib.core.animatable.GeoAnimatable;

public class AyaTsujiEntity extends PathfinderMob implements GeoEntity {

    private final AnimatableInstanceCache geoCache = AzureLibUtil.createInstanceCache(this);

    public AyaTsujiEntity(Level world) {
        super(JEntityTypeRegistry.AYA_TSUJI.get(), world);
        JEnemies.add(this);
        final CommonStandComponent standData = JComponentPlatformUtils.getStandData(this);
        standData.setType(StandType.CINDERELLA);
        standData.setSkin(0);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // TODO Arna
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    public static AttributeSupplier.Builder createAyaTsujiAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0).add(Attributes.MOVEMENT_SPEED, 0.5);
    }
}
