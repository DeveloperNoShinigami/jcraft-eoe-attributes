package net.arna.jcraft.common.entity;

import net.arna.jcraft.common.tickable.JEnemies;
import net.arna.jcraft.registry.JEntityTypeRegistry;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class PetshopEntity extends PathAwareEntity implements GeoEntity {

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public PetshopEntity(World world) {
        super(JEntityTypeRegistry.PETSHOP.get(), world);
        JEnemies.add(this);
        // TODO Ayutac set standtype to Horus
    }

    public static DefaultAttributeContainer.Builder createPetshopAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0).add(EntityAttributes.GENERIC_FLYING_SPEED, 1.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.375);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // TODO Arna
    }
}
