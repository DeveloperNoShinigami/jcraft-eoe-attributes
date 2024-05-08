package net.arna.jcraft.common.item;

import net.arna.jcraft.client.registry.JArmorRendererRegistry;
import net.arna.jcraft.client.renderer.armor.DIOArmorRenderer;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class DIOArmorItem extends ArmorItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public DIOArmorItem(ArmorMaterial materialIn, Type slot, Settings builder) {
        super(materialIn, slot, builder);
    }

    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    @Override
    public void createRenderer(Consumer<Object> consumer) {
        JArmorRendererRegistry.createRenderer(consumer, new DIOArmorRenderer());
    }

    @Override
    public Supplier<Object> getRenderProvider() {
        return this.renderProvider;
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController(this, "controller", 20, this::predicate));
    }

    private PlayState predicate(AnimationState animationState) {
        return PlayState.STOP;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
