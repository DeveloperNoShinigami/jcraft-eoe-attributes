package net.arna.jcraft.common.item;

import mod.azure.azurelib.animatable.GeoItem;
import mod.azure.azurelib.animatable.client.RenderProvider;
import mod.azure.azurelib.constant.DataTickets;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.core.animation.RawAnimation;
import mod.azure.azurelib.core.object.PlayState;
import mod.azure.azurelib.renderer.GeoArmorRenderer;
import mod.azure.azurelib.util.AzureLibUtil;
import net.arna.jcraft.client.renderer.armor.DIOCapeRenderer;
import net.arna.jcraft.client.renderer.armor.JotaroArmorRenderer;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.registry.JItemRegistry;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * {@link ArmorItem} animated by GeckoLib to flutter when its wearer is moving.
 */
public class FlutteringArmorItem extends ArmorItem implements GeoItem {
    private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    public FlutteringArmorItem(ArmorMaterial materialIn, Type slot, Properties builder) {
        super(materialIn, slot, builder);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController(this, "controller", 10, this::predicate));
    }

    private PlayState predicate(AnimationState state) {
        Entity entity = (Entity) state.getData(DataTickets.ENTITY);

        boolean moving = (entity instanceof Player player) ? !JUtils.deltaPos(player).equals(Vec3.ZERO) : entity.getDeltaMovement().horizontalDistanceSqr() > 0.01;
        state.getController().setAnimation(RawAnimation.begin().thenLoop(moving ? "animation.moving" : "animation.idle"));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new RenderProvider() {
            private GeoArmorRenderer<?> renderer;
            @Override public @NotNull HumanoidModel<LivingEntity> getHumanoidArmorModel(
                    LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<LivingEntity> original) {
                if (this.renderer == null) {
                    if (itemStack.is(JItemRegistry.DIO_CAPE.get())) {
                        this.renderer = new DIOCapeRenderer();
                    } else {
                        this.renderer = new JotaroArmorRenderer();
                    }
                }
                renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);
                return renderer;
            }});
    }

    @Override
    public Supplier<Object> getRenderProvider() {
        return renderProvider;
    }
}
